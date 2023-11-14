package it.betacom.model;

public class contoRisparmio extends Conto {

	public contoRisparmio(String titolare) {
		super(titolare);
        this.tassoDiInteresse = 0.10; 
        this.versamento(1000, 2021);
	}

}
