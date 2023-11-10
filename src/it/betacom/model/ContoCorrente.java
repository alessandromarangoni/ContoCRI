package it.betacom.model;


public class ContoCorrente extends Conto {

    public ContoCorrente(String titolare) {
        super(titolare);
        this.tassoDiInteresse = 0.07; 
        this.versamento(1000, 2021);
    }

    
}
