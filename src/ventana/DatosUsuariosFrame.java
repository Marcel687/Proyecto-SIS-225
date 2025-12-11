package ventana;

import javax.swing.*;

import clases.Estudiante;
import clases.Usuario;

import java.awt.*;
import java.awt.image.BufferedImage;
import dao.EstudianteDAO;

public class DatosUsuariosFrame extends JFrame {

    // === CAMPOS QUE SE LLENARÁN DESDE LA BASE DE DATOS ===
    private JLabel lblNombreCompleto;
    private JLabel lblCarrera;
    private JLabel lblCI;
    private JLabel lblCelular;
    private JLabel lblPadre;
    private JLabel lblMatricula;
    
    private Usuario usuarioLogueado;

    public DatosUsuariosFrame(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        initComponents();
        cargarDatosDesdeBD(); // <-- AQUÍ SE CARGAN LOS DATOS REALES
    }


    private void initComponents() {
        setTitle("Sistema de Gestión Académica");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(750, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);
        getContentPane().add(mainPanel);

        // === Barra superior ===
        JLabel lblIconoGorrito = new JLabel("🎓");
        lblIconoGorrito.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));
        lblIconoGorrito.setBounds(20, 15, 60, 60);
        mainPanel.add(lblIconoGorrito);

        JLabel lblTitulo = new JLabel("Pagina Datos Usuario (estudiante)");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(90, 25, 400, 30);
        mainPanel.add(lblTitulo);

        JLabel lblNombreUsuarioHeader = new JLabel(usuarioLogueado.getUsername().toUpperCase());
        lblNombreUsuarioHeader.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNombreUsuarioHeader.setBounds(500, 25, 150, 25);
        mainPanel.add(lblNombreUsuarioHeader);

        JLabel lblIconoUsuario = new JLabel("👤");
        lblIconoUsuario.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        lblIconoUsuario.setBounds(660, 20, 60, 60);
        mainPanel.add(lblIconoUsuario);

        // === Foto de perfil circular ===
        JLabel lblFotoPerfil = new JLabel();
        lblFotoPerfil.setIcon(createCircularPlaceholder());
        lblFotoPerfil.setBounds(40, 100, 120, 120);
        mainPanel.add(lblFotoPerfil);

        // === Etiquetas y campos de texto (vacíos al inicio) ===
        int xIzq = 180;
        int xDer = 450;
        int y = 120;

        // Izquierda
        JLabel lblNom = new JLabel("Nombre Usuario:");
        lblNom.setFont(new Font("Arial", Font.BOLD, 14));
        lblNom.setBounds(xIzq, y, 140, 25);
        mainPanel.add(lblNom);

        lblNombreCompleto = new JLabel("");
        lblNombreCompleto.setFont(new Font("Arial", Font.PLAIN, 14));
        lblNombreCompleto.setBounds(xIzq, y + 25, 300, 25);
        mainPanel.add(lblNombreCompleto);

        JLabel lblCar = new JLabel("Carrera:");
        lblCar.setBounds(xIzq, y + 60, 100, 25);
        mainPanel.add(lblCar);

        lblCarrera = new JLabel("");
        lblCarrera.setBounds(xIzq + 110, y + 60, 200, 25);
        mainPanel.add(lblCarrera);

        JLabel lblCi = new JLabel("CI:");
        lblCi.setBounds(xIzq, y + 90, 100, 25);
        mainPanel.add(lblCi);

        lblCI = new JLabel("");
        lblCI.setBounds(xIzq + 110, y + 90, 150, 25);
        mainPanel.add(lblCI);

        JLabel lblCel = new JLabel("Celular:");
        lblCel.setBounds(xIzq, y + 120, 100, 25);
        mainPanel.add(lblCel);

        lblCelular = new JLabel("");
        lblCelular.setBounds(xIzq + 110, y + 120, 150, 25);
        mainPanel.add(lblCelular);

        // Derecha
        JLabel lblCor = new JLabel("Correo:");
        lblCor.setBounds(xDer, y + 60, 130, 25);
        mainPanel.add(lblCor);

        lblPadre = new JLabel("");
        lblPadre.setBounds(xDer + 140, y + 60, 200, 25);
        mainPanel.add(lblPadre);

        JLabel lblMat = new JLabel("Matricula:");
        lblMat.setBounds(xDer, y + 90, 130, 25);
        mainPanel.add(lblMat);

        lblMatricula = new JLabel("");
        lblMatricula.setBounds(xDer + 140, y + 90, 200, 25);
        mainPanel.add(lblMatricula);
    }

    // Foto de perfil circular placeholder
    private ImageIcon createCircularPlaceholder() {
        int size = 120;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(220, 220, 220));
        g2.fillOval(0, 0, size, size);

        g2.setColor(new Color(100, 100, 100));
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 70));
        g2.drawString("Person Icon", 15, 85);

        g2.dispose();
        return new ImageIcon(image);
    }

    // === MÉTODO PARA CARGAR DATOS DESDE LA BASE DE DATOS ===
    public void cargarDatosEstudiante(
            String nombre, String carrera, String ci, String celular,
            String celularRef, String padre, String madre, String ubicacion) {

        lblNombreCompleto.setText(nombre);
        lblCarrera.setText(carrera);
        lblCI.setText(ci);
        lblCelular.setText(celular);
        lblPadre.setText(padre);
        lblMatricula.setText(madre);
    }
    
    private void cargarDatosDesdeBD() {
        EstudianteDAO dao = new EstudianteDAO();
        Estudiante est = dao.obtenerPorUsername(usuarioLogueado.getUsername());

        if (est == null) {
            JOptionPane.showMessageDialog(this, "No se encontró información del estudiante.");
            return;
        }

        lblNombreCompleto.setText(est.getNombres() + " " + est.getApellidos());
        lblCarrera.setText(est.getCarrera());
        lblCI.setText(est.getCarnet());
        lblCelular.setText(String.valueOf(est.getTelefono()));
        lblPadre.setText(est.getCorreo());
        lblMatricula.setText(est.getMatricula());
    }
}