package dao;

import clases.Usuario;
import conexion.ConexionDB;

import java.sql.*;

public class UsuarioDAO {

    public Usuario autenticar(String username, String password) {
        String sql = """
                SELECT u.id_usuario, u.username, u.password, u.rol_id, r.nombre, u.estado
                FROM usuarios u
                INNER JOIN roles r ON u.rol_id = r.id_rol
                WHERE u.username = ? AND u.password = ? 
                """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password); // En producción: comparar hash

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getInt("rol_id"),
                    rs.getString("nombre"),
                    true
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Usuario no encontrado o inactivo
    }

    //obtener el ID de la persona una vez autenticado
    public int getIdPersonaPorUsuario(int idUsuario) {
        String sql = """
            SELECT id_persona FROM estudiantes WHERE id_usuario = ?
            UNION
            SELECT id_persona FROM docentes WHERE id_usuario = ?
            UNION
            SELECT id_persona FROM empleado WHERE id_usuario = ?
            LIMIT 1
            """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_persona");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // No encontrado
    }
    
    public int getIdUsuarioPorPersona(int idPersona) {
        String sql = "SELECT id_usuario FROM persona WHERE id_persona = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_usuario");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean cambiarEstadoUsuario(int idUsuario, boolean activar) {
        String sql = "UPDATE usuarios SET estado = ? WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activar ? 1 : 0);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getIdEstudiantePorUsuario(int idUsuario) {
        String sql = """
            SELECT e.id_estudiante
            FROM usuarios u
            JOIN persona p ON u.id_usuario = p.id_usuario
            JOIN estudiantes e ON p.id_persona = e.id_persona
            WHERE u.id_usuario = ?
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Configurar el parámetro de la consulta
            ps.setInt(1, idUsuario);  // Solo necesitamos un parámetro

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_estudiante");  // Retornar el id_estudiante
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Si no se encuentra el estudiante, retornamos -1
    }
}