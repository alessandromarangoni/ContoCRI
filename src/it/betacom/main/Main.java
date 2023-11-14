package it.betacom.main;

import java.util.ArrayList;
import java.util.List;

import it.betacom.model.Conto;
import it.betacom.model.ContoCorrente;
import it.betacom.model.ContoFactory;
import it.betacom.model.Movimento;
import it.betacom.model.contoRisparmio;

public class Main {

    public static void main(String[] args) {
        ContoFactory contoFactory = new ContoFactory();
        
        // Creazione di un conto corrente
        Conto contoCorrente = contoFactory.generaConto("ContoCorrente", "Mario Rossi");
        Conto contoRisparmio = contoFactory.generaConto("ContoRisparmio", "Giuseppe Benedetti");
        Conto contoInvestimenti = contoFactory.generaConto("contoInvestimenti", "Marco invesstitore");
        
        for (int i = 0; i < 3; i++) {
            contoCorrente.versamento(0, 2021);
            contoRisparmio.versamento(0, 2021);
            contoInvestimenti.versamento(0, 2021);
        }
        
        for (int i = 0; i < 3; i++) {
            contoCorrente.versamento(10, 2022);
            contoRisparmio.versamento(10, 2022);
            contoInvestimenti.versamento(0, 2022);
        }
        
        for (int i = 0; i < 3; i++) {
            contoCorrente.versamento(0, 2023);
            contoRisparmio.versamento(10, 2023);
            contoInvestimenti.versamento(0, 2023);
        }

        
        contoCorrente.generaInteressi();
        contoRisparmio.generaInteressi();
        contoInvestimenti.generaInteressi();
        
//        for (int i = 0; i < contoCorrente.getListaMovimenti().size(); i++) {
//        	System.out.println(contoCorrente.getListaMovimenti().get(i).getDataOperazione());
//        }
        
   
    }
}
