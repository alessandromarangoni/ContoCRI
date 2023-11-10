package it.betacom.model;

public enum TipoOperazione {
	PRELIEVO("Prelievo"),
	VERSAMENTO("Versamento"),;

	private String tipo;
	
	private TipoOperazione(String tipo) {
		this.tipo = tipo;
	}
	
	
	
}
