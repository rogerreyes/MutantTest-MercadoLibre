package co.com.mercadolibre.examen.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Pattern regexPattern = Pattern.compile("([Aa]{4}|[Tt]{4}|[Cc]{4}|[Gg]{4})");
	
	Logger logger = LoggerFactory.getLogger(MutantServiceImp.class);

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
				logger.info(String.format("Humano Encontrado %s", Dna256Key));
				return false;
			}

			matches = validateDNA(Dna256Key, joinDNA, matches);
			if (matches > 1) {
				throw new HumanExeption();
			}

			int diagonalSize = (2 * size) - 1;
			String[][] original = request.getDna().stream().map(str -> str.split("")).toArray(String[][]::new);
			String[][] traspuesta = new String[size][size];
			String[][] diagonal1 = new String[diagonalSize][size];
			String[][] diagonal2 = new String[diagonalSize][size];

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					traspuesta[i][j] = original[j][i];
					diagonal1[i + j][j] = original[i][j];
					diagonal2[size - 1 + i - j][j] = original[j][i];
				}
			}

			String joinTraspuesta = Arrays.stream(traspuesta).map(arr -> String.join("", arr)).collect(Collectors.joining("|"));
			matches = validateDNA(Dna256Key, joinTraspuesta, matches);
			if (matches > 1) {
				throw new HumanExeption();
			}

			String joinDiag1 = Arrays.stream(diagonal1).map(arr -> String.join("", arr)).collect(Collectors.joining("|"));
			matches = validateDNA(Dna256Key, joinDiag1, matches);
			if (matches > 1) {
				throw new HumanExeption();
			}

			String joinDiag2 = Arrays.stream(diagonal2).map(arr -> String.join("", arr)).collect(Collectors.joining("|"));
			matches = validateDNA(Dna256Key, joinDiag2, matches);
			if (matches > 1) {
				throw new HumanExeption();
			}

			dnaRepository.save(new DnaDocument(Dna256Key, false)).getId();
			logger.info(String.format("Humano Encontrado %s", Dna256Key));
			return false;
		} catch (HumanExeption humanExeption) {
			dnaRepository.save(new DnaDocument(Dna256Key, true));
			logger.info(String.format("Mutante Encontrado %s", Dna256Key));
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

	private String encryptSHA256(String data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : encodedhash) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error("Error generando hash SHA-256 key");
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

	@Override
	public void resetStats() {
		dnaRepository.deleteAll();
	}
}
