package ventana;

import dao.DocenteDAO;
import javax.swing.*;
import java.awt.*;

public class AgregarDocenteFrame extends JFrame {

    private JTextField txtNombres, txtApellidos, txtCarnet;
    private JTextField txtCorreo, txtTelefono;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public AgregarDocenteFrame() {
        setTitle("Registrar Docente");
        setSize(600, 620);  // Más compacta, sin campos extras
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Título
        JLabel titulo = new JLabel("Registro de Docente", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(new Color(30, 30, 30));
        panel.add(titulo, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(250, 250, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        txtNombres    = agregarCampo(formPanel, "Nombres:", row++);
        txtApellidos  = agregarCampo(formPanel, "Apellidos:", row++);
        txtCarnet     = agregarCampo(formPanel, "Carnet:", row++);
        txtCorreo     = agregarCampo(formPanel, "Correo:", row++);
        txtTelefono   = agregarCampo(formPanel, "Teléfono:", row++);
        txtUsername   = agregarCampo(formPanel, "Usuario:", row++);
        
        txtPassword = new JPasswordField(20);
        agregarCampoPersonalizado(formPanel, "Contraseña:", txtPassword, row++);

        panel.add(formPanel, BorderLayout.CENTER);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
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

    private void guardar() {
        String nombres   = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String carnet    = txtCarnet.getText().trim().toUpperCase();
        String correo    = txtCorreo.getText().trim();
        String telefono  = txtTelefono.getText().trim();
        String username  = txtUsername.getText().trim();
        String password  = new String(txtPassword.getPassword());

        if (nombres.isEmpty() || apellidos.isEmpty() || carnet.isEmpty() ||
            correo.isEmpty() || telefono.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DocenteDAO dao = new DocenteDAO();
        boolean ok = dao.registrarDocente(nombres, apellidos, carnet, correo, telefono,
                                          username, password);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Docente registrado con éxito\nCarnet: " + carnet,
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al registrar.\nPosibles causas: usuario o carnet ya existen.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Métodos auxiliares (iguales que en las ventanas anteriores)
    private JTextField agregarCampo(JPanel panel, String labelText, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        JTextField campo = new JTextField(25);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(label, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(campo, gbc);
        return campo;
    }

    private void agregarCampoPersonalizado(JPanel panel, String labelText, JPasswordField campo, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(label, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(campo, gbc);
    }
}