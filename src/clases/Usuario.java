package clases;

public class Usuario {
    private int idUsuario;
    private String username;
    private String password;
    private int rolId;
    private String nombreRol;
    private boolean estado;

    // Constructor, getters y setters
    public Usuario(int idUsuario, String username, String password,
                   int rolId, String nombreRol, boolean estado) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.rolId = rolId;
        this.nombreRol = nombreRol;
        this.estado = estado;
    }

    // Getters
    public int getIdUsuario() { return idUsuario; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getRolId() { return rolId; }
    public String getNombreRol() { return nombreRol; }
    public boolean isestado() { return estado; }

	@Override
	public String toString() {
		return "Usuario [idUsuario=" + idUsuario + ", username=" + username + ", password=" + password + ", rolId="
				+ rolId + ", nombreRol=" + nombreRol + ", activo=" + estado + "]";
	}
    
    
}