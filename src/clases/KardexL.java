package clases;

public class KardexL {

	
	//kardex
	private int idKardex;
	
	//Estudiante
	private int idEstudiante;
	private String Nombre;
	
	//Paralelo
	private int idParalelo;
	private String docente;
	private String materia;
	private int semestre;
	private int anio;
	
	//Datos
	private int notaFinal;
	private String estado;
	
	
	public KardexL(int idKardex, int idEstudiante, String nombre, int idParalelo, String docente, String materia,
			int semestre, int anio, int notaFinal, String estado) {
		super();
		this.idKardex = idKardex;
		this.idEstudiante = idEstudiante;
		this.Nombre = nombre;
		this.idParalelo = idParalelo;
		this.docente = docente;
		this.materia = materia;
		this.semestre = semestre;
		this.anio = anio;
		this.notaFinal = notaFinal;
		this.estado = estado;
	}


	public int getIdKardex() {
		return idKardex;
	}


	public void setIdKardex(int idKardex) {
		this.idKardex = idKardex;
	}


	public int getIdEstudiante() {
		return idEstudiante;
	}


	public void setIdEstudiante(int idEstudiante) {
		this.idEstudiante = idEstudiante;
	}


	public String getNombre() {
		return Nombre;
	}


	public void setNombre(String nombre) {
		Nombre = nombre;
	}


	public int getIdParalelo() {
		return idParalelo;
	}


	public void setIdParalelo(int idParalelo) {
		this.idParalelo = idParalelo;
	}


	public String getDocente() {
		return docente;
	}


	public void setDocente(String docente) {
		this.docente = docente;
	}


	public String getMateria() {
		return materia;
	}


	public void setMateria(String materia) {
		this.materia = materia;
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


	public int getNotaFinal() {
		return notaFinal;
	}


	public void setNotaFinal(int notaFinal) {
		this.notaFinal = notaFinal;
	}


	public String getEstado() {
		return estado;
	}


	public void setEstado(String estado) {
		this.estado = estado;
	}
}
