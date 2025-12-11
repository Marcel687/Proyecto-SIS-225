package ventana;

import dao.UsuarioDAO;
import clases.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginFrame() {
        setTitle("Sistema Académico - Inicio de Sesión");
        setSize(430, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== Fondo general =====
        getContentPane().setBackground(new Color(240, 240, 240));
        setLayout(new GridBagLayout());

        // ===== Panel principal =====
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(360, 260));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== Título =====
        JLabel lblTitulo = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(30, 60, 120));
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // ===== Usuario =====
        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                txtUsuario.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.add(txtUsuario, gbc);

        // ===== Contraseña =====
        gbc.gridx = 0; 
        gbc.gridy++;
        panel.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                txtPassword.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.add(txtPassword, gbc);

        // ===== Botón Login =====
        gbc.gridx = 0; 
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton btnLogin = new JButton("Ingresar");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(150, 40));
        btnLogin.setBackground(new Color(40, 120, 200));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        panel.add(btnLogin, gbc);

        // ===== Acción =====
        btnLogin.addActionListener(this::loginAction);

        // Agregar panel al centro
        add(panel);

        setVisible(true);
    }

    private void loginAction(ActionEvent e) {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(user, pass);

        if (usuario != null) {
            dispose();
            abrirVentanaSegunRol(usuario);
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirVentanaSegunRol(Usuario usuario) {
        JFrame frame = null;
        String rol = usuario.getNombreRol().toLowerCase();

        switch (rol) {
            case "estudiante":
                frame = new EstudianteFrame(usuario);
                break;
            case "docente":
            	frame = new DocenteParalelosFrame(usuario.getIdUsuario());
                break;
            case "administrador":
            case "admin":
                frame = new AdminFrame(usuario);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Rol no reconocido");
                return;
        }

        if (frame != null) {
            frame.setVisible(true);
        }
    }
}

/**





























package ventana;

import dao.DocenteDAO;
import dao.UsuarioDAO;
import clases.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginFrame() {
        setTitle("Sistema Académico - Inicio de Sesión");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblTitulo = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        JButton btnLogin = new JButton("Ingresar");
        btnLogin.setPreferredSize(new Dimension(150, 40));
        add(btnLogin, gbc);

        btnLogin.addActionListener(this::loginAction);

        setVisible(true);
    }

    private void loginAction(ActionEvent e) {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(user, pass);

        if (usuario != null) {
            dispose(); // cerrar login
            abrirVentanaSegunRol(usuario);
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirVentanaSegunRol(Usuario usuario) {
        JFrame frame = null;
        String rol = usuario.getNombreRol().toLowerCase();

        switch (rol) {
            case "estudiante":
                frame = new EstudianteFrame();
                break;
            case "docente":
                frame = new DocenteParalelosFrame(usuario.getIdUsuario());
                break;
            case "administrador":
                //frame = new AdminFrame();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Rol no reconocido");
                return;
        }

        if (frame != null) {
            frame.setVisible(true);
        }
    }
}**/