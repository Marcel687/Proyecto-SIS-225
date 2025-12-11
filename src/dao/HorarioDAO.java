package dao;

import java.sql.*;
import clases.Horario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import conexion.ConexionDB;

public class HorarioDAO {

    // Método para obtener todos los horarios
    public List<Horario> obtenerHorarios() {
        List<Horario> lista = new ArrayList<>();

        String sql = """
            SELECT 
                CONCAT(h.hora_inicio, ' - ', h.hora_fin) AS horas,
                m.nombre AS nombreM,
                p.nombre AS nombreP,
                h.dia,
                h.aula
            FROM horario h
            INNER JOIN paralelos p ON p.id_paralelo = h.id_paralelo
            INNER JOIN materias m ON m.id_materia = p.id_materia
            """;

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Horario h = new Horario(
                        rs.getString("horas"),
                        rs.getString("nombreM"),
                        rs.getString("nombreP"),
                        rs.getString("dia"),
                        rs.getString("aula")
                );
                lista.add(h);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    public List<Horario> obtenerHorarioPorEstudiante(int idEstudiante) {
        List<Horario> lista = new ArrayList<>();

        String sql = """
            SELECT 
                CONCAT(h.hora_inicio, ' - ', h.hora_fin) AS horas,
                m.nombre AS nombreM,
                p.nombre AS nombreP,
                h.dia,
                h.aula
            FROM horario h
            INNER JOIN paralelos p ON p.id_paralelo = h.id_paralelo
            INNER JOIN materias m ON m.id_materia = p.id_materia
            INNER JOIN inscripciones i ON i.id_paralelo = p.id_paralelo
            WHERE i.id_estudiante = ?
            ORDER BY h.dia, h.hora_inicio
            """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Horario h = new Horario(
                            rs.getString("horas"),
                            rs.getString("nombreM"),
                            rs.getString("nombreP"),
                            rs.getString("dia"),
                            rs.getString("aula")
                    );
                    lista.add(h);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

}
