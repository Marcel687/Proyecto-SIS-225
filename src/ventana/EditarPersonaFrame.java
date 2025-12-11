// ventana/EditarPersonaFrame.java
package ventana;

import dao.*;
import clases.Carrera;
import conexion.ConexionDB;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EditarPersonaFrame extends JFrame {

    private JTextField txtNombres, txtApellidos, txtCarnet, txtCorreo, txtTelefono;
    private JTextField txtMatricula, txtCargo;
    private JComboBox<Carrera> comboCarrera;
    private final int idPersona;
    private final String tipo;

    public EditarPersonaFrame(int idPersona, String tipo) {
        this.idPersona = idPersona;
        this.tipo = tipo;

        setTitle("Editar " + tipo);
        setSize(600, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        txtNombres = agregarCampo(panel, "Nombres:", y++);
        txtApellidos = agregarCampo(panel, "Apellidos:", y++);
        txtCarnet = agregarCampo(panel, "Carnet:", y++);
        txtCorreo = agregarCampo(panel, "Correo:", y++);
        txtTelefono = agregarCampo(panel, "Teléfono:", y++);

        if ("Estudiante".equals(tipo)) {
            comboCarrera = new JComboBox<>();
            cargarCarreras();
            agregarCampoCombo(panel, "Carrera:", comboCarrera, y++);
            txtMatricula = agregarCampo(panel, "Matrícula:", y++);
        } else if ("Administrador (Empleado)".equals(tipo)) {
            txtCargo = agregarCampo(panel, "Cargo:", y++);
        }

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(new Color(40, 167, 69));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGuardar.setPreferredSize(new Dimension(200, 48));
        btnGuardar.addActionListener(e -> guardarCambios());

        gbc.gridwidth = 2;
        gbc.gridy = y++;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnGuardar, gbc);

        add(new JScrollPane(panel));
        cargarDatosActuales();
        setVisible(true);
    }

    private void cargarDatosActuales() {
        String sql = "SELECT nombres, apellidos, carnet, correo, telefono FROM persona WHERE id_persona = ?";
        try (var conn = ConexionDB.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            var rs = ps.executeQuery();
            if (rs.next()) {
                txtNombres.setText(rs.getString("nombres"));
                txtApellidos.setText(rs.getString("apellidos"));
                txtCarnet.setText(rs.getString("carnet"));
                txtCorreo.setText(rs.getString("correo"));
                txtTelefono.setText(rs.getString("telefono"));
            }
        } catch (Exception e) { e.printStackTrace(); }

        if ("Estudiante".equals(tipo)) {
            String sqlEst = "SELECT e.id_carrera, e.matricula, c.nombre_carrera FROM estudiantes e " +
                            "JOIN carrera c ON e.id_carrera = c.id_carrera WHERE e.id_persona = ?";
            try (var conn = ConexionDB.getConnection();
                 var ps = conn.prepareStatement(sqlEst)) {
                ps.setInt(1, idPersona);
                var rs = ps.executeQuery();
                if (rs.next()) {
                    txtMatricula.setText(rs.getString("matricula"));
                    String nombreCarrera = rs.getString("nombre_carrera");
                    for (int i = 0; i < comboCarrera.getItemCount(); i++) {
                        if (comboCarrera.getItemAt(i).toString().equals(nombreCarrera)) {
                            comboCarrera.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        } else if ("Administrador (Empleado)".equals(tipo)) {
            String sqlEmp = "SELECT cargo FROM empleado WHERE id_persona = ?";
            try (var conn = ConexionDB.getConnection();
                 var ps = conn.prepareStatement(sqlEmp)) {
                ps.setInt(1, idPersona);
                var rs = ps.executeQuery();
                if (rs.next()) txtCargo.setText(rs.getString("cargo"));
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void guardarCambios() {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String carnet = txtCarnet.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombres.isEmpty() || apellidos.isEmpty() || carnet.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete los campos obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = false;
        if ("Estudiante".equals(tipo)) {
            Carrera c = (Carrera) comboCarrera.getSelectedItem();
            int idCarrera = c != null ? c.getIdCarrera() : 0;
            String matricula = txtMatricula.getText().trim();
            ok = new EstudianteDAO().actualizarEstudiante(idPersona, nombres, apellidos, carnet, correo, telefono, idCarrera, matricula);
        } else if ("Docente".equals(tipo)) {
            ok = new DocenteDAO().actualizarDocente(idPersona, nombres, apellidos, carnet, correo, telefono);
        } else if ("Administrador (Empleado)".equals(tipo)) {
            String cargo = txtCargo.getText().trim();
            ok = new EmpleadoDAO().actualizarEmpleado(idPersona, nombres, apellidos, carnet, correo, telefono, cargo);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, "Datos actualizados con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarCarreras() {
        CarreraDAO dao = new CarreraDAO();
        List<Carrera> lista = dao.listarCarreras();
        comboCarrera.removeAllItems();
        for (Carrera c : lista) comboCarrera.addItem(c);
    }

    private JTextField agregarCampo(JPanel panel, String label, int row) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(l, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(campo, gbc);
        return campo;
    }

    private void agregarCampoCombo(JPanel panel, String labelText, JComboBox<?> combo, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(label, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(combo, gbc);
    }
}