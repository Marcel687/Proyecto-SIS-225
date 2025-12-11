package dao;

import conexion.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import clases.Empleado;

public class EmpleadoDAO {
    private static final int ROL_ADMIN = 1;        // según tu tabla roles (1 = Administrador)
    // o private static final int ROL_EMPLEADO = 2; si quieres distinguir

    public boolean registrarEmpleado(String nombres, String apellidos,
                                     String carnet,
                                     String correo, String telefono,
                                     String username, String password,
                                     String cargo) {
        String sqlUsuario = "INSERT INTO usuarios (username, password, rol_id) VALUES (?, ?, ?)";
        String sqlPersona = "INSERT INTO persona (id_usuario, nombres, apellidos, carnet, correo, telefono) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlEmpleado = "INSERT INTO empleado (id_persona, cargo) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            if (conn == null) return false;

            conn.setAutoCommit(false);

            int idUsuario = 0;
            int idPersona = 0;

            // 1. Insertar usuario (rol administrador o empleado)
            try (PreparedStatement ps = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, username);
                ps.setString(2, password); // ¡OJO! En producción deberías hashearla (BCrypt, etc.)
                ps.setInt(3, ROL_ADMIN);
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

            // 3. Insertar empleado con su cargo
            try (PreparedStatement ps = conn.prepareStatement(sqlEmpleado)) {
                ps.setInt(1, idPersona);
                ps.setString(2, cargo.trim().toUpperCase());
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
    
    public List<Empleado> listarEmpleados(String busqueda, boolean mostrarInactivos) {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT p.id_persona, p.nombres, p.apellidos, p.carnet, p.correo, p.telefono, e.cargo "
                   + "FROM persona p "
                   + "JOIN empleado e ON p.id_persona = e.id_persona "
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
                    Empleado emp = new Empleado(rs.getInt("id_persona"), rs.getInt("id_persona"), rs.getString("cargo"));
                    emp.setNombres(rs.getString("nombres"));
                    emp.setApellidos(rs.getString("apellidos"));
                    emp.setCarnet(rs.getString("carnet"));
                    emp.setCorreo(rs.getString("correo"));
                    emp.setTelefono(rs.getString("telefono"));
                    lista.add(emp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    
    public boolean cambiarEstadoEmpleado(int idPersona, boolean activar) {
        UsuarioDAO udao = new UsuarioDAO();
        int idUsuario = udao.getIdUsuarioPorPersona(idPersona);

        if (idUsuario == -1) return false;

        return udao.cambiarEstadoUsuario(idUsuario, activar);
    }


    
    public boolean actualizarEmpleado(int idPersona, String nombres, String apellidos, String carnet,
            String correo, String telefono, String cargo) {
    	String sqlPersona = "UPDATE persona SET nombres = ?, apellidos = ?, carnet = ?, correo = ?, telefono = ? WHERE id_persona = ?";
    	String sqlEmpleado = "UPDATE empleado SET cargo = ? WHERE id_persona = ?";

    	try (Connection conn = ConexionDB.getConnection();
    			PreparedStatement ps1 = conn.prepareStatement(sqlPersona);
    			PreparedStatement ps2 = conn.prepareStatement(sqlEmpleado)) {

    		conn.setAutoCommit(false);

    		ps1.setString(1, nombres.trim());
    		ps1.setString(2, apellidos.trim());
    		ps1.setString(3, carnet.trim().toUpperCase());
    		ps1.setString(4, correo.trim());
    		ps1.setString(5, telefono != null ? telefono.trim() : null);
    		ps1.setInt(6, idPersona);
    		ps1.executeUpdate();

    		ps2.setString(1, cargo.trim().toUpperCase());
    		ps2.setInt(2, idPersona);
    		ps2.executeUpdate();

    		conn.commit();
    		return true;
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
    }
}