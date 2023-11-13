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
	public List<ArrayList<Movimento>> listaInAnni;
	
	public Conto(String titolare) {
		
        this.titolare = titolare;
        this.dataApertura = LocalDate.of(2021,01,01);
        this.listaMovimenti = new ArrayList<Movimento>();
        this.tassoDiInteresse= 0.07;
        
    }
	
	

	
	public void generaInteressi() {

		// inizializzo anno
		int year = 2020;

		// lista contenente liste di movimenti divise per anno
		this.listaInAnni = new ArrayList<>();

		// liste di movimenti divise per anno
		ArrayList<Movimento> listaAnno1 = new ArrayList<>(listaMovimenti.stream()
				.filter(movimento -> movimento.getDataOperazione().getYear() == 2021).collect(Collectors.toList()));
		ArrayList<Movimento> listaAnno2 = new ArrayList<>(listaMovimenti.stream()
				.filter(movimento -> movimento.getDataOperazione().getYear() == 2022).collect(Collectors.toList()));
		ArrayList<Movimento> listaAnno3 = new ArrayList<>(listaMovimenti.stream()
				.filter(movimento -> movimento.getDataOperazione().getYear() == 2023).collect(Collectors.toList()));

		// aggiunte alla lista generica
		listaInAnni.add(listaAnno1);
		listaInAnni.add(listaAnno2);
		listaInAnni.add(listaAnno3);

		double interessiAnniPrecedenti = 0;
		double interesseTotale = 0;
		double interesseTemporaneo = 0;

		double saldoPeriodo = 0;
		long periodo = 0;
		double interesseGiornaliero = 0;

		double saldoAlCambioAnno = 0;
		double interessiNetti = 0;

		// ciclo su ogni lista
		for (ArrayList<Movimento> listaAnno : listaInAnni) {

			// verifico che la data abbia transazioni e che l anno è diverso dalla data
			// attuale
			if (!listaAnno.isEmpty() && listaAnno.get(0).getDataOperazione().getYear() != LocalDate.now().getYear()) {

				// prendo l ultimo movimento che mi serve per accedere al saldo
				Movimento ultimoMovimento = listaAnno.get(listaAnno.size() - 1);

				double SaldoUltimoMOvimento = ultimoMovimento.getSaldoDopoOperazione();

				// aggiungo un anno ogni iterazione del ciclo
				year++;

				// creo movimento a inizio anno (serve soprattutto nel 2022 o 2023)
				Movimento movimentoInizioAnno = new Movimento(LocalDate.of(year, 1, 1), TipoOperazione.VERSAMENTO, 0);
				// creo movimento a fine anno per calcolare gli interessi arrivando fino a
				// quella data
				Movimento movimentoFineAnno = new Movimento(LocalDate.of(year, 12, 31), TipoOperazione.VERSAMENTO, 0);

				System.out.println("Movimenti dopo il sort:");

				// aggiungo i movimenti
				listaAnno.add(movimentoInizioAnno);
				movimentoInizioAnno.saldoDopoOperazione = listaAnno.get(0).getSaldoDopoOperazione();

				// in questo caso serve assegnare il valore dell ultimo movimento come saldo
				// dopo operazione per poter calcolare gli interessi
				listaAnno.add(movimentoFineAnno);
				movimentoFineAnno.saldoDopoOperazione = SaldoUltimoMOvimento;

				// qui li ordino per data
				Collections.sort(listaAnno);

				listaAnno.forEach(movimento -> System.out.println(movimento.getDataOperazione() + " - "
						+ movimento.getTipoOperazione() + " - " + movimento.getImportoOperazione()));

				// ciclo sugli elementi della sottolista
				for (int i = 1; i < listaAnno.size(); i++) {

					// movimento corrente
					Movimento mov = listaAnno.get(i);
					// mmovimento precedente
					Movimento movPrecedente = listaAnno.get(i - 1);

					// il saldo in quel periodo è determinato dal movimento attuale

					saldoPeriodo = movPrecedente.saldoDopoOperazione + interessiAnniPrecedenti;

					System.out.println("saldo periodo " + saldoPeriodo);

					// conto i giorni dalla transazione precendente a quella attuale
					periodo = ChronoUnit.DAYS.between(movPrecedente.getDataOperazione(), mov.getDataOperazione());

					System.out.println("periodo " + periodo);

					// interesse giornaliero
					interesseGiornaliero = (saldoPeriodo * this.tassoDiInteresse) / 365.0;

					System.out.println("saldoPeriodo " + saldoPeriodo);

					System.out.println("interesse giornaliero " + interesseGiornaliero);

					// interesse temporaneo maturato
					interesseTemporaneo += interesseGiornaliero * periodo;

					System.out.println("interesse temporaneo " + interesseTemporaneo);

					System.out.println("saldo = " + saldoPeriodo);

				}

				interesseTotale += interesseTemporaneo;

				interessiNetti = interesseTotale - (interesseTotale * 0.26);

				saldoPeriodo += interessiNetti;

				interessiAnniPrecedenti += interessiNetti;

				System.out.println("saldo dopo interessi = " + saldoPeriodo + "interessi netti maturati nell anno "
						+ year + " = " + interessiNetti);

				interesseTemporaneo = 0;

				saldoAlCambioAnno = saldoPeriodo;

				System.out.println(saldoAlCambioAnno);

				System.out.println("fine");

				generaEstrattoconto(listaAnno, year, saldoPeriodo, interessiAnniPrecedenti, interesseTotale,
						interessiNetti);

				interesseTotale = 0;

				// se siamo nell anno corrente
			} else if (!listaAnno.isEmpty()
					&& listaAnno.get(0).getDataOperazione().getYear() == LocalDate.now().getYear()) {

				// prendo l ultimo movimento che mi serve per accedere al saldo
				Movimento ultimoMovimento = listaAnno.get(listaAnno.size() - 1);

				double SaldoUltimoMOvimento = ultimoMovimento.getSaldoDopoOperazione();

				// aggiungo un anno ogni iterazione del ciclo
				year++;

				// creo movimento a inizio anno (serve soprattutto nel 2022 o 2023)
				Movimento movimentoInizioAnno = new Movimento(LocalDate.of(year, 1, 1), TipoOperazione.VERSAMENTO, 0);
				// creo movimento in data attuale per calcolare gli interessi arrivando fino a
				// quella data

				Movimento movimentoInDataAttuale = new Movimento(LocalDate.now(), TipoOperazione.VERSAMENTO, 0);
				movimentoInDataAttuale.saldoDopoOperazione = SaldoUltimoMOvimento;

				System.out.println("Movimenti dopo il sort:");

				// aggiungo i movimenti
				listaAnno.add(movimentoInizioAnno);
				movimentoInizioAnno.saldoDopoOperazione = listaAnno.get(0).getSaldoDopoOperazione();

				// in questo caso serve assegnare il valore dell ultimo movimento come saldo
				// dopo operazione per poter calcolare gli interessi
				listaAnno.add(movimentoInDataAttuale);
				movimentoInDataAttuale.saldoDopoOperazione = SaldoUltimoMOvimento;

				// qui li ordino per data
				Collections.sort(listaAnno);

				listaAnno.forEach(movimento -> System.out.println(movimento.getDataOperazione() + " - "
						+ movimento.getTipoOperazione() + " - " + movimento.getImportoOperazione()));

				// ciclo sugli elementi della sottolista
				for (int i = 1; i < listaAnno.size(); i++) {

					// movimento corrente
					Movimento mov = listaAnno.get(i);
					// mmovimento precedente
					Movimento movPrecedente = listaAnno.get(i - 1);

					// il saldo in quel periodo è determinato dal movimento attuale

					saldoPeriodo = movPrecedente.saldoDopoOperazione + interessiAnniPrecedenti;

					System.out.println("saldo periodo " + saldoPeriodo);

					// conto i giorni dalla transazione precendente a quella attuale
					periodo = ChronoUnit.DAYS.between(movPrecedente.getDataOperazione(), mov.getDataOperazione());

					System.out.println("periodo " + periodo);

					// interesse giornaliero
					interesseGiornaliero = (saldoPeriodo * this.tassoDiInteresse) / 365.0;

					System.out.println("saldoPeriodo " + saldoPeriodo + "tassodiIntersee" + this.tassoDiInteresse);

					System.out.println("interesse giornaliero " + interesseGiornaliero);

					// interesse temporaneo maturato
					interesseTemporaneo += interesseGiornaliero * periodo;

					System.out.println("interesse temporaneo " + interesseTemporaneo);

					System.out.println("saldo = " + saldoPeriodo);

				}

				//interesse Lordo 
				interesseTotale += interesseTemporaneo;
				
				//netto
				interessiNetti = interesseTotale - (interesseTotale * 0.26);

				//IL SALDO DI QUEL PERIODO DIVENTA += AGLI INTERESSI NETTI 
				saldoPeriodo += interessiNetti;

				//interessi dell anno precedente interessiNetti
				interessiAnniPrecedenti += interessiNetti;

				System.out.println("saldo dopo interessi = " + saldoPeriodo + "interessi netti maturati nell anno "
						+ year + " = " + interessiNetti);

				interesseTemporaneo = 0;

				saldoAlCambioAnno = saldoPeriodo;

				System.out.println(saldoAlCambioAnno);

				System.out.println("fine");

				generaEstrattoconto(listaAnno, year, saldoPeriodo, interessiAnniPrecedenti, interesseTotale,
						interessiNetti);

				interesseTotale = 0;

			} else {
				year++;

				interesseGiornaliero = (saldoAlCambioAnno * this.tassoDiInteresse) / 365;

				System.out.println("tasso di interesse giornaliero " + interesseGiornaliero);

				interesseTotale = interesseGiornaliero * 365;

				System.out.println("tasso di interesse t " + interesseTotale);

				interessiAnniPrecedenti += interesseTotale;

			}
		}

		this.saldo += interessiAnniPrecedenti;

		System.out.println();

	}
	

	public void generaEstrattoconto(ArrayList<Movimento> listaAnno, int year ,double saldoPeriodo ,double interessiAnniPrecedenti ,double interesseTotale, double interessiNetti) {
		
			System.out.println();
			System.out.println( "|Estratto conto di" + this.titolare + " " + year);
			System.out.println();
			System.out.println("|Data Operazione   |" + " TipoOperazione    " + "| Importo   "  + "| Saldo Parziale");
			System.out.println();
			for (int i = 1; i < listaAnno.size(); i++) {
				if(year > 2021) {
					System.out.println("|"+ listaAnno.get(i).getDataOperazione() + "        | " +  listaAnno.get(i).getTipoOperazione().toString() + "        | " 
					+ listaAnno.get(i).getImportoOperazione() + "         | " + (listaAnno.get(i).saldoDopoOperazione +(interessiAnniPrecedenti - interessiNetti) ));
					System.out.println((listaAnno.get(i).saldoDopoOperazione +(interessiAnniPrecedenti - interessiNetti) ));
				}else {
					System.out.println("|"+ listaAnno.get(i).getDataOperazione() + "        | " +  listaAnno.get(i).getTipoOperazione().toString() + "       | " 
					+ listaAnno.get(i).getImportoOperazione() + "         | " + listaAnno.get(i).saldoDopoOperazione);
				}
			}
			System.out.println();
			System.out.println("Saldo Disponibile: " + ( year > 2021 ? (listaAnno.get(listaAnno.size()-1).getSaldoDopoOperazione() + (interessiAnniPrecedenti - interessiNetti) ) : listaAnno.get(listaAnno.size()-1).getSaldoDopoOperazione()) );
			System.out.println("interessi Lordi Maturati:  " + interesseTotale);
			System.out.println("interessi Netti Maturati:  " + interessiNetti);
			System.out.println();
			System.out.println("Saldo finale in data " + listaAnno.get(listaAnno.size()-1).getDataOperazione() + " " + (listaAnno.get(listaAnno.size()-1).getSaldoDopoOperazione() + (interessiAnniPrecedenti) ));
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
		
	    if (dataAttuale.getYear() == LocalDate.now().getYear()) {
	        long giorniMancanti = dataRiferimento.until(LocalDate.now(), ChronoUnit.DAYS);
	        long giorniGenerabili = ThreadLocalRandom.current().nextLong(Math.min(giorniMancanti, 365));

	        return dataRiferimento.plusDays(giorniGenerabili);
	    } else {
	        long giorniMancanti = dataRiferimento.until(dataAttuale, ChronoUnit.DAYS) + 1;
	        long giorniGenerabili = ThreadLocalRandom.current().nextLong(giorniMancanti);

	        return dataRiferimento.plusDays(giorniGenerabili);
	    }
	}
	
}
