package it.betacom.model;

import java.time.LocalDate;

public class contoRisparmio extends Conto {

	public contoRisparmio(String titolare) {
		super(titolare);
        this.tassoDiInteresse = 0.10; 
        this.versamento(1000, 2021);
	}

	@Override
	public void prelievo(double importo,int year) {
	    if (importo <= saldo && importo < 1000 ) {
	        LocalDate data;
	        
	        if (listaMovimenti.isEmpty()) {
	            data = LocalDate.of(2021, 1, 1);
	        } else {
	            LocalDate ultimaData = listaMovimenti.get(listaMovimenti.size() - 1).getDataOperazione();
	            data = generaData(ultimaData, LocalDate.of(year, 12 ,31 ));
	        }
	        
	        this.saldo -= importo;

	        Movimento movimento = new Movimento(data, TipoOperazione.PRELIEVO, importo);
	        movimento.setSaldoDopoOperazione(this.saldo);
	        listaMovimenti.add(movimento);
	    } else {
	        System.out.println("Fondi non Sufficenti");
	    }
	}
}
