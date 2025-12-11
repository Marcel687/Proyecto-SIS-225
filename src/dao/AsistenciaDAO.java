package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import clases.Asistencia;
import conexion.ConexionDB;

public class AsistenciaDAO {

    public List<Asistencia> listarAsistenciasPorParalelo(int idEstudiante, int idParalelo) {
        List<Asistencia> lista = new ArrayList<>();

        String sql = """
            SELECT 
                a.id_asistencia,
                CONCAT(p.nombres, ' ', p.apellidos) AS nombre_completo,
                e.nombre_estado AS estado_asistencia,
                pa.nombre AS paralelo,
                m.nombre AS materia,
                a.fecha,
                pa.semestre,
                pa.anio
            FROM asistencias a
            INNER JOIN estadoasistencia e ON e.id_estado = a.id_estado
            INNER JOIN inscripciones i ON i.id_inscripcion = a.id_inscripcion
            INNER JOIN estudiantes es ON i.id_estudiante = es.id_estudiante
            INNER JOIN persona p ON es.id_persona = p.id_persona
            INNER JOIN paralelos pa ON i.id_paralelo = pa.id_paralelo
            INNER JOIN materias m ON m.id_materia = pa.id_materia
            WHERE pa.estado = true
              AND es.id_estudiante = ?
              AND pa.id_paralelo = ?
            ORDER BY a.fecha;
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ps.setInt(2, idParalelo);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int idAsis = rs.getInt("id_asistencia");
                    String nombreS = rs.getString("nombre_completo");
                    String estadoAsis = rs.getString("estado_asistencia");
                    String paralelo = rs.getString("paralelo");
                    String materia = rs.getString("materia");
                    LocalDate fecha = rs.getDate("fecha").toLocalDate();
                    int semestre = rs.getInt("semestre");
                    int anio = rs.getInt("anio");

                    Asistencia asistencia = new Asistencia(
                            idAsis, nombreS, estadoAsis, paralelo, materia,
                            fecha, semestre, anio
                    );

                    lista.add(asistencia);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}

