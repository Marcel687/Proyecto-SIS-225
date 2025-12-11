package clases;

public class Notas {
	private int nroEvaluacion;
	private double peso;
	private double nota;          // Cambiado de 'decimal' a 'double'
	private double notaPonderada; // Cambiado a double por si el cálculo no es entero
	private int semana;
	
	public Notas(int nroEvaluacion, double peso, double nota, double notaPonderada, int semana) {
		super();
		this.nroEvaluacion = nroEvaluacion;
		this.peso = peso;
		this.nota = nota;
		this.notaPonderada = notaPonderada;
		this.semana = semana;
	}
	
	public int getNroEvaluacion() {
		return nroEvaluacion;
	}
	public void setNroEvaluacion(int nroEvaluacion) {
		this.nroEvaluacion = nroEvaluacion;
	}
	public double getPeso() {
		return peso;
	}
	public void setPeso(double peso) {
		this.peso = peso;
	}
	public double getNota() {
		return nota;
	}
	public void setNota(double nota) {
		this.nota = nota;
	}
	public double getNotaPonderada() {
		return notaPonderada;
	}
	public void setNotaPonderada(double notaPonderada) {
		this.notaPonderada = notaPonderada;
	}
	public int getSemana() {
		return semana;
	}
	public void setSemana(int semana) {
		this.semana = semana;
	}

	

}
