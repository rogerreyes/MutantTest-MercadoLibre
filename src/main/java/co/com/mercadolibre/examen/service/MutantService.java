package co.com.mercadolibre.examen.service;

import java.util.Map;

import co.com.mercadolibre.examen.domain.DnaRequest;

public interface MutantService {

	boolean isMutant(DnaRequest request);

	Map<String, Object> getStats();

}
