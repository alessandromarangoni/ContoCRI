package it.betacom.model;

import java.util.Random;

public class ContoInvestimenti extends Conto {

	public ContoInvestimenti(String titolare) {
		
		super(titolare);
		this.versamento(1000, 2021);
	}
	
	@Override
    public double getTassoDiInteresse() {
		
		// Crea un oggetto Random
        Random random = new Random();

        // Genera un numero casuale tra -1 e 1
        double tassoDiInteresse = -1 + (random.nextDouble() * 2);
        System.out.println();
        return tassoDiInteresse;
	}
	
}
