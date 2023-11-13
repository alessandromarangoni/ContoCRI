package it.betacom.main;

import java.util.ArrayList;
import java.util.List;

import it.betacom.model.Conto;
import it.betacom.model.ContoCorrente;
import it.betacom.model.ContoFactory;
import it.betacom.model.Movimento;

public class Main {

    public static void main(String[] args) {
        ContoFactory contoFactory = new ContoFactory();

        // Creazione di un conto corrente
        Conto contoCorrente = contoFactory.generaConto("ContoCorrente", "Mario Rossi");

        for (int i = 0; i < 3; i++) {
            contoCorrente.versamento(0, 2021);
        }

        for (int i = 0; i < 3; i++) {
            contoCorrente.versamento(10, 2022);
        }
        
        for (int i = 0; i < 3; i++) {
            contoCorrente.versamento(0, 2023);
        }

        contoCorrente.generaInteressi();
        
        System.out.println(" saldo " + contoCorrente.getSaldo() + " titolare " + contoCorrente.getTitolare() + " tasso "
                + contoCorrente.getTassoDiInteresse() + " apertura conto " + contoCorrente.getDataApertura());

        for (Movimento movimento : contoCorrente.getListaMovimenti()) {
            System.out.println(movimento.getDataOperazione());
        }
    }
}
