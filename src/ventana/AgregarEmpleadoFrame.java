package ventana;

import dao.EmpleadoDAO;
import javax.swing.*;
import java.awt.*;

public class AgregarEmpleadoFrame extends JFrame {

    private JTextField txtNombres, txtApellidos, txtCarnet;
    private JTextField txtCorreo, txtTelefono;
    private JTextField txtUsername, txtCargo;
    private JPasswordField txtPassword;

    public AgregarEmpleadoFrame() {

        setTitle("Registrar Administrador / Empleado");
        setSize(600, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // ───────────────────── TÍTULO ─────────────────────
        JLabel titulo = new JLabel("Registro de Administrador / Empleado", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(new Color(30, 30, 30));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 25, 10));
        panel.add(titulo, BorderLayout.NORTH);

        // ───────────────────── FORM PANEL ─────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(250, 250, 250));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        txtNombres   = agregarCampo(formPanel, "Nombres:", row++);
        txtApellidos = agregarCampo(formPanel, "Apellidos:", row++);
        txtCarnet    = agregarCampo(formPanel, "Carnet:", row++);
        txtCorreo    = agregarCampo(formPanel, "Correo:", row++);
        txtTelefono  = agregarCampo(formPanel, "Teléfono:", row++);
        txtUsername  = agregarCampo(formPanel, "Usuario:", row++);

        txtPassword = new JPasswordField(25);
        agregarCampoPersonalizado(formPanel, "Contraseña:", txtPassword, row++);

        txtCargo = agregarCampo(formPanel, "Cargo (ej. ADMINISTRADOR, SECRETARIA):", row++);

        panel.add(formPanel, BorderLayout.CENTER);

        // ───────────────────── BOTONES ─────────────────────
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        botones.setBackground(new Color(250, 250, 250));

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(new Color(60, 105, 240));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setPreferredSize(new Dimension(150, 42));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(200, 200, 200));
        btnCancelar.setPreferredSize(new Dimension(150, 42));

        botones.add(btnGuardar);
        botones.add(btnCancelar);
        panel.add(botones, BorderLayout.SOUTH);

        add(new JScrollPane(panel));

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardar());

        setVisible(true);
    }

    // ───────────────────── GUARDAR ─────────────────────
    private void guardar() {
        String nombres   = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String carnet    = txtCarnet.getText().trim().toUpperCase();
        String correo    = txtCorreo.getText().trim();
        String telefono  = txtTelefono.getText().trim();
        String username  = txtUsername.getText().trim();
        String password  = new String(txtPassword.getPassword());
        String cargo     = txtCargo.getText().trim().toUpperCase();

        if (nombres.isEmpty() || apellidos.isEmpty() || carnet.isEmpty() ||
            correo.isEmpty() || username.isEmpty() || password.isEmpty() || cargo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EmpleadoDAO dao = new EmpleadoDAO();
        boolean ok = dao.registrarEmpleado(nombres, apellidos, carnet, correo, telefono,
                                           username, password, cargo);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Administrador/Empleado registrado con éxito\nCarnet: " + carnet,
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al registrar.\nPosibles causas: usuario o carnet ya existen.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ───────────────────── MÉTODOS AUXILIARES ─────────────────────
    private JTextField agregarCampo(JPanel panel, String labelText, int row) {

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setPreferredSize(new Dimension(140, 25)); // ← FIJA ANCHO DEL LABEL

        JTextField campo = new JTextField(25);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;      // ← EVITA QUE LA COLUMNA IZQUIERDA SE EXPANDA
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;      // ← LA COLUMNA DEL TEXFIELD OCUPA EL ESPACIO
        panel.add(campo, gbc);

        return campo;
    }

    private void agregarCampoPersonalizado(JPanel panel, String labelText, JPasswordField campo, int row) {

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setPreferredSize(new Dimension(140, 25)); // ← MISMO ANCHO

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }
}
