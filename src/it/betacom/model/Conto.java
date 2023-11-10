package it.betacom.model;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;



public abstract class Conto {
	private String titolare;
	private LocalDate dataApertura;
	protected double saldo;
	protected List<Movimento> listaMovimenti;
	protected double tassoDiInteresse;
	
	public Conto(String titolare) {
		
        this.titolare = titolare;
        this.dataApertura = LocalDate.of(2021,01,01);
        this.listaMovimenti = new ArrayList<Movimento>();
        this.tassoDiInteresse= 0.07;
    }
	
	

	
	
	public void generaInteressi() {
		
		//inizializzo anno
		int year = 2020;

		//lista contenente liste di movimenti divise per anno
		List<ArrayList<Movimento>> listaGenerica = new ArrayList<>();
		
		//liste di movimenti divise per anno
		ArrayList<Movimento> listaAnno1 = new ArrayList<>(listaMovimenti.stream()
				.filter(movimento -> movimento.getDataOperazione().getYear() == 2021).collect(Collectors.toList()));
		ArrayList<Movimento> listaAnno2 = new ArrayList<>(listaMovimenti.stream()
				.filter(movimento -> movimento.getDataOperazione().getYear() == 2022).collect(Collectors.toList()));
		ArrayList<Movimento> listaAnno3 = new ArrayList<>(listaMovimenti.stream()
				.filter(movimento -> movimento.getDataOperazione().getYear() == 2023).collect(Collectors.toList()));

		//aggiunte alla lista generica
		listaGenerica.add(listaAnno1);
		listaGenerica.add(listaAnno2);
		listaGenerica.add(listaAnno3);

		double interesseTotale = 0;
		double interesseTemporaneo = 0;
		double saldoPeriodo = 0;
		long periodo = 0;
		double interesseGiornaliero = 0;

		//ciclo su ogni lista
		for (ArrayList<Movimento> listaAnno : listaGenerica) {

			//verifico che la data abbia transazioni e che l anno è diverso dalla data attuale
			if (!listaAnno.isEmpty() && listaAnno.get(0).getDataOperazione().getYear() != LocalDate.now().getYear()) {

				//prendo l ultimo movimento che mi serve per accedere al saldo
				Movimento ultimoMovimento = listaAnno.get(listaAnno.size() - 1);
				double SaldoUltimoMOvimento = ultimoMovimento.getSaldoDopoOperazione();

				//aggiungo un anno ogni iterazione del ciclo 
				year++;
				
				//creo movimento a inizio anno (serve soprattutto nel 2022 o 2023)
				Movimento movimentoInizioAnno = new Movimento(LocalDate.of(year, 1, 1), TipoOperazione.VERSAMENTO, 0);
				//creo movimento a fine anno per calcolare gli interessi arrivando fino a quella data
				Movimento movimentoFineAnno = new Movimento(LocalDate.of(year, 12, 31), TipoOperazione.VERSAMENTO, 0);

				System.out.println("Movimenti dopo il sort:");
				
				//aggiungo i movimenti
				listaAnno.add(movimentoInizioAnno);
				
				movimentoInizioAnno.saldoDopoOperazione = listaAnno.get(0).getSaldoDopoOperazione();;

				//in questo caso serve assegnare il valore dell ultimo movimento come saldo dopo operazione per poter calcolare gli interessi
				listaAnno.add(movimentoFineAnno);
				movimentoFineAnno.saldoDopoOperazione = SaldoUltimoMOvimento;

				//qui li ordino per data
				Collections.sort(listaAnno);

				
				listaAnno.forEach(movimento -> System.out.println(movimento.getDataOperazione() + " - "
						+ movimento.getTipoOperazione() + " - " + movimento.getImportoOperazione()));

				//ciclo sugli elementi della sottolista
				for (int i = 1; i < listaAnno.size(); i++) {

					//movimento corrente 
					Movimento mov = listaAnno.get(i);
					//mmovimento precedente
					Movimento movPrecedente = listaAnno.get(i - 1);
					//il saldo in quel periodo è determinato dal movimento attuale
					saldoPeriodo = mov.saldoDopoOperazione;

					System.out.println("saldo periodo " + saldoPeriodo);

					periodo = ChronoUnit.DAYS.between(movPrecedente.getDataOperazione(), mov.getDataOperazione());

					System.out.println("periodo " + periodo);

					interesseGiornaliero = (saldoPeriodo * this.tassoDiInteresse) / 365.0;

					System.out.println("saldoPeriodo " + saldoPeriodo + "tassodiIntersee" + this.tassoDiInteresse);

					System.out.println("interesse giornaliero " + interesseGiornaliero);

					interesseTemporaneo += interesseGiornaliero * periodo;

					System.out.println("interesse temporaneo " + interesseTemporaneo);

				}

				System.out.println("fine");

			} else if(!listaAnno.isEmpty() && listaAnno.get(0).getDataOperazione().getYear() == LocalDate.now().getYear()){
				
				
				Movimento ultimoMovimentoEffettuato = listaAnno.get(listaAnno.size() - 1);
				double SaldoUltimoMOvimento = ultimoMovimentoEffettuato.getSaldoDopoOperazione();
				
				Movimento movimentoInizioAnno = new Movimento(LocalDate.of(year, 1, 1), TipoOperazione.VERSAMENTO, 0);
				
				
				Movimento movimentoInDataAttuale = new Movimento(LocalDate.now(), TipoOperazione.VERSAMENTO, 0);
				movimentoInDataAttuale.saldoDopoOperazione = SaldoUltimoMOvimento;
				
				
				year++;
				
				
				
				System.out.println("la lista è vuota");
			}
			DecimalFormat df = new DecimalFormat("#0.00");
			System.out.println("Interesse maturato nell'anno " + (year) + ": " + df.format(interesseTemporaneo));
			
			interesseTotale += interesseTemporaneo;
			interesseTemporaneo = 0;
			this.saldo += interesseTotale;

			

		}

		System.out.println();

	}


