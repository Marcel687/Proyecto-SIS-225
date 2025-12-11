package clases;

public class Empleado {
    private int idEmpleado;
    private int idPersona;
    private String cargo;

    public Empleado(int idEmpleado, int idPersona, String cargo) {
        this.idEmpleado = idEmpleado;
        this.idPersona = idPersona;
        this.cargo = cargo;
    }
    
    // Getters y Setters

	public int getIdEmpleado() {
		return idEmpleado;
	}

	public void setIdEmpleado(int idEmpleado) {
		this.idEmpleado = idEmpleado;
	}

	public int getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(int idPersona) {
		this.idPersona = idPersona;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	
	private String nombres, apellidos, carnet, correo, telefono, carrera;
	// Getters y Setters
	 public String getNombres() { return nombres; }
	 public void setNombres(String nombres) { this.nombres = nombres; }
	 public String getApellidos() { return apellidos; }
	 public void setApellidos(String apellidos) { this.apellidos = apellidos; }
	 public String getCarnet() { return carnet; }
	 public void setCarnet(String carnet) { this.carnet = carnet; }
	 public String getCorreo() { return correo; }
	 public void setCorreo(String correo) { this.correo = correo; }
	 public String getTelefono() { return telefono; }
	 public void setTelefono(String telefono) { this.telefono = telefono; }
	 public String getCarrera() { return carrera; }
	 public void setCarrera(String carrera) { this.carrera = carrera; }
    
    
}