package clases;

public class Horario {
	private String horas;
	private String nombreM;
	private String nombreP;
	private String dia;
	private String aula;
	
	public Horario(String horas, String nombreM, String nombreP, String dia, String aula) {
		super();
		this.horas = horas;
		this.nombreM = nombreM;
		this.nombreP = nombreP;
		this.dia = dia;
		this.aula = aula;
	}
	
	public String getHoras() {
		return horas;
	}
	public void setHoras(String horas) {
		this.horas = horas;
	}
	public String getNombreM() {
		return nombreM;
	}
	public void setNombreM(String nombreM) {
		this.nombreM = nombreM;
	}
	public String getNombreP() {
		return nombreP;
	}
	public void setNombreP(String nombreP) {
		this.nombreP = nombreP;
	}
	public String getDia() {
		return dia;
	}
	public void setDia(String dia) {
		this.dia = dia;
	}
	public String getAula() {
		return aula;
	}
	public void setAula(String aula) {
		this.aula = aula;
	}

	
}