	public String getTitolare() {
		return titolare;
	}


	public void setTitolare(String titolare) {
		this.titolare = titolare;
	}


	public LocalDate getDataApertura() {
		return dataApertura;
	}


	public void setDataApertura(LocalDate dataApertura) {
		this.dataApertura = dataApertura;
	}


	public double getSaldo() {
		return saldo;
	}


	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}
	
	
	public void prelievo(double importo,int year) {
	    if (importo <= saldo) {
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

	public void versamento(double importo,int year) {
	    saldo += importo;

	    LocalDate data;
	    
	    if (listaMovimenti.isEmpty()) {
	        data = LocalDate.of(2021, 1, 1);
	    } else {
	        LocalDate ultimaData = listaMovimenti.get(listaMovimenti.size() - 1).getDataOperazione();
	        data = generaData(ultimaData, LocalDate.of(year, 12 ,31 ));
	    }

	    Movimento movimento = new Movimento(data, TipoOperazione.VERSAMENTO, importo);
	    movimento.saldoDopoOperazione = this.saldo;
	    listaMovimenti.add(movimento);
	}
	
	
	public List<Movimento> getListaMovimenti() {
		return listaMovimenti;
	}


	public void setListaMovimenti(List<Movimento> listaMovimenti) {
		this.listaMovimenti = listaMovimenti;
	}


	public double getTassoDiInteresse() {
		return tassoDiInteresse;
	}
	
	
	private LocalDate generaData(LocalDate dataRiferimento, LocalDate dataAttuale) {
	    long dataDiRiferimento = dataRiferimento.until(dataAttuale, ChronoUnit.DAYS) + 1;
	    long GiorniAggiunti = ThreadLocalRandom.current().nextLong(dataDiRiferimento);

	    return dataRiferimento.plusDays(GiorniAggiunti);
	}

	
}
