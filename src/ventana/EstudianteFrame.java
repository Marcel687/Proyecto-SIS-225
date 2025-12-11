package ventana;

import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import clases.Usuario;

public class EstudianteFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Usuario usuarioLogueado;
    

    public EstudianteFrame(Usuario usuario) {
    	this.usuarioLogueado = usuario;

        setTitle("Sistema de Gestión Académica - Estudiante");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 720);
        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(245, 246, 250));
        setLayout(new BorderLayout());

        // ================= CONTENEDOR GENERAL =================
        contentPane = new JPanel(new BorderLayout(25, 25));
        contentPane.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));
        contentPane.setBackground(new Color(245, 246, 250));
        add(contentPane, BorderLayout.CENTER);

        // ================= ENCABEZADO ELEGANTE =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(18, 25, 18, 25)
        ));

        JLabel lblTitulo = new JLabel("Panel de Estudiante");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(32, 32, 32));

        header.add(lblTitulo, BorderLayout.WEST);

        // ---------- Panel usuario + botón salir ----------
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 4));
        rightPanel.setOpaque(false);

        JLabel lblUsuario = new JLabel(usuarioLogueado.getUsername().toUpperCase());
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblUsuario.setForeground(new Color(80, 80, 80));
        rightPanel.add(lblUsuario);

        JButton btnSalir = new JButton("Cerrar Sesión");
        btnSalir.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setBackground(new Color(55, 55, 55));
        btnSalir.setFocusPainted(false);
        btnSalir.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSalir.addActionListener(e -> cerrarSesion());
        rightPanel.add(btnSalir);

        header.add(rightPanel, BorderLayout.EAST);

        contentPane.add(header, BorderLayout.NORTH);

        // ================= GRID DE OPCIONES =================
        JPanel grid = new JPanel(new GridLayout(3, 2, 25, 25));
        grid.setOpaque(false);

        grid.add(crearBotonDashboard("Datos Usuario"));
        grid.add(crearBotonDashboard("Ver Horarios"));
        grid.add(crearBotonDashboard("Ver Materias"));
        grid.add(crearBotonDashboard("Ver Materias Inscritas"));
        grid.add(crearBotonDashboard("Ver Reporte"));
        grid.add(crearBotonDashboard("Ver Notas"));

        contentPane.add(grid, BorderLayout.CENTER);

        // ================= FOOTER =================
        JLabel footer = new JLabel("Sistema Integrado de Gestión Académica © 2025", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setForeground(new Color(130, 130, 130));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        add(footer, BorderLayout.SOUTH);
    }
    
    private JButton crearBotonDashboard(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btn.setForeground(new Color(45, 45, 45));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                BorderFactory.createEmptyBorder(22, 20, 22, 20)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(245, 245, 245));
                btn.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170), 1));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210), 1));
            }
        });

        btn.addActionListener(e -> {
            String opcion = btn.getText().trim();

            if (opcion.equalsIgnoreCase("Datos Usuario")) {
            	DatosUsuariosFrame datos = new DatosUsuariosFrame(usuarioLogueado);
                datos.setVisible(true);
            } 
            else if (opcion.equalsIgnoreCase("Ver Materias")) {
            	VerMaterias datos = new VerMaterias(usuarioLogueado);
                datos.setVisible(true);
            } 
            else if (opcion.equalsIgnoreCase("Ver Materias Inscritas")) {
            	VerMateriasInscritas datos = new VerMateriasInscritas(usuarioLogueado);
                datos.setVisible(true);
            } 
            else if (opcion.equalsIgnoreCase("Ver Reporte")) {
            	VerKardexLFrame datos = new VerKardexLFrame(usuarioLogueado);
                datos.setVisible(true);
            } 
            else if (opcion.equalsIgnoreCase("Ver Horarios")) {
            	VerHorarioFrame datos = new VerHorarioFrame(usuarioLogueado);
                datos.setVisible(true);
            } 
            else if (opcion.equalsIgnoreCase("Ver Notas")) {
            	VerNotasParciales datos = new VerNotasParciales(usuarioLogueado);
                datos.setVisible(true);
            }
            else {
                JOptionPane.showMessageDialog(
                    this,
                    "Módulo en desarrollo: " + opcion,
                    "Administración",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        return btn;
    }


    // ------------------- Botón principal grande --------------------
    private JButton crearBotonPrincipal(String texto) {
        JButton btn = crearBotonDashboard(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setPreferredSize(new Dimension(700, 90));
        return btn;
    }

    // ==============================================================
    //                    CERRAR SESIÓN
    // ==============================================================

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cerrar la sesión actual?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            new ventana.LoginFrame().setVisible(true);
        }
    }
}
