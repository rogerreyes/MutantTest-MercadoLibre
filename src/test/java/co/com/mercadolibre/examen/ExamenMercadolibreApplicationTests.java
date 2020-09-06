package co.com.mercadolibre.examen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import co.com.mercadolibre.examen.domain.DnaDocument;
import co.com.mercadolibre.examen.domain.DnaRequest;

@TestPropertySource(properties = {
		"MONGO_CLUSTER_URI=mongodb+srv://testUser:xnGdVu3UyilKO662@cluster0.kveqa.mongodb.net/testDB?retryWrites=true&w=majority" })
@SpringBootTest(classes = { ExamenMercadolibreApplication.class })
@AutoConfigureMockMvc
class ExamenMercadolibreApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testStats() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/stats");
		MvcResult result = mockMvc.perform(request).andReturn();
		assertEquals(result.getResponse().getStatus(), 200);
	}

	@Test
	void testHumanSizematriz() throws Exception {
		DnaRequest request = new DnaRequest();
		request.setDna(Arrays.asList("ATG", "CAG", "TTA"));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/mutant")
				.content(new JSONObject(request).toString()).contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(result.getResponse().getStatus(), 403);

	}

	@Test
	void testIsMutant() throws Exception {
		DnaRequest request = new DnaRequest();
		request.setDna(Arrays.asList("ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/mutant")
				.content(new JSONObject(request).toString()).contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(result.getResponse().getStatus(), 200);

	}

	@Test
	void testNoMutant() throws Exception {
		DnaRequest request = new DnaRequest();
		request.setDna(Arrays.asList("ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/mutant")
				.content(new JSONObject(request).toString()).contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(result.getResponse().getStatus(), 403);
	}

	@Test
	void testDnaDocument() {

		String idTest = "m3Rc4D0";
		DnaDocument dnaDocumnet = new DnaDocument();
		dnaDocumnet.setId(idTest);
		dnaDocumnet.setMutant(true);

		assertEquals(dnaDocumnet.getId(), idTest);
		assertTrue(dnaDocumnet.isMutant());
	}

}
