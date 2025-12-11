package clases;

import java.time.LocalDate;
import java.time.LocalTime;

public class Asistencia {
	
	private int idAsistencia;
	private String nombreS;
	private String estadoAsis;
	private String paralelo;
	private String materia;
	private LocalDate fecha;
	private int semestre;
	private int anio;
	
	
	public Asistencia(int idAsistencia, String nombreS, String estadoAsis, String paralelo, String materia,
			LocalDate fecha, int semestre, int anio) {
		super();
		this.idAsistencia = idAsistencia;
		this.nombreS = nombreS;
		this.estadoAsis = estadoAsis;
		this.paralelo = paralelo;
		this.materia = materia;
		this.fecha = fecha;
		this.semestre = semestre;
		this.anio = anio;
	}


	public int getIdAsistencia() {
		return idAsistencia;
	}


	public void setIdAsistencia(int idAsistencia) {
		this.idAsistencia = idAsistencia;
	}


	public String getNombreS() {
		return nombreS;
	}


	public void setNombreS(String nombreS) {
		this.nombreS = nombreS;
	}


	public String getEstadoAsis() {
		return estadoAsis;
	}


	public void setEstadoAsis(String estadoAsis) {
		this.estadoAsis = estadoAsis;
	}


	public String getParalelo() {
		return paralelo;
	}


	public void setParalelo(String paralelo) {
		this.paralelo = paralelo;
	}


	public String getMateria() {
		return materia;
	}


	public void setMateria(String materia) {
		this.materia = materia;
	}


	public LocalDate getFecha() {
		return fecha;
	}


	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}


	public int getSemestre() {
		return semestre;
	}


	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}


	public int getAnio() {
		return anio;
	}


	public void setAnio(int anio) {
		this.anio = anio;
	}
	

	
	

}
