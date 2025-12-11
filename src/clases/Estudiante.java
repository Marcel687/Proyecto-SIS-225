package clases;

public class Estudiante{
    private int idEstudiante;
    private int idPersona;
    private int idCarrera;
	private int idUsuario;
    private String matricula;
	private String nombres;
	private String apellidos;
	private String carnet;
	private String correo;
	private int telefono;
	private String username;
	private String carrera;
    
	public Estudiante(int idEstudiante, int idPersona, String nombres, String apellidos, String carnet, String correo,
			int telefono, int idUsuario, String username, int idCarrera, String carrera, String matricula) {
		super();
		this.idEstudiante = idEstudiante;
		this.idPersona = idPersona;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.carnet = carnet;
		this.correo = correo;
		this.telefono = telefono;
		this.idUsuario = idUsuario;
		this.username = username;
		this.idCarrera = idCarrera;
		this.carrera = carrera;
		this.matricula = matricula;
	}

    public Estudiante(int idEstudiante, int idPersona, int idCarrera, String matricula) {
        this.idEstudiante = idEstudiante;
        this.idPersona = idPersona;
        this.idCarrera = idCarrera;
        this.matricula = matricula;
    }
    
    public int getIdEstudiante() {
		return idEstudiante;
	}


	public void setIdEstudiante(int idEstudiante) {
		this.idEstudiante = idEstudiante;
	}


	public int getIdPersona() {
		return idPersona;
	}


	public void setIdPersona(int idPersona) {
		this.idPersona = idPersona;
	}


	public String getNombres() {
		return nombres;
	}


	public void setNombres(String nombres) {
		this.nombres = nombres;
	}


	public String getApellidos() {
		return apellidos;
	}


	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
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


	public int getTelefono() {
		return telefono;
	}


	public void setTelefono(int telefono) {
		this.telefono = telefono;
	}


	public int getIdUsuario() {
		return idUsuario;
	}


	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public int getIdCarrera() {
		return idCarrera;
	}


	public void setIdCarrera(int idCarrera) {
		this.idCarrera = idCarrera;
	}


	public String getCarrera() {
		return carrera;
	}


	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}


	public String getMatricula() {
		return matricula;
	}


	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
}