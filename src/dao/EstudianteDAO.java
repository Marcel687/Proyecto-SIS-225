package dao;

import conexion.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import clases.Estudiante;

public class EstudianteDAO {

    private static final int ROL_ESTUDIANTE = 3;

    public Estudiante obtenerPorUsername(String usernameBuscado) {
	    String sql = """
	        SELECT
	            e.id_estudiante,
	            p.id_persona,
	            p.nombres,
	            p.apellidos,
	            p.carnet,
	            p.correo,
	            p.telefono,
	            u.id_usuario,
	            u.username,
	            c.id_carrera,
	            c.nombre_carrera,
	            e.matricula
	        FROM usuarios u
	        INNER JOIN persona p ON u.id_usuario = p.id_usuario
	        INNER JOIN estudiantes e ON p.id_persona = e.id_persona
	        INNER JOIN carrera c ON e.id_carrera = c.id_carrera
	        WHERE u.username = ?
	    """;

	    try (Connection conn = ConexionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, usernameBuscado);
	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            return new Estudiante(
	                rs.getInt("id_estudiante"),
	                rs.getInt("id_persona"),
	                rs.getString("nombres"),
	                rs.getString("apellidos"),
	                rs.getString("carnet"),
	                rs.getString("correo"),
	                rs.getInt("telefono"),
	                rs.getInt("id_usuario"),
	                rs.getString("username"),
	                rs.getInt("id_carrera"),
	                rs.getString("nombre_carrera"),
	                rs.getString("matricula")
	            );
	        }

	    } catch (SQLException ex) {
	        ex.printStackTrace();
	    }

	    return null; // No se encontró
	}
    
    public boolean registrarEstudiante(String nombres, String apellidos,
                                       String carnet,
                                       String correo, String telefono,
                                       String username, String password,
                                       int idCarrera, String matricula) {

        String sqlUsuario = "INSERT INTO usuarios (username, password, rol_id) VALUES (?, ?, ?)";           // ← CORREGIDO
        String sqlPersona = "INSERT INTO persona (id_usuario, nombres, apellidos, carnet, correo, telefono) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlEstudiante = "INSERT INTO estudiantes (id_persona, id_carrera, matricula) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return false;

            conn.setAutoCommit(false);

            int idUsuario = 0;
            int idPersona = 0;

            // 1. Usuario
            try (PreparedStatement ps = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setInt(3, ROL_ESTUDIANTE);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) idUsuario = rs.getInt(1);
                }
            }

            // 2. Persona (con carnet solo aquí)
            try (PreparedStatement ps = conn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idUsuario);
                ps.setString(2, nombres.trim());
                ps.setString(3, apellidos.trim());
                ps.setString(4, carnet != null ? carnet.trim().toUpperCase() : null);
                ps.setString(5, correo.trim());
                ps.setString(6, telefono != null ? telefono.trim() : null);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) idPersona = rs.getInt(1);
                }
            }

            // 3. Estudiante (sin carnet)
            try (PreparedStatement ps = conn.prepareStatement(sqlEstudiante)) {
                ps.setInt(1, idPersona);
                ps.setInt(2, idCarrera);
                ps.setString(3, matricula.trim().toUpperCase());
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }
    
    public List<Estudiante> listarEstudiantes(String busqueda, boolean mostrarInactivos) {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT p.id_persona, p.nombres, p.apellidos, p.carnet, p.correo, p.telefono, "
                   + "c.nombre_carrera AS carrera, e.matricula "
                   + "FROM persona p "
                   + "JOIN estudiantes e ON p.id_persona = e.id_persona "
                   + "JOIN carrera c ON e.id_carrera = c.id_carrera "
                   + "JOIN usuarios u ON p.id_usuario = u.id_usuario "
                   + "WHERE (p.nombres LIKE ? OR p.apellidos LIKE ? OR p.carnet LIKE ?) "
                   + "AND u.estado = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + busqueda + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setInt(4, mostrarInactivos ? 0 : 1);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Estudiante est = new Estudiante(0, rs.getInt("id_persona"), 0, rs.getString("matricula"));
                    est.setNombres(rs.getString("nombres"));
                    est.setApellidos(rs.getString("apellidos"));
                    est.setCarnet(rs.getString("carnet"));
                    est.setCorreo(rs.getString("correo"));
                    est.setTelefono(rs.getInt("telefono"));
                    est.setCarrera(rs.getString("carrera"));
                    lista.add(est);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean cambiarEstadoEstudiante(int idPersona, boolean activar) {
        UsuarioDAO udao = new UsuarioDAO();
        int idUsuario = udao.getIdUsuarioPorPersona(idPersona);

        if (idUsuario == -1) return false;

        return udao.cambiarEstadoUsuario(idUsuario, activar);
    }


    
    public boolean actualizarEstudiante(int idPersona, String nombres, String apellidos, String carnet,
            String correo, String telefono, int idCarrera, String matricula) {
    	String sqlPersona = "UPDATE persona SET nombres = ?, apellidos = ?, carnet = ?, correo = ?, telefono = ? WHERE id_persona = ?";
    	String sqlEstudiante = "UPDATE estudiantes SET id_carrera = ?, matricula = ? WHERE id_persona = ?";

    	try (Connection conn = ConexionDB.getConnection();
    			PreparedStatement ps1 = conn.prepareStatement(sqlPersona);
    			PreparedStatement ps2 = conn.prepareStatement(sqlEstudiante)) {

    		conn.setAutoCommit(false);

    		// Actualizar persona
    		ps1.setString(1, nombres.trim());
    		ps1.setString(2, apellidos.trim());
    		ps1.setString(3, carnet.trim().toUpperCase());
    		ps1.setString(4, correo.trim());
    		ps1.setString(5, telefono != null ? telefono.trim() : null);
    		ps1.setInt(6, idPersona);
    		ps1.executeUpdate();

    		// Actualizar estudiante
    		ps2.setInt(1, idCarrera);
    		ps2.setString(2, matricula.trim().toUpperCase());
    		ps2.setInt(3, idPersona);
    		ps2.executeUpdate();

    		conn.commit();
    		return true;
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
    }
}