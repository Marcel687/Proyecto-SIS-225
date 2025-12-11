package dao;

import clases.Docente;
import clases.Usuario;
import conexion.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocenteDAO {
	public Docente recupera(int idUsuario) {
        String sql = "SELECT d.id_docente as ID, p.nombres as NOMBRE, p.apellidos as APELLIDO, p.carnet as CARNET, p.correo as CORREO, p.telefono as TELEFONO "
        		+ "FROM usuarios u, persona p, docentes d "
        		+ "where u.id_usuario = " + idUsuario
        		+ " and p.id_usuario = u.id_usuario "
        		+ "and d.id_persona = p.id_persona;";
        
        
        Connection db = ConexionDB.getConnection();
		try {
			Statement stmt = db.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next()) {
				return new Docente(
                    rs.getInt("ID"),
                    rs.getString("NOMBRE"),
                    rs.getString("APELLIDO"),
                    rs.getString("CARNET"),
                    rs.getString("CORREO"),
                    rs.getString("TELEFONO")
                );
			}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Usuario no encontrado o inactivo
    }
	
	private static final int ROL_DOCENTE = 2;  // Asumiendo que en tu tabla 'roles' el id 4 es para Docente (ajusta si es diferente)

    public boolean registrarDocente(String nombres, String apellidos,
                                    String carnet,
                                    String correo, String telefono,
                                    String username, String password) {
        String sqlUsuario = "INSERT INTO usuarios (username, password, rol_id) VALUES (?, ?, ?)";
        String sqlPersona = "INSERT INTO persona (id_usuario, nombres, apellidos, carnet, correo, telefono) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDocente = "INSERT INTO docentes (id_persona) VALUES (?)";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return false;

            conn.setAutoCommit(false);

            int idUsuario = 0;
            int idPersona = 0;

            // 1. Insertar usuario (rol docente)
            try (PreparedStatement ps = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, username);
                ps.setString(2, password); // ¡OJO! En producción hashea la contraseña (BCrypt, etc.)
                ps.setInt(3, ROL_DOCENTE);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) idUsuario = rs.getInt(1);
                }
            }

            // 2. Insertar persona
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

            // 3. Insertar docente (solo id_persona, ya que no tiene campos adicionales)
            try (PreparedStatement ps = conn.prepareStatement(sqlDocente)) {
                ps.setInt(1, idPersona);
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
    
    public List<Docente> listarDocentes(String busqueda, boolean mostrarInactivos) {
        List<Docente> lista = new ArrayList<>();
        String sql = "SELECT p.id_persona, p.nombres, p.apellidos, p.carnet, p.correo, p.telefono "
                   + "FROM persona p "
                   + "JOIN docentes d ON p.id_persona = d.id_persona "
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
                    Docente doc = new Docente(rs.getInt("id_persona"), rs.getInt("id_persona"));
                    doc.setNombre(rs.getString("nombres"));
                    doc.setApellido(rs.getString("apellidos"));
                    doc.setCarnet(rs.getString("carnet"));
                    doc.setCorreo(rs.getString("correo"));
                    doc.setTelefono(rs.getString("telefono"));
                    lista.add(doc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    public boolean cambiarEstadoDocente(int idPersona, boolean activar) {
        UsuarioDAO udao = new UsuarioDAO();
        int idUsuario = udao.getIdUsuarioPorPersona(idPersona);

        if (idUsuario == -1) return false;

        return udao.cambiarEstadoUsuario(idUsuario, activar);
    }


    
    public boolean actualizarDocente(int idPersona, String nombres, String apellidos, String carnet,
            String correo, String telefono) {
    	String sql = "UPDATE persona SET nombres = ?, apellidos = ?, carnet = ?, correo = ?, telefono = ? WHERE id_persona = ?";

    	try (Connection conn = ConexionDB.getConnection();
    			PreparedStatement ps = conn.prepareStatement(sql)) {

    		ps.setString(1, nombres.trim());
    		ps.setString(2, apellidos.trim());
    		ps.setString(3, carnet.trim().toUpperCase());
    		ps.setString(4, correo.trim());
    		ps.setString(5, telefono != null ? telefono.trim() : null);
    		ps.setInt(6, idPersona);

    		return ps.executeUpdate() > 0;
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
    }
}
