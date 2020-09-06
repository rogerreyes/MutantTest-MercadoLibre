package co.com.mercadolibre.examen.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DnaDocument {

	@Id
	private String id;
	private boolean isMutant;

	public DnaDocument() {
	}

	public DnaDocument(String id, boolean isMutant) {
		super();
		this.id = id;
		this.isMutant = isMutant;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isMutant() {
		return isMutant;
	}

	public void setMutant(boolean isMutant) {
		this.isMutant = isMutant;
	}

}
