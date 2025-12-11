package dao;

import clases.Paralelo;
import conexion.ConexionDB;
import conexion.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class ParaleloDAO {
	
	public ParaleloDAO() {
	}
	
	public ParaleloDAO(int idDocente) {
		
	}

	public Paralelo obtenerParaleloPorId(int idParalelo) {

        String sql = """
            SELECT 
                p.id_paralelo,
                m.id_materia,
                m.nombre AS nombre_materia,
                d.id_docente,
                CONCAT(per.nombres, ' ', per.apellidos) AS nombre_docente,
                p.nombre,
                m.codigo,
                p.semestre,
                p.anio,
                p.cupo,
                h.dia,
                h.hora_inicio,
                h.hora_fin
            FROM paralelos p
            INNER JOIN materias m      ON p.id_materia = m.id_materia
            INNER JOIN docentes d      ON p.id_docente = d.id_docente
            INNER JOIN persona per     ON d.id_persona = per.id_persona
            LEFT JOIN horario h        ON p.id_paralelo = h.id_paralelo
            WHERE p.id_paralelo = ?
        """;

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idParalelo);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Paralelo(
                        rs.getInt("id_paralelo"),
                        rs.getInt("id_materia"),
                        rs.getString("nombre_materia"),
                        rs.getInt("id_docente"),
                        rs.getString("nombre_docente"),
                        rs.getString("nombre"),
                        rs.getString("codigo"),
                        rs.getInt("semestre"),
                        rs.getInt("anio"),
                        rs.getInt("cupo"),
                        rs.getString("dia"),
                        rs.getString("hora_inicio"),
                        rs.getString("hora_fin")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // no encontrado
    }
	
	public List<Paralelo> listarTodos() {

	    String sql = """
	        SELECT 
	            p.id_paralelo,
	            m.id_materia,
	            m.codigo,
	            m.nombre AS nombre_materia,
	            m.codigo,
	            d.id_docente,
	            CONCAT(per.nombres, ' ', per.apellidos) AS nombre_docente,
	            p.nombre AS nombre_paralelo,
	            p.cupo,
	            h.dia,
	            h.hora_inicio,
	            h.hora_fin
	        FROM paralelos p
	        INNER JOIN materias m ON p.id_materia = m.id_materia
	        INNER JOIN docentes d ON p.id_docente = d.id_docente
	        INNER JOIN persona per ON d.id_persona = per.id_persona
	        LEFT JOIN horario h ON p.id_paralelo = h.id_paralelo
	        ORDER BY m.nombre, p.nombre
	    """;

	    List<Paralelo> lista = new ArrayList<>();

	    try (Connection con = ConexionDB.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            lista.add(new Paralelo(
	                rs.getInt("id_paralelo"),
	                rs.getInt("id_materia"),
	                rs.getString("nombre_materia"),
	                rs.getInt("id_docente"),
	                rs.getString("nombre_docente"),
	                rs.getString("nombre_paralelo"),
	                rs.getString("codigo"),
	                0, 0, // semestre y año no usados aquí
	                rs.getInt("cupo"),
	                rs.getString("dia"),
	                rs.getString("hora_inicio"),
	                rs.getString("hora_fin")
	            ));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return lista;
	}
	
	public Paralelo obtenerParaleloPorIdEstudiante(int idEstudiante) {

        String sql = """
            SELECT 
			    p.id_paralelo,
			    m.id_materia,
			    m.nombre AS nombre_materia,
			    d.id_docente,
			    CONCAT(per.nombres, ' ', per.apellidos) AS nombre_docente,
			    p.nombre,
			    m.codigo,
			    p.semestre,
			    p.anio,
			    p.cupo,
			    h.dia,
			    h.hora_inicio,
			    h.hora_fin
			FROM paralelos p
			INNER JOIN materias m ON p.id_materia = m.id_materia
			INNER JOIN docentes d ON p.id_docente = d.id_docente
			INNER JOIN persona per ON d.id_persona = per.id_persona
			LEFT JOIN horario h ON p.id_paralelo = h.id_paralelo
			INNER JOIN inscripciones i ON p.id_paralelo = i.id_paralelo
			WHERE i.id_estudiante = ?;
        """;

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Paralelo(
                        rs.getInt("id_paralelo"),
                        rs.getInt("id_materia"),
                        rs.getString("nombre_materia"),
                        rs.getInt("id_docente"),
                        rs.getString("nombre_docente"),
                        rs.getString("nombre"),
                        rs.getString("codigo"),
                        rs.getInt("semestre"),
                        rs.getInt("anio"),
                        rs.getInt("cupo"),
                        rs.getString("dia"),
                        rs.getString("hora_inicio"),
                        rs.getString("hora_fin")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // no encontrado
    }
	
	public List<Paralelo> listarParalelosPorEstudiante(int idEstudiante) {

	    String sql = """
	        SELECT 
	            p.id_paralelo,
	            m.id_materia,
	            m.codigo,
	            m.nombre AS nombre_materia,
	            d.id_docente,
	            CONCAT(per.nombres, ' ', per.apellidos) AS nombre_docente,
	            p.nombre AS nombre_paralelo,
	            p.cupo,
	            h.dia,
	            h.hora_inicio,
	            h.hora_fin
	        FROM paralelos p
	        INNER JOIN materias m ON p.id_materia = m.id_materia
	        INNER JOIN docentes d ON p.id_docente = d.id_docente
	        INNER JOIN persona per ON d.id_persona = per.id_persona
	        LEFT JOIN horario h ON p.id_paralelo = h.id_paralelo
	        INNER JOIN inscripciones i ON p.id_paralelo = i.id_paralelo
	        WHERE i.id_estudiante = ?
	        ORDER BY m.nombre, p.nombre
	    """;

	    List<Paralelo> lista = new ArrayList<>();

	    try (Connection con = ConexionDB.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, idEstudiante);  // Establecer el parámetro idEstudiante

	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            lista.add(new Paralelo(
	                rs.getInt("id_paralelo"),
	                rs.getInt("id_materia"),
	                rs.getString("nombre_materia"),
	                rs.getInt("id_docente"),
	                rs.getString("nombre_docente"),
	                rs.getString("nombre_paralelo"),
	                rs.getString("codigo"),
	                0, 0, // semestre y año no usados aquí
	                rs.getInt("cupo"),
	                rs.getString("dia"),
	                rs.getString("hora_inicio"),
	                rs.getString("hora_fin")
	            ));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return lista;
	}
	
	public DefaultTableModel obtenerModeloTablaParalelos(int idDocente) {
	    String[] columnas = {"ID", "Materia", "Paralelo", "Semestre", "Inscritos"};
	    DefaultTableModel modelo = new DefaultTableModel(null, columnas);

	    try (Connection conn = ConexionDB.getConnection(); 
	         Statement stmt = conn.createStatement(); 
	         ResultSet result = stmt.executeQuery("select p.id_paralelo as ID, m.nombre as MATERIA, p.nombre as NOMBRE, concat(p.semestre, '-', p.anio) as SEMESTRE, count(distinct(i.id_estudiante)) as INSCRITOS"
	         		+ " from paralelos p, materias m, inscripciones i"
	         		+ " where p.id_docente = " + idDocente
	         		+ " and p.id_paralelo = i.id_paralelo"
	         		+ " and m.id_materia = p.id_materia"
	         		+ " group by p.id_paralelo;")) {

	        while(result.next()) {
	            Object[] fila = new Object[8];
	            fila[0] = result.getInt("ID");
	            fila[1] = result.getString("MATERIA");
	            fila[2] = result.getString("NOMBRE");
	            fila[3] = result.getString("SEMESTRE");
	            fila[4] = result.getString("INSCRITOS");
	            modelo.addRow(fila);
	        }
	        conn.close();

	    } catch (SQLException e) {
	        System.out.println(e.getMessage());
	    }
	    return modelo;
	}
	
	public boolean registrarParaleloSinHorario(int idMateria, int idDocente, String nombre,
            String semestre, int anio, int cupo) {
String sql = "INSERT INTO paralelos (id_materia, id_docente, nombre, semestre, anio, cupo) " +
"VALUES (?, ?, ?, ?, ?, ?)";

try (Connection conn = ConexionDB.getConnection();
PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

ps.setInt(1, idMateria);
ps.setInt(2, idDocente);
ps.setString(3, nombre);
ps.setString(4, semestre);
ps.setInt(5, anio);
ps.setInt(6, cupo);

ps.executeUpdate();
return true;

} catch (SQLException e) {
e.printStackTrace();
return false;
}
}

// MÉTODOS PARA COMBOS
public String[] listarMaterias() {
return cargarCombo("SELECT id_materia, codigo, nombre FROM materias where estado = 1 ORDER BY nombre", "codigo", "nombre");
}

public String[] listarDocentes() {
return cargarCombo(
"SELECT d.id_docente, p.nombres, p.apellidos " +
"FROM docentes d " +
"JOIN persona p ON d.id_persona = p.id_persona " +
"JOIN usuarios u ON p.id_usuario = u.id_usuario " +
"WHERE u.estado = 1 " +
"ORDER BY p.apellidos, p.nombres",
"nombres", "apellidos"  // o el formato que espera tu método
);
}

public String[] listarParalelos() {
return cargarCombo(
"SELECT p.id_paralelo, m.codigo, m.nombre, p.nombre " +
"FROM paralelos p JOIN materias m ON p.id_materia = m.id_materia " +
"where estado = 1," +
"ORDER BY m.nombre, p.nombre",
"codigo", "nombre"
);
}

private String[] cargarCombo(String sql, String campo1, String campo2) {
try (Connection conn = ConexionDB.getConnection();
PreparedStatement ps = conn.prepareStatement(sql);
ResultSet rs = ps.executeQuery()) {

java.util.List<String> lista = new java.util.ArrayList<>();
while (rs.next()) {
String texto = rs.getInt(1) + " | " +
rs.getString(campo1) + " - " +
rs.getString(campo2);
lista.add(texto);
}
return lista.toArray(new String[0]);

} catch (SQLException e) {
e.printStackTrace();
return new String[]{"Error al cargar"};
}
}

public static int extraerId(String texto) {
if (texto == null) return 0;
try {
return Integer.parseInt(texto.split(" \\| ")[0]);
} catch (Exception e) {
return 0;
}
}

// Agregar este método a tu clase ParaleloDAO.java
public int registrarParaleloYDevolverId(int idMateria, int idDocente, String nombre,
         String semestre, int anio, int cupo) {
String sql = "INSERT INTO paralelos (id_materia, id_docente, nombre, semestre, anio, cupo) " +
"VALUES (?, ?, ?, ?, ?, ?)";

try (Connection conn = conexion.ConexionDB.getConnection();
PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

ps.setInt(1, idMateria);
ps.setInt(2, idDocente);
ps.setString(3, nombre);
ps.setString(4, semestre);
ps.setInt(5, anio);
ps.setInt(6, cupo);

int filas = ps.executeUpdate();
if (filas > 0) {
try (ResultSet rs = ps.getGeneratedKeys()) {
if (rs.next()) {
return rs.getInt(1); // Devuelve el ID generado
}
}
}
} catch (SQLException e) {
e.printStackTrace();
}
return -1; // Error
}

// En la clase ParaleloDAO
public boolean modificarParalelo(int idParalelo, int idMateria, int idDocente, String nombre,
 String semestre, int anio, int cupo) {
String sql = "UPDATE paralelos SET id_materia = ?, id_docente = ?, nombre = ?, " +
"semestre = ?, anio = ?, cupo = ? WHERE id_paralelo = ?";

try (Connection conn = ConexionDB.getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) {

ps.setInt(1, idMateria);
ps.setInt(2, idDocente);
ps.setString(3, nombre);
ps.setString(4, semestre);
ps.setInt(5, anio);
ps.setInt(6, cupo);
ps.setInt(7, idParalelo);

int filas = ps.executeUpdate();
return filas > 0;

} catch (SQLException e) {
e.printStackTrace();
return false;
}
}

public String[] obtenerDatosParalelo(int idParalelo) {
String sql = "SELECT p.*, m.codigo, m.nombre as materia_nombre, " +
"CONCAT(per.nombres, ' ', per.apellidos) as docente_nombre " +
"FROM paralelos p " +
"JOIN materias m ON p.id_materia = m.id_materia " +
"JOIN docentes d ON p.id_docente = d.id_docente " +
"JOIN persona per ON d.id_persona = per.id_persona " +
"WHERE p.id_paralelo = ?";

try (Connection conn = ConexionDB.getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) {

ps.setInt(1, idParalelo);
ResultSet rs = ps.executeQuery();

if (rs.next()) {
return new String[]{
String.valueOf(rs.getInt("id_materia")),
String.valueOf(rs.getInt("id_docente")),
rs.getString("nombre"),
rs.getString("semestre"),
String.valueOf(rs.getInt("anio")),
String.valueOf(rs.getInt("cupo")),
rs.getString("codigo"),
rs.getString("materia_nombre"),
rs.getString("docente_nombre")
};
}
} catch (SQLException e) {
e.printStackTrace();
}
return null;
}

// En ParaleloDAO.java
public boolean cambiarEstadoParalelo(int idParalelo, boolean nuevoEstado) {
String sql = "UPDATE paralelos SET estado = ? WHERE id_paralelo = ?";

try (Connection conn = ConexionDB.getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) {

ps.setBoolean(1, nuevoEstado);
ps.setInt(2, idParalelo);

int filas = ps.executeUpdate();
return filas > 0;

} catch (SQLException e) {
e.printStackTrace();
return false;
}
}
/**
public Paralelo obtenerParaleloPorId(int idParalelo) {
String sql = "SELECT * FROM paralelos WHERE id_paralelo = ?";

try (Connection conn = ConexionDB.getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) {

ps.setInt(1, idParalelo);
ResultSet rs = ps.executeQuery();

if (rs.next()) {
Paralelo paralelo = new Paralelo();
paralelo.setIdParalelo(rs.getInt("id_paralelo"));
paralelo.setIdMateria(rs.getInt("id_materia"));
paralelo.setIdDocente(rs.getInt("id_docente"));
paralelo.setNombre(rs.getString("nombre"));
paralelo.setSemestre(rs.getInt("semestre"));
paralelo.setAnio(rs.getInt("anio"));
paralelo.setCupo(rs.getInt("cupo"));
paralelo.setEstado(rs.getBoolean("estado"));  // Usando setEstado()
return paralelo;
}

} catch (SQLException e) {
e.printStackTrace();
}
return null;
}
**/
public boolean alternarEstadoParalelo(int idParalelo) {
// Obtener el estado actual
Paralelo paralelo = obtenerParaleloPorId(idParalelo);
if (paralelo != null) {
boolean nuevoEstado = !paralelo.getEstado();  // Usando isEstado()
return cambiarEstadoParalelo(idParalelo, nuevoEstado);
}
return false;
}
}