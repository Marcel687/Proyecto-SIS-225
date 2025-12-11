package clases;

public class Materia {
    private int idMateria;
    private int idCarrera;
    private String codigo;
    private String nombre;
    private int creditos;
    private int semestre;
    private String prerrequisitos; 
    private boolean estado;
    
    public Materia() {
    	
    }

    public Materia(int idMateria, int idCarrera, String codigo, String nombre, 
                   int creditos, int semestre, String prerrequisitos) {
        this.idMateria = idMateria;
        this.idCarrera = idCarrera;
        this.codigo = codigo;
        this.nombre = nombre;
        this.creditos = creditos;
        this.semestre = semestre;
        this.prerrequisitos = prerrequisitos;
    }

    // Getters y Setters
    public int getIdMateria() {
		return idMateria;
	}

	public void setIdMateria(int idMateria) {
		this.idMateria = idMateria;
	}

	public int getIdCarrera() {
		return idCarrera;
	}

	public void setIdCarrera(int idCarrera) {
		this.idCarrera = idCarrera;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getCreditos() {
		return creditos;
	}

	public void setCreditos(int creditos) {
		this.creditos = creditos;
	}

	public int getSemestre() {
		return semestre;
	}

	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	public String getPrerrequisitos() {
		return prerrequisitos;
	}

	public void setPrerrequisitos(String prerrequisitos) {
		this.prerrequisitos = prerrequisitos;
	}
	
	public boolean getEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }

	
}