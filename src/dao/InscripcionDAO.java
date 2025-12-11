package dao;

import java.sql.*;
import conexion.ConexionDB;

public class InscripcionDAO {

    // ===========================
    //     MÉTODO: INSCRIBIR
    // ===========================
    public String inscribirEnParalelo(int idEstudiante, int idParalelo) {

        try (Connection con = ConexionDB.getConnection()) {

            // 1. Verificar si ya está inscrito
            String sqlCheckInscrito = """
                SELECT COUNT(*) AS total
                FROM inscripciones
                WHERE id_estudiante = ? AND id_paralelo = ?
            """;

            PreparedStatement ps1 = con.prepareStatement(sqlCheckInscrito);
            ps1.setInt(1, idEstudiante);
            ps1.setInt(2, idParalelo);
            ResultSet rs1 = ps1.executeQuery();
            rs1.next();

            if (rs1.getInt("total") > 0) {
                return "⚠ Ya estás inscrito en este paralelo.";
            }

            // 2. Obtener información del paralelo
            String sqlParalelo = """
                SELECT p.id_materia, p.cupo
                FROM paralelos p
                WHERE p.id_paralelo = ?
            """;

            PreparedStatement ps2 = con.prepareStatement(sqlParalelo);
            ps2.setInt(1, idParalelo);
            ResultSet rs2 = ps2.executeQuery();

            if (!rs2.next()) {
                return "⚠ El paralelo no existe.";
            }

            int idMateria = rs2.getInt("id_materia");
            int cupos = rs2.getInt("cupo");

            // =============================
            //  VALIDACIÓN DE CUPOS
            // =============================
            if (cupos <= 0) {
                return "❌ No hay cupos disponibles en este paralelo.";
            }

         // =============================
        //  VALIDACIÓN DE PRERREQUISITOS
        // =============================
        String sqlPrereq = """
            SELECT pr.id_materia_requerida, me.nombre 
            FROM prerrequisitos pr
            INNER JOIN materias m ON m.id_materia = pr.id_materia
            INNER JOIN materias me ON me.id_materia = pr.id_materia_requerida 
            WHERE pr.id_materia = ?
        """;

        try (PreparedStatement ps3 = con.prepareStatement(sqlPrereq)) {
            ps3.setInt(1, idMateria);  // Se pasa el ID de la materia a la consulta
            ResultSet rs3 = ps3.executeQuery();

            while (rs3.next()) {
                int idMateriaRequerida = rs3.getInt("id_materia_requerida");
                String nombreMateriaRequerida = rs3.getString("nombre");  // Obtenemos el nombre de la materia

                // Buscar la materia requerida en el Kardex del estudiante
                String sqlEstado = """
                    SELECT k.estado
                    FROM kardex k
                    INNER JOIN paralelos p ON k.id_paralelo = p.id_paralelo
                    WHERE k.id_estudiante = ?
                      AND p.id_materia = ?
                """;

                try (PreparedStatement ps4 = con.prepareStatement(sqlEstado)) {
                    ps4.setInt(1, idEstudiante);
                    ps4.setInt(2, idMateriaRequerida);

                    ResultSet rs4 = ps4.executeQuery();

                    // No cursó esa materia nunca
                    if (!rs4.next()) {
                        return "❌ No puedes inscribirte: prerrequisito NO cursado (Materia: " 
                                + nombreMateriaRequerida + ").";
                    }

                    String estado = rs4.getString("estado");

                    if (estado == null || !estado.equalsIgnoreCase("Aprobado")) {
                        return "❌ No puedes inscribirte: prerrequisito NO aprobado (Materia: " 
                                + nombreMateriaRequerida + ").";
                    }
                }
            }

        } catch (SQLException e) {
            return "❌ Error al validar prerrequisitos: " + e.getMessage();
        }



            // =============================
            //  INSERTAR INSCRIPCIÓN
            // =============================
            String sqlInsert = """
                INSERT INTO inscripciones (id_paralelo, id_estudiante, fecha)
                VALUES (?, ?, NOW())
            """;

            PreparedStatement ps4 = con.prepareStatement(sqlInsert);
            ps4.setInt(1, idParalelo);
            ps4.setInt(2, idEstudiante);
            ps4.executeUpdate();

            // =============================
            //  RESTAR UN CUPO
            // =============================
            String sqlUpdateCupos = """
                UPDATE paralelos
                SET cupo = cupo - 1
                WHERE id_paralelo = ?
            """;

            PreparedStatement ps5 = con.prepareStatement(sqlUpdateCupos);
            ps5.setInt(1, idParalelo);
            ps5.executeUpdate();

            return "✅ Inscripción realizada correctamente.";

        } catch (SQLException e) {
            return "❌ Error en inscripción: " + e.getMessage();
        }
    }



    // ==================================
    //       MÉTODO: SALIR DEL PARALELO
    // ==================================
    public String salirDeParalelo(int idEstudiante, int idParalelo) {

        try (Connection con = ConexionDB.getConnection()) {

            // 1. Verificar si está inscrito
            String sqlCheck = """
                SELECT COUNT(*) AS total
                FROM inscripciones
                WHERE id_estudiante = ? AND id_paralelo = ?
            """;

            PreparedStatement ps1 = con.prepareStatement(sqlCheck);
            ps1.setInt(1, idEstudiante);
            ps1.setInt(2, idParalelo);
            ResultSet rs1 = ps1.executeQuery();
            rs1.next();

            if (rs1.getInt("total") == 0) {
                return "⚠ No estás inscrito en este paralelo.";
            }

            // 2. Eliminar inscripción
            String sqlDelete = """
                DELETE FROM inscripciones
                WHERE id_estudiante = ? AND id_paralelo = ?
            """;

            PreparedStatement ps2 = con.prepareStatement(sqlDelete);
            ps2.setInt(1, idEstudiante);
            ps2.setInt(2, idParalelo);
            ps2.executeUpdate();

            // 3. Sumar cupo
            String sqlUpdateCupos = """
                UPDATE paralelos
                SET cupo = cupo + 1
                WHERE id_paralelo = ?
            """;

            PreparedStatement ps3 = con.prepareStatement(sqlUpdateCupos);
            ps3.setInt(1, idParalelo);
            ps3.executeUpdate();

            return "✔ Te has salido del paralelo correctamente.";

        } catch (SQLException e) {
            return "❌ Error al salir del paralelo: " + e.getMessage();
        }
    }
}
