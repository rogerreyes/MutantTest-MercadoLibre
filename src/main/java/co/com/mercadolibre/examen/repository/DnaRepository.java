package co.com.mercadolibre.examen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import co.com.mercadolibre.examen.domain.DnaDocument;

@Repository
public interface DnaRepository extends MongoRepository<DnaDocument, String> {
	
	@Query(value = "{isMutant:?0}", count = true)
	public Integer countIsMutant(boolean isMutant);
	
	

}