// dao/MateriaDAO.java
package dao;

import conexion.ConexionDB;
import java.sql.*;

import clases.Materia;

public class MateriaDAO {

    // Registra materia y guarda el texto en prerequisitos
	public int registrarMateria(int idCarrera, String codigo, String nombre,
            int creditos, int semestre, String prerequisitos) {
		String sql = "INSERT INTO materias (id_carrera, codigo, nombre, creditos, semestre, prerequisitos) " +
					 "VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = ConexionDB.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setInt(1, idCarrera);
			ps.setString(2, codigo.trim().toUpperCase());
			ps.setString(3, nombre.trim());
			ps.setInt(4, creditos);
			ps.setInt(5, semestre);
			ps.setString(6, (prerequisitos != null && !prerequisitos.trim().isEmpty()) 
					? prerequisitos.trim().toUpperCase() : null);

			int filas = ps.executeUpdate();
			if (filas > 0) {
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						return rs.getInt(1); // Devuelve el ID generado
					}
				}
			}
			return 0; // Error

		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

    // Nuevo: Agregar relación en tabla prerequisitos
    public boolean agregarPrerrequisito(int idMateria, int idMateriaRequerida) {
        String sql = "INSERT IGNORE INTO prerrequisitos (id_materia, id_materia_requerida) VALUES (?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMateria);
            ps.setInt(2, idMateriaRequerida);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) return true; // ya existe
            e.printStackTrace();
            return false;
        }
    }

    // Cargar todas las materias para ComboBox
    public String[] listarMateriasParaCombo() {
        String sql = "SELECT id_materia, codigo, nombre FROM materias where estado = 1 ORDER BY codigo";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            java.util.List<String> lista = new java.util.ArrayList<>();
            while (rs.next()) {
                lista.add(rs.getInt("id_materia") + " | " + rs.getString("codigo") + " - " + rs.getString("nombre"));
            }
            return lista.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[]{"Error al cargar materias"};
        }
    }

    // Obtener ID desde texto del combo
    public static int extraerIdDeCombo(String texto) {
        if (texto == null || texto.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(texto.split(" \\| ")[0]);
        } catch (Exception e) {
            return 0;
        }
    }
    
    public boolean modificarMateria(int idMateria, int idCarrera, String codigo, String nombre,
                                   int creditos, int semestre, String prerequisitos) {
        String sql = "UPDATE materias SET id_carrera = ?, codigo = ?, nombre = ?, " +
                     "creditos = ?, semestre = ?, prerequisitos = ? WHERE id_materia = ?";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idCarrera);
            ps.setString(2, codigo.trim().toUpperCase());
            ps.setString(3, nombre.trim());
            ps.setInt(4, creditos);
            ps.setInt(5, semestre);
            ps.setString(6, (prerequisitos != null && !prerequisitos.trim().isEmpty()) 
                    ? prerequisitos.trim().toUpperCase() : null);
            ps.setInt(7, idMateria);
            
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String[] obtenerDatosMateria(int idMateria) {
        String sql = "SELECT m.*, c.nombre_carrera FROM materias m " +
                     "JOIN carrera c ON m.id_carrera = c.id_carrera " +
                     "WHERE m.id_materia = ? and estado = 1";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idMateria);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getInt("id_carrera")),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    String.valueOf(rs.getInt("creditos")),
                    String.valueOf(rs.getInt("semestre")),
                    rs.getString("prerequisitos") != null ? rs.getString("prerequisitos") : "",
                    rs.getString("nombre_carrera")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] listarCarrerasParaCombo() {
        String sql = "SELECT id_carrera, nombre_carrera FROM carrera ORDER BY nombre_carrera";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            java.util.List<String> lista = new java.util.ArrayList<>();
            while (rs.next()) {
                lista.add(rs.getInt("id_carrera") + " | " + rs.getString("nombre_carrera"));
            }
            return lista.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[]{"Error al cargar carreras"};
        }
    }
    
    public boolean cambiarEstadoMateria(int idMateria, boolean nuevoEstado) {
        String sql = "UPDATE materias SET estado = ? WHERE id_materia = ?";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setBoolean(1, nuevoEstado);
            ps.setInt(2, idMateria);
            
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Materia obtenerMateriaPorId(int idMateria) {
        String sql = "SELECT * FROM materias WHERE id_materia = ?";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idMateria);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Materia materia = new Materia();
                materia.setIdMateria(rs.getInt("id_materia"));
                materia.setIdCarrera(rs.getInt("id_carrera"));
                materia.setCodigo(rs.getString("codigo"));
                materia.setNombre(rs.getString("nombre"));
                materia.setCreditos(rs.getInt("creditos"));
                materia.setSemestre(rs.getInt("semestre"));
                materia.setPrerrequisitos(rs.getString("prerequisitos"));
                materia.setEstado(rs.getBoolean("estado"));  // Usando setEstado()
                return materia;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método para alternar estado (habilitar/deshabilitar)
    public boolean alternarEstadoMateria(int idMateria) {
        // Obtener el estado actual
        Materia materia = obtenerMateriaPorId(idMateria);
        if (materia != null) {
            boolean nuevoEstado = !materia.getEstado();  // Usando isEstado()
            return cambiarEstadoMateria(idMateria, nuevoEstado);
        }
        return false;
    }
}