package dao;

import conexion.ConexionDB;
import clases.Carrera;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarreraDAO {

    public List<Carrera> listarCarreras() {
        List<Carrera> lista = new ArrayList<>();
        String sql = "SELECT id_carrera, nombre_carrera FROM carrera ORDER BY nombre_carrera";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Carrera(
                        rs.getInt("id_carrera"),
                        rs.getString("nombre_carrera")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar carreras: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}