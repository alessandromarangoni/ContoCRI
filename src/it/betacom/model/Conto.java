package it.betacom.model;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.concurrent.ThreadLocalRandom;

import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;



public abstract class Conto {
	private String titolare;
	private LocalDate dataApertura;
	protected double saldo;
	protected List<Movimento> listaMovimenti;
	protected double tassoDiInteresse;
	protected static final Logger logger = (Logger) LogManager.getLogger("Conto");
	
	public Conto(String titolare) {
		
        this.titolare = titolare;
        this.dataApertura = LocalDate.of(2021,01,01);
        this.listaMovimenti = new ArrayList<Movimento>();
        this.tassoDiInteresse= 0.07;
        
    }
	
	public void generaInteressi() {
		
		List<ArrayList<Movimento>> listaInAnni;
		
		// inizializzo anno
		int year = 2020;
		
		// lista contenente liste di movimenti divise per anno
		listaInAnni = new ArrayList<>();

		//
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
				// creo movimento a fine anno per calcolare gli interessi arrivando fino a quella data
				Movimento movimentoFineAnno = new Movimento(LocalDate.of(year, 12, 31), TipoOperazione.VERSAMENTO, 0);
				// aggiungo i movimenti
				listaAnno.add(movimentoInizioAnno);
				movimentoInizioAnno.saldoDopoOperazione = listaAnno.get(0).getSaldoDopoOperazione();
				// in questo caso serve assegnare il valore dell ultimo movimento come saldo
				// dopo operazione per poter calcolare gli interessi
				listaAnno.add(movimentoFineAnno);
				movimentoFineAnno.saldoDopoOperazione = SaldoUltimoMOvimento;
				// qui li ordino per data
				Collections.sort(listaAnno);

				// ciclo sugli elementi della sottolista
					for (int i = 1; i < listaAnno.size(); i++) {
						// movimento corrente
						Movimento mov = listaAnno.get(i);
						// mmovimento precedente
						Movimento movPrecedente = listaAnno.get(i - 1);
						// il saldo in quel periodo è determinato dal movimento attuale
						saldoPeriodo = movPrecedente.saldoDopoOperazione + interessiAnniPrecedenti;
						logger.info("il saldo nel periodo dal " + movPrecedente.getDataOperazione() +  "al " + mov.getDataOperazione() + " era di " +
						saldoPeriodo + " €");
						// conto i giorni dalla transazione precendente a quella attuale
						periodo = ChronoUnit.DAYS.between(movPrecedente.getDataOperazione(), mov.getDataOperazione());
						
						logger.info("durata in giorni: " + periodo);
						// interesse giornaliero
						interesseGiornaliero = (saldoPeriodo * this.getTassoDiInteresse()) / 365.0;
						
						logger.info("l' interesse giornaliero è calcolato usando questi dati: ( saldo periodo ( "+ saldoPeriodo+ " ) * tasso di interesse ( "+ this.getTassoDiInteresse()+ " ) / 365) " );
						logger.info("interesse giornaliero "+ interesseGiornaliero);
						// interesse temporaneo maturato
						interesseTemporaneo += interesseGiornaliero * periodo;
						
						logger.info("l' interesse Temporaneo è calcolato e accumulato usando questi dati:"
								+ " ( interesse Giornaliero ( "+ interesseGiornaliero + " )"+ " * periodo "+ periodo + "= " + (interesseGiornaliero * periodo));
						logger.info("interesse Temporaneo: " + interesseTemporaneo);
						

				}
				
				interesseTotale += interesseTemporaneo;
				logger.info("l interesse imponibile totale sarà di "+ interesseTotale);	
				if (interesseTotale > 0) {
					interessiNetti = interesseTotale - (interesseTotale * 0.26);
					logger.info("l interesse netto sarà di " + interessiNetti);	
				}else {
					interessiNetti = interesseTotale;
				}
				saldoPeriodo += interessiNetti;
				interessiAnniPrecedenti += interessiNetti;
				interesseTemporaneo = 0;
				saldoAlCambioAnno = saldoPeriodo;
				generaEstrattoconto(listaAnno, year, saldoPeriodo, interessiAnniPrecedenti, interesseTotale, interessiNetti);
				generaEstrattocontoPdf(listaAnno, year, saldoPeriodo, interessiAnniPrecedenti, interesseTotale, interessiNetti);
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
				// aggiungo i movimenti
				listaAnno.add(movimentoInizioAnno);
				movimentoInizioAnno.saldoDopoOperazione = listaAnno.get(0).getSaldoDopoOperazione();
				// in questo caso serve assegnare il valore dell ultimo movimento come saldo
				// dopo operazione per poter calcolare gli interessi
				listaAnno.add(movimentoInDataAttuale);
				movimentoInDataAttuale.saldoDopoOperazione = SaldoUltimoMOvimento;
				// qui li ordino per data
				Collections.sort(listaAnno);
				
				
				// ciclo sugli elementi della sottolista
				if(!listaAnno.isEmpty()) {
					for (int i = 1; i <= listaAnno.size() -1; i++) {
						// movimento corrente
						Movimento mov = listaAnno.get(i);
						// mmovimento precedente
						Movimento movPrecedente = listaAnno.get(i - 1);
						System.out.println( movPrecedente.getDataOperazione());
						// il saldo in quel periodo è determinato dal movimento attuale
						saldoPeriodo = movPrecedente.saldoDopoOperazione + interessiAnniPrecedenti;
						logger.info("il saldo nel periodo dal " + movPrecedente.getDataOperazione() +  "al " + mov.getDataOperazione() + " era di " +
								saldoPeriodo + " €");
						// conto i giorni dalla transazione precendente a quella attuale
						periodo = ChronoUnit.DAYS.between(movPrecedente.getDataOperazione(), mov.getDataOperazione());
						logger.info("durata in giorni: " + periodo);
						// interesse giornaliero
						interesseGiornaliero = (saldoPeriodo * this.getTassoDiInteresse()) / 365.0;

						logger.info("l' interesse giornaliero è calcolato usando questi dati: ( saldo periodo ( "+ saldoPeriodo+ " ) * tasso di interesse ( "+ this.getTassoDiInteresse()+ " ) / 365) " );
						logger.info("interesse giornaliero "+ interesseGiornaliero);
						// interesse temporaneo maturato
						interesseTemporaneo += interesseGiornaliero * periodo;

						logger.info("l' interesse Temporaneo è calcolato e accumulato usando questi dati:"
								+ " ( interesse Giornaliero ( "+ interesseGiornaliero + " )"+ " * periodo "+ periodo + "= " + (interesseGiornaliero * periodo));
						logger.info("interesse Temporaneo: " + interesseTemporaneo);
					}
				}
				// interesse Lordo
				interesseTotale += interesseTemporaneo;
				// netto
				logger.info("l interesse imponibile totale sarà di "+ interesseTotale);	
				if (interesseTotale > 0) {
					interessiNetti = interesseTotale - (interesseTotale * 0.26);
					logger.info("l interesse netto sarà di " + interessiNetti);	
				}else {
					interessiNetti = interesseTotale;
				}
				// IL SALDO DI QUEL PERIODO DIVENTA += AGLI INTERESSI NETTI
				saldoPeriodo += interessiNetti;
				// interessi dell anno precedente interessiNetti
				interessiAnniPrecedenti += interessiNetti;
				interesseTemporaneo = 0;
				saldoAlCambioAnno = saldoPeriodo;
				generaEstrattoconto(listaAnno, year, saldoPeriodo, interessiAnniPrecedenti, interesseTotale,
						interessiNetti);
				generaEstrattocontoPdf(listaAnno, year, saldoPeriodo, interessiAnniPrecedenti, interesseTotale, interessiNetti);
				interesseTotale = 0;
			} else if (listaAnno.isEmpty()){
				year++;
				System.out.println("è vuota");
				interesseGiornaliero = (saldoAlCambioAnno * this.tassoDiInteresse) / 365;
				interesseTotale = interesseGiornaliero * 365;
				interessiAnniPrecedenti += interesseTotale;
				
				
			}
		}

		this.saldo += interessiAnniPrecedenti;
	}
	
	
	public String getTipoDiConto() {
		String TipoDiconto;
		if (this.getClass().getSimpleName().equalsIgnoreCase("ContoCorrente")) {
			return TipoDiconto = "Conto Corrente";
		}else if (this.getClass().getSimpleName().equalsIgnoreCase("ContoRisparmio")) {
			return TipoDiconto = "Conto Risparmi";
		}return TipoDiconto = "conto Investimenti";
	}

	public void generaEstrattoconto(ArrayList<Movimento> listaAnno, int year ,double saldoPeriodo ,double interessiAnniPrecedenti ,double interesseTotale, double interessiNetti) {
		
		
			System.out.println("**************************************************************************************************");
			System.out.println();
			System.out.println( "|Estratto conto di " + this.titolare + " " + year );
			System.out.println("|"  + getTipoDiConto());
			System.out.println("|Data Operazione   |" + " TipoOperazione    " + "| Importo   "  + "| Saldo Parziale");
			System.out.println();
			if(!listaAnno.isEmpty()) {
				for (int i = 1; i < listaAnno.size()-1; i++) {
					if(year > 2021) {
						System.out.println("|"+ listaAnno.get(i).getDataOperazione() + "        | " +  listaAnno.get(i).getTipoOperazione().toString() + "        | " 
						+ listaAnno.get(i).getImportoOperazione() + "         | " + (listaAnno.get(i).saldoDopoOperazione +(interessiAnniPrecedenti - interessiNetti) ));
					}else {
						System.out.println("|"+ listaAnno.get(i).getDataOperazione() + "        | " +  listaAnno.get(i).getTipoOperazione().toString() + "       | " 
						+ listaAnno.get(i).getImportoOperazione() + "         | " + listaAnno.get(i).saldoDopoOperazione);
					}
				}
			}
			System.out.println();
			System.out.println("Saldo Disponibile: " + ( year > 2021 ? (listaAnno.get(listaAnno.size()-1).getSaldoDopoOperazione() + (interessiAnniPrecedenti - interessiNetti) ) : listaAnno.get(listaAnno.size()-1).getSaldoDopoOperazione()) );
			System.out.println("---------------------------------------------------------------------------------------------------");
			System.out.println("interessi Lordi Maturati:  " + interesseTotale);
			System.out.println("interessi Netti Maturati:  " + interessiNetti);
			System.out.println("Saldo finale in data " + listaAnno.get(listaAnno.size()-1).getDataOperazione() + " " + (listaAnno.get(listaAnno.size()-1).getSaldoDopoOperazione() + (interessiAnniPrecedenti) ));
			System.out.println();
			System.out.println("**************************************************************************************************");
		}
	
	
	
	
	public void generaEstrattocontoPdf(ArrayList<Movimento> listaAnno, int year ,double saldoPeriodo ,double interessiAnniPrecedenti ,double interesseTotale, double interessiNetti) {
		
		Document document = new Document();
    	
    	try {
			PdfWriter.getInstance(document, new FileOutputStream("EstrattoConto " + year + " " +  this.getTipoDiConto() + ".pdf"));
			
			document.open();
			
			document.add(new Paragraph("|Estratto conto di " + this.titolare + " " + year));

			for (int i = 1; i < listaAnno.size()-1; i++) { 
			document.add(new Paragraph (("|"+ listaAnno.get(i).getDataOperazione() + "        | " +  listaAnno.get(i).getTipoOperazione().toString() + "        | " 
							+ listaAnno.get(i).getImportoOperazione() + "         | " + (listaAnno.get(i).saldoDopoOperazione +(interessiAnniPrecedenti - interessiNetti) ))));		
			}
			document.add(new Paragraph("Saldo Disponibile: " + ( year > 2021 ? (listaAnno.get(listaAnno.size()-1).getSaldoDopoOperazione() + (interessiAnniPrecedenti - interessiNetti) ) : listaAnno.get(listaAnno.size()-1).getSaldoDopoOperazione()) ));
			document.add(new Paragraph("---------------------------------------------------------------------------------------------------"));
			document.add(new Paragraph("interessi Lordi Maturati:  " + interesseTotale));
			document.add(new Paragraph("interessi Netti Maturati:  " + interessiNetti));
			document.add(new Paragraph("Saldo finale in data " + listaAnno.get(listaAnno.size()-1).getDataOperazione() + " " + (listaAnno.get(listaAnno.size()-1).getSaldoDopoOperazione() + (interessiAnniPrecedenti) )));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            document.close();
        }
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
	        LocalDate ultimaData = listaMovimenti.get(listaMovimenti.size()-1).getDataOperazione();
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
	
	
	protected LocalDate generaData(LocalDate dataRiferimento, LocalDate dataAttuale) {
		
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
