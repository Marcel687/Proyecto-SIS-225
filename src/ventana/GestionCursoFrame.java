package ventana;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GestionCursoFrame extends JFrame {

    private int idParalelo;
    private String nombreMateria;

    public GestionCursoFrame(int idParalelo, String nombreMateria) {
        this.idParalelo = idParalelo;
        this.nombreMateria = nombreMateria;
        
        setTitle("Gestión de Curso: " + nombreMateria);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cerrar esta ventana

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // Título
        JLabel lblTitulo = new JLabel("Gestión Académica - " + nombreMateria);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblTitulo, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // Agregar los paneles
        tabbedPane.addTab("Calificaciones", new PanelNotas(idParalelo));
        tabbedPane.addTab("Registro Asistencia", new PanelAsistencia(idParalelo));
        tabbedPane.addTab("Resumen Asistencia", new PanelResumenAsistencia(idParalelo));
    }
}