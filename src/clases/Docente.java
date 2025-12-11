package clases;

public class Docente {
	private int idDocente;
	private String nombre;
	private String apellido;
	private String carnet;
	private String correo;
	private String telefono;
    private int idPersona;

    public Docente(int idDocente, int idPersona) {
        this.idDocente = idDocente;
        this.idPersona = idPersona;
    }
	
	public Docente(String nombre) {
		super();
		this.nombre = nombre;
	}

	public Docente(int idDocente, String nombre, String apellido, String carnet, String correo, String telefono) {
		this.idDocente = idDocente;
		this.nombre = nombre;
		this.apellido = apellido;
		this.carnet = carnet;
		this.correo = correo;
		this.telefono = telefono;
	}

	public int getIdPersona() {
		return idPersona;
	}
	public void setIdPersona(int idPersona) {
		this.idPersona = idPersona;
	}
	public int getIdDocente() {
		return idDocente;
	}
	public void setIdDocente(int idDocente) {
		this.idDocente = idDocente;
	}

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getCarnet() {
		return carnet;
	}
	public void setCarnet(String carnet) {
		this.carnet = carnet;
	}

	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	@Override
	public String toString() {
		return "Docente [idDocente=" + idDocente + ", nombre=" + nombre + ", apellido=" + apellido + ", carnet="
				+ carnet + ", correo=" + correo + ", telefono=" + telefono + "]";
	}
	
	
	
}
