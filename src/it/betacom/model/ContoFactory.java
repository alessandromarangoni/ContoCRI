package it.betacom.model;

import java.time.LocalDate;

public class ContoFactory {
    public Conto generaConto(String tipoConto, String titolare) {
        if ("ContoCorrente".equalsIgnoreCase(tipoConto)) {
            return new ContoCorrente(titolare);
        } else if ("ContoRisparmio".equalsIgnoreCase(tipoConto)) {
           return new contoRisparmio(titolare);
        }else if ("ContoInvestimenti".equalsIgnoreCase(tipoConto)) {
            return new ContoInvestimenti(titolare);
         }

        return null;
    }
    
}

