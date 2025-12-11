package clases;

public class Paralelo {
    private int idParalelo;
    private int idMateria;
    private int idDocente;
	private String materia;
	private String docente;
	private String nombre;
	private String codigo;
	private int semestre;
	private int anio;
	private int cupo;
    private boolean estado;
	private String dia;
	private String horaInicio;
	private String horaFin;

    public Paralelo() {
    	
    }
    public Paralelo(int idParalelo, int idMateria, int idDocente, String nombre, 
                    int semestre, int anio, int cupo) {
        this.idParalelo = idParalelo;
        this.idMateria = idMateria;
        this.idDocente = idDocente;
        this.nombre = nombre;
        this.semestre = semestre;
        this.anio = anio;
        this.cupo = cupo;
    }
    
	public Paralelo(String materia, String docente, String nombre, int semestre, int anio, int cupo) {
		super();
		this.materia = materia;
		this.docente = docente;
		this.nombre = nombre;
		this.semestre = semestre;
		this.anio = anio;
		this.cupo = cupo;
	}
	
	public Paralelo(int idParalelo, int idMateria, String nombreMateria, int idDocente, String nombreDocente,
			String nombre, String codigo, int semestre, int anio, int cupo, String dia, String horaInicio, String horaFin) {
		super();
		this.idParalelo = idParalelo;
		this.idMateria = idMateria;
		this.materia = nombreMateria;
		this.idDocente = idDocente;
		this.docente = nombreDocente;
		this.nombre = nombre;
		this.codigo = codigo;
		this.semestre = semestre;
		this.anio = anio;
		this.cupo = cupo;
		this.dia = dia;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
	}
	
	public int getIdParalelo() {
		return idParalelo;
	}

	public void setIdParalelo(int idParalelo) {
		this.idParalelo = idParalelo;
	}

	public int getIdMateria() {
		return idMateria;
	}

	public void setIdMateria(int idMateria) {
		this.idMateria = idMateria;
	}

	public int getIdDocente() {
		return idDocente;
	}

	public void setIdDocente(int idDocente) {
		this.idDocente = idDocente;
	}
	public String getMateria() {
		return materia;
	}
	public void setMateria(String materia) {
		this.materia = materia;
	}
	public String getDocente() {
		return docente;
	}
	public void setDocente(String docente) {
		this.docente = docente;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
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
	public int getCupo() {
		return cupo;
	}
	public void setCupo(int cupo) {
		this.cupo = cupo;
	}
	public boolean getEstado() {
		return estado;
	}
	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	public String getDia() {
		return dia;
	}
	public void setDia(String dia) {
		this.dia = dia;
	}
	public String getHoraInicio() {
		return horaInicio;
	}
	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}
	public String getHoraFin() {
		return horaFin;
	}
	public void setHoraFin(String horaFin) {
		this.horaFin = horaFin;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}
