package co.com.mercadolibre.examen.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.mercadolibre.examen.domain.DnaDocument;
import co.com.mercadolibre.examen.domain.DnaRequest;
import co.com.mercadolibre.examen.exception.HumanExeption;
import co.com.mercadolibre.examen.repository.DnaRepository;

@Service
public class MutantServiceImp implements MutantService {

	@Autowired
	private DnaRepository dnaRepository;

	private static final Pattern regexPattern = Pattern.compile("(A{4}|T{4}|C{4}|G{4})");

	@Override
	public boolean isMutant(DnaRequest request) {

		String joinDNA = String.join("|", request.getDna());
		String Dna256Key = encryptSHA256(joinDNA);
		try {

			Integer matches = 0;
			int size = request.getDna().size();

			// Evaluar mutantes mayor a 3 * 3
			if (size < 4) {
				dnaRepository.save(new DnaDocument(Dna256Key, false));
				return false;
			}

			matches = validateDNA(Dna256Key, joinDNA, matches);
			if (matches > 1) {
				throw new HumanExeption();
			}

			int diag = (2 * size) - 1;
			String[][] a = request.getDna().stream().map(str -> str.split("")).toArray(String[][]::new);
			String[][] AT = new String[size][size];
			String[][] D1 = new String[diag][size];
			String[][] D2 = new String[diag][size];

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					AT[i][j] = a[j][i];
					D1[i + j][j] = a[i][j];
					D2[size - 1 + i - j][j] = a[j][i];
				}
			}

			String joinAT = Arrays.stream(AT).map(arr -> String.join("", arr)).collect(Collectors.joining("|"));
			matches = validateDNA(Dna256Key, joinAT, matches);
			if (matches > 1) {
				throw new HumanExeption();
			}

			String joinD1 = Arrays.stream(D1).map(arr -> String.join("", arr)).collect(Collectors.joining("|"));
			matches = validateDNA(Dna256Key, joinD1, matches);
			if (matches > 1) {
				throw new HumanExeption();
			}

			String joinD2 = Arrays.stream(D2).map(arr -> String.join("", arr)).collect(Collectors.joining("|"));
			matches = validateDNA(Dna256Key, joinD2, matches);
			if (matches > 1) {
				throw new HumanExeption();
			}

			dnaRepository.save(new DnaDocument(Dna256Key, false)).getId();
			return false;
		} catch (HumanExeption humanExeption) {
			dnaRepository.save(new DnaDocument(Dna256Key, true));
			return true;
		}
	}

	private Integer validateDNA(String Dna256Key, CharSequence joinDNA, Integer matches) {
		Matcher matcher = regexPattern.matcher(joinDNA);
		while (matcher.find() & matches < 2) {
			matches++;
		}
		return matches;
	}

	private static String encryptSHA256(String data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : encodedhash) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
		}
		return "UNDEFINED_KEY";
	}

	@Override
	public Map<String, Object> getStats() {		
		Integer countMutant = dnaRepository.countIsMutant(true);
		Integer countHuman = dnaRepository.countIsMutant(false);
		double ratio = countHuman > 0 ? countMutant / (double) countHuman : 0.0;
		JSONObject response = new JSONObject().put("count_mutant_dna", countMutant).put("count_human_dna", countHuman)
				.put("ratio", ratio);
		return response.toMap();
	}
}
