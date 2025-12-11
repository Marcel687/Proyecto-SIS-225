package dao;

import clases.Notas;
import conexion.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotasDAO {

    public List<Notas> listarNotasPorEstudiante(int idEstudiante, int idParalelo) {
        List<Notas> lista = new ArrayList<>();
        String sql = """
            SELECT c.nota, e.peso, c.nota_ponderada, e.nro_evaluacion, e.semana_evaluacion
            FROM calificacion c
            INNER JOIN evaluacion e ON c.id_evaluacion = e.id_evaluacion
            INNER JOIN inscripciones i ON c.id_inscripcion = i.id_inscripcion
            INNER JOIN paralelos p ON e.id_paralelo = p.id_paralelo
            INNER JOIN estudiantes e2 ON i.id_estudiante = e2.id_estudiante
            INNER JOIN persona p2 ON e2.id_persona = p2.id_persona
            WHERE p.id_paralelo = ?
              AND e2.id_estudiante = ?
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idParalelo);
            ps.setInt(2, idEstudiante);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int nroEvaluacion = rs.getInt("nro_evaluacion");
                double peso = rs.getDouble("peso");
                double nota = rs.getDouble("nota");
                double notaPonderada = rs.getDouble("nota_ponderada");
                int semana = rs.getInt("semana_evaluacion");

                lista.add(new Notas(nroEvaluacion, peso, nota, notaPonderada, semana));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    
}

