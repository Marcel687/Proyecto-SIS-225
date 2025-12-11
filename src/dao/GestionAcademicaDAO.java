package dao;

import clases.EstudianteInscrito;
import clases.EvaluacionDTO;
import conexion.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestionAcademicaDAO {

    // Obtener estudiantes inscritos en un paralelo específico
    public List<EstudianteInscrito> obtenerEstudiantesPorParalelo(int idParalelo) {
        List<EstudianteInscrito> lista = new ArrayList<>();
        String sql = "SELECT i.id_inscripcion, e.id_estudiante, concat(p.apellidos, ' ', p.nombres) as nombre_completo "
                + "FROM inscripciones i "
                + "JOIN estudiantes e ON i.id_estudiante = e.id_estudiante "
                + "JOIN persona p ON e.id_persona = p.id_persona "
                + "WHERE i.id_paralelo = ? "
                + "ORDER BY p.apellidos";

        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idParalelo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new EstudianteInscrito(
                        rs.getInt("id_inscripcion"),
                        rs.getInt("id_estudiante"),
                        rs.getString("nombre_completo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Obtener evaluaciones configuradas para el paralelo
    public List<EvaluacionDTO> obtenerEvaluaciones(int idParalelo) {
        List<EvaluacionDTO> lista = new ArrayList<>();
        String sql = "SELECT id_evaluacion, nro_evaluacion, tipo, peso FROM evaluacion WHERE id_paralelo = ?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idParalelo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new EvaluacionDTO(
                        rs.getInt("id_evaluacion"),
                        rs.getInt("nro_evaluacion"),
                        rs.getString("tipo"),
                        rs.getDouble("peso")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void guardarNota(int idInscripcion, int idEvaluacion, int nota) {
        String update = "UPDATE calificacion SET nota = ?, nota_ponderada = (? * (SELECT peso FROM evaluacion WHERE id_evaluacion = ?)) WHERE id_inscripcion = ? AND id_evaluacion = ?";
        String insert = "INSERT INTO calificacion (id_evaluacion, id_inscripcion, nota, nota_ponderada) VALUES (?, ?, ?, (? * (SELECT peso FROM evaluacion WHERE id_evaluacion = ?)))";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            boolean actualizado = false;
            try (PreparedStatement psUpdate = conn.prepareStatement(update)) {
                psUpdate.setInt(1, nota);           // SET nota = ?
                psUpdate.setInt(2, nota);           // nota_ponderada = (? * ...)
                psUpdate.setInt(3, idEvaluacion);   // WHERE id_evaluacion = ? (subquery)
                psUpdate.setInt(4, idInscripcion);  // WHERE id_inscripcion = ?
                psUpdate.setInt(5, idEvaluacion);   // AND id_evaluacion = ?

                if (psUpdate.executeUpdate() > 0) {
                    actualizado = true;
                }
            }

            if (!actualizado) {
                try (PreparedStatement psInsert = conn.prepareStatement(insert)) {
                    psInsert.setInt(1, idEvaluacion);
                    psInsert.setInt(2, idInscripcion);
                    psInsert.setInt(3, nota);         // valor nota
                    psInsert.setInt(4, nota);         // valor nota (para el cálculo)
                    psInsert.setInt(5, idEvaluacion); // para el subquery de peso

                    psInsert.executeUpdate();
                }
            }

            actualizarKardex(conn, idInscripcion);

            conn.commit(); // Confirmar cambios
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Error SQL al guardar nota: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
 // En dao/GestionAcademicaDAO.java

 // 1. Método para obtener asistencia de una fecha específica (Corregido y verificado)
 public List<Object[]> obtenerAsistenciaPorFecha(int idParalelo, String fecha) {
     List<Object[]> lista = new ArrayList<>();
     String sql = """
             SELECT i.id_inscripcion, 
                    CONCAT(p.apellidos, ' ', p.nombres) AS nombre_completo, 
                    COALESCE(a.id_estado, 1) as id_estado_actual 
             FROM inscripciones i
             JOIN estudiantes e ON i.id_estudiante = e.id_estudiante
             JOIN persona p ON e.id_persona = p.id_persona
             LEFT JOIN asistencias a ON i.id_inscripcion = a.id_inscripcion AND a.fecha = ?
             WHERE i.id_paralelo = ?
             ORDER BY p.apellidos
             """;

     try (Connection conn = ConexionDB.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {
         
         ps.setString(1, fecha);
         ps.setInt(2, idParalelo);
         
         ResultSet rs = ps.executeQuery();
         while (rs.next()) {
             lista.add(new Object[]{
                 rs.getInt("id_inscripcion"),
                 rs.getString("nombre_completo"),
                 rs.getInt("id_estado_actual") // 1=Presente por defecto si es null
             });
         }
     } catch (SQLException e) { e.printStackTrace(); }
     return lista;
 }

 // 2. NUEVO MÉTODO: Obtener TODO el historial para el Resumen
 // Retorna: Nombre, Fecha, ID_Estado
 public List<Object[]> obtenerHistorialCompleto(int idParalelo) {
     List<Object[]> lista = new ArrayList<>();
     String sql = """
             SELECT CONCAT(p.apellidos, ' ', p.nombres) as nombre, 
                    a.fecha, 
                    a.id_estado
             FROM asistencias a
             JOIN inscripciones i ON a.id_inscripcion = i.id_inscripcion
             JOIN estudiantes e ON i.id_estudiante = e.id_estudiante
             JOIN persona p ON e.id_persona = p.id_persona
             WHERE i.id_paralelo = ?
             ORDER BY p.apellidos, a.fecha
             """;
     
     try (Connection conn = ConexionDB.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {
         ps.setInt(1, idParalelo);
         ResultSet rs = ps.executeQuery();
         while(rs.next()){
             lista.add(new Object[]{
                 rs.getString("nombre"),
                 rs.getDate("fecha"),
                 rs.getInt("id_estado")
             });
         }
     } catch (SQLException e) { e.printStackTrace(); }
     return lista;
 }
    // Método para guardar (Actualizar lógica para evitar duplicados en la misma fecha)
    public void registrarAsistencia(int idInscripcion, int idEstado, String fecha) {
        // Primero borramos si existe (forma simple de hacer "update o insert")
        String delete = "DELETE FROM asistencias WHERE id_inscripcion = ? AND fecha = ?";
        String insert = "INSERT INTO asistencias (id_inscripcion, id_estado, fecha) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection()) {
            // Borrar anterior
            try (PreparedStatement psDel = conn.prepareStatement(delete)) {
                psDel.setInt(1, idInscripcion);
                psDel.setString(2, fecha);
                psDel.executeUpdate();
            }
            // Insertar nuevo
            try (PreparedStatement psIns = conn.prepareStatement(insert)) {
                psIns.setInt(1, idInscripcion);
                psIns.setInt(2, idEstado);
                psIns.setString(3, fecha);
                psIns.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void guardarNota(int idInscripcion, int idEvaluacion, double nota) {
        String update = "UPDATE calificacion SET nota = ?, nota_ponderada = (nota * (SELECT peso FROM evaluacion WHERE id_evaluacion = ?)) WHERE id_inscripcion = ? AND id_evaluacion = ?";
        String insert = "INSERT INTO calificacion (id_evaluacion, id_inscripcion, nota, nota_ponderada) VALUES (?, ?, ?, (? * (SELECT peso FROM evaluacion WHERE id_evaluacion = ?)))";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); // Manejo transaccional

            // 1. Guardar/Actualizar Calificación
            boolean actualizado = false;
            try (PreparedStatement psUpdate = conn.prepareStatement(update)) {
                psUpdate.setDouble(1, nota);
                psUpdate.setInt(2, idEvaluacion);
                psUpdate.setInt(3, idInscripcion);
                psUpdate.setInt(4, idEvaluacion);
                if (psUpdate.executeUpdate() > 0) {
                    actualizado = true;
                }
            }

            if (!actualizado) {
                try (PreparedStatement psInsert = conn.prepareStatement(insert)) {
                    psInsert.setInt(1, idEvaluacion);
                    psInsert.setInt(2, idInscripcion);
                    psInsert.setDouble(3, nota);
                    psInsert.setDouble(4, nota);
                    psInsert.setInt(5, idEvaluacion);
                    psInsert.executeUpdate();
                }
            }

            // 2. Actualizar Kardex inmediatamente
            actualizarKardex(conn, idInscripcion);

            conn.commit(); // Confirmar cambios
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Método privado auxiliar para calcular y actualizar el Kardex
    private void actualizarKardex(Connection conn, int idInscripcion) throws SQLException {
        // A. Calcular nota final acumulada
        String sqlSuma = "SELECT SUM(nota_ponderada) as total FROM calificacion WHERE id_inscripcion = ?";
        double notaFinal = 0.0;

        try (PreparedStatement ps = conn.prepareStatement(sqlSuma)) {
            ps.setInt(1, idInscripcion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                notaFinal = rs.getDouble("total");
            }
        }

        // B. Determinar estado
        String estado = (notaFinal >= 51.0) ? "Aprobado" : "Reprobado";

        // C. Obtener datos necesarios para Kardex (id_estudiante, id_paralelo) desde inscripcion
        String sqlDatos = "SELECT id_estudiante, id_paralelo FROM inscripciones WHERE id_inscripcion = ?";
        int idEstudiante = 0;
        int idParalelo = 0;

        try (PreparedStatement ps = conn.prepareStatement(sqlDatos)) {
            ps.setInt(1, idInscripcion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idEstudiante = rs.getInt("id_estudiante");
                idParalelo = rs.getInt("id_paralelo");
            }
        }

        String checkKardex = "SELECT id_kardex FROM kardex WHERE id_estudiante = ? AND id_paralelo = ?";
        String updateKardex = "UPDATE kardex SET nota_final = ?, estado = ?, fecha_registro = NOW() WHERE id_estudiante = ? AND id_paralelo = ?";
        String insertKardex = "INSERT INTO kardex (id_estudiante, id_paralelo, nota_final, estado, fecha_registro) VALUES (?, ?, ?, ?, NOW())";

        boolean existe = false;
        try (PreparedStatement ps = conn.prepareStatement(checkKardex)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idParalelo);
            if (ps.executeQuery().next()) {
                existe = true;
            }
        }

        if (existe) {
            try (PreparedStatement ps = conn.prepareStatement(updateKardex)) {
                ps.setDouble(1, notaFinal);
                ps.setString(2, estado);
                ps.setInt(3, idEstudiante);
                ps.setInt(4, idParalelo);
                ps.executeUpdate();
            }
        } else {
            try (PreparedStatement ps = conn.prepareStatement(insertKardex)) {
                ps.setInt(1, idEstudiante);
                ps.setInt(2, idParalelo);
                ps.setDouble(3, notaFinal);
                ps.setString(4, estado);
                ps.executeUpdate();
            }
        }
    }

    // Método para obtener la nota final actual del Kardex para mostrarla en la tabla
    public double obtenerNotaKardex(int idInscripcion) {
        String sql = "SELECT k.nota_final FROM kardex k, inscripciones i "
                + "WHERE k.id_estudiante = i.id_estudiante AND k.id_paralelo = i.id_paralelo "
                + "AND i.id_inscripcion = ?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("nota_final");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int obtenerNota(int idInscripcion, int idEvaluacion) {
        String sql = "SELECT nota FROM calificacion WHERE id_inscripcion = ? AND id_evaluacion = ?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            ps.setInt(2, idEvaluacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("nota"); // .getInt() en lugar de .getDouble()

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
