package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import clases.KardexL;
import conexion.ConexionDB;

public class KardexLDAO {

    public List<KardexL> obtenerKardexPorEstudianteYSemestre(int idEstudiante, int semestre) {
        List<KardexL> lista = new ArrayList<>();

        String sql = """
            SELECT 
                k.id_kardex,
                est.id_estudiante,
                per.nombres AS nombre_estudiante,
                par.id_paralelo,
                CONCAT(pe.nombres, ' ', pe.apellidos) AS docente,
                mat.nombre AS nombre_materia,
                par.semestre,
                par.anio,
                k.nota_final,
                k.estado
            FROM kardex k
            INNER JOIN estudiantes est ON k.id_estudiante = est.id_estudiante
            INNER JOIN persona per ON est.id_persona = per.id_persona
            INNER JOIN paralelos par ON k.id_paralelo = par.id_paralelo
            INNER JOIN docentes doc ON par.id_docente = doc.id_docente
            INNER JOIN persona pe ON pe.id_persona = doc.id_persona 
            INNER JOIN materias mat ON par.id_materia = mat.id_materia
            WHERE k.id_estudiante = ? AND par.semestre = ?
            ORDER BY par.anio, par.semestre;
        """;

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {

            st.setInt(1, idEstudiante);
            st.setInt(2, semestre);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                // Ahora el estado YA VIENE como texto desde la BD
                String estadoTexto = rs.getString("estado");

                KardexL kardex = new KardexL(
                    rs.getInt("id_kardex"),
                    rs.getInt("id_estudiante"),
                    rs.getString("nombre_estudiante"),
                    rs.getInt("id_paralelo"),
                    rs.getString("docente"),
                    rs.getString("nombre_materia"),
                    rs.getInt("semestre"),
                    rs.getInt("anio"),
                    rs.getInt("nota_final"),
                    estadoTexto
                );

                lista.add(kardex);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    public List<KardexL> obtenerKardexCompleto(int idEstudiante) {
        List<KardexL> lista = new ArrayList<>();

        String sql = """
            SELECT 
                k.id_kardex,
                est.id_estudiante,
                per.nombres AS nombre_estudiante,
                par.id_paralelo,
                CONCAT(pe.nombres, ' ', pe.apellidos) AS docente,
                mat.nombre AS nombre_materia,
                par.semestre,
                par.anio,
                k.nota_final,
                k.estado
            FROM kardex k
            INNER JOIN estudiantes est ON k.id_estudiante = est.id_estudiante
            INNER JOIN persona per ON est.id_persona = per.id_persona
            INNER JOIN paralelos par ON k.id_paralelo = par.id_paralelo
            INNER JOIN docentes doc ON par.id_docente = doc.id_docente
            INNER JOIN persona pe ON pe.id_persona = doc.id_persona 
            INNER JOIN materias mat ON par.id_materia = mat.id_materia
            WHERE k.id_estudiante = ?
            ORDER BY par.anio DESC, par.semestre DESC;
        """;

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {

            st.setInt(1, idEstudiante);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                KardexL kardex = new KardexL(
                    rs.getInt("id_kardex"),
                    rs.getInt("id_estudiante"),
                    rs.getString("nombre_estudiante"),
                    rs.getInt("id_paralelo"),
                    rs.getString("docente"),
                    rs.getString("nombre_materia"),
                    rs.getInt("semestre"),
                    rs.getInt("anio"),
                    rs.getInt("nota_final"),
                    rs.getString("estado")
                );
                lista.add(kardex);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


}
