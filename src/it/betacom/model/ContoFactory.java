package it.betacom.model;

import java.time.LocalDate;

public class ContoFactory {
    public Conto generaConto(String tipoConto, String titolare) {
        if ("ContoCorrente".equalsIgnoreCase(tipoConto)) {
            return new ContoCorrente(titolare);
        } else if ("AltroTipoDiConto".equalsIgnoreCase(tipoConto)) {
//            return new AltroTipoDiConto(titolare, dataApertura, saldo, tassoDiInteresse);
        }

        return null;
    }
}

