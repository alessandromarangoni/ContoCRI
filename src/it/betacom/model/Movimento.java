package it.betacom.model;

import java.time.LocalDate;

public class Movimento implements Comparable<Movimento>{

	private LocalDate dataOperazione;
	private TipoOperazione tipoOperazione;
	private double importoOperazione;
	double saldoDopoOperazione;

	
	
	public Movimento(LocalDate dataOperazione, TipoOperazione tipoOperazione, double importoOperazione) {
		super();
		this.dataOperazione = dataOperazione;
		this.tipoOperazione = tipoOperazione;
		this.importoOperazione = importoOperazione;
		
	}
	
	 public int compareTo(Movimento altroMovimento) {
        // Confronto le date di operazione
        return this.getDataOperazione().compareTo(altroMovimento.getDataOperazione());
    }
	
	
	public LocalDate getDataOperazione() {
		return dataOperazione;
	}
	public void setDataOperazione(LocalDate dataOperazione) {
		this.dataOperazione = dataOperazione;
	}
	public TipoOperazione getTipoOperazione() {
		return tipoOperazione;
	}
	public void setTipoOperazione(TipoOperazione tipoOperazione) {
		this.tipoOperazione = tipoOperazione;
	}
	public double getImportoOperazione() {
		return importoOperazione;
	}
	public void setImportoOperazione(double importoOperazione) {
		this.importoOperazione = importoOperazione;
	}
	public double getSaldoDopoOperazione() {
		return saldoDopoOperazione;
	}
	public void setSaldoDopoOperazione(double saldoDopoOperazione) {
		this.saldoDopoOperazione = saldoDopoOperazione;
	}
	
	
	
}
