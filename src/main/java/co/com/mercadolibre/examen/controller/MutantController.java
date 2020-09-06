package co.com.mercadolibre.examen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import co.com.mercadolibre.examen.domain.DnaRequest;
import co.com.mercadolibre.examen.service.MutantService;

@RestController
public class MutantController {

	@Autowired
	private MutantService mutantService;

	@PostMapping(value = "/mutant", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> isMuant(@RequestBody DnaRequest request) {
		HttpStatus status = mutantService.isMutant(request)?HttpStatus.OK:HttpStatus.FORBIDDEN;
		return new ResponseEntity<>(status);
	}

	@GetMapping("/stats")
	public ResponseEntity<Object> getStats() {
		return new ResponseEntity<>(mutantService.getStats(), HttpStatus.OK);
	}
}
