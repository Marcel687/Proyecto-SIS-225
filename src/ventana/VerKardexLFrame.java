package ventana;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.util.List;

import clases.KardexL;
import clases.Usuario;
import dao.KardexLDAO;
import dao.UsuarioDAO;

import javax.swing.table.DefaultTableModel;

public class VerKardexLFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Color BACKGROUND_COLOR = new Color(245, 246, 250);
    private static final Color HEADER_COLOR = new Color(32, 32, 32);
    private static final Color BUTTON_COLOR = new Color(55, 55, 55);
    private static final Color BUTTON_HOVER_COLOR = new Color(80, 80, 80);
    private static final Color TEXT_COLOR = new Color(80, 80, 80);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    private JPanel contentPane;
    private Usuario usuarioLogueado;
    private JTextField textFieldAnio;
    private JComboBox<String> comboSemestre;
    private JTable Kardex;
    private JLabel lblPromedio;


    public VerKardexLFrame(Usuario usuario) {
        this.usuarioLogueado = usuario;
        initializeUI();
        cargarKardexCompleto();  // <<--- MOSTRAR TODO AL ABRIR
    }


    private void initializeUI() {
        setTitle("Sistema de Gestión Académica - Estudiante");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(960, 720);
        setLocationRelativeTo(null);

        // Inicializar contentPane
        contentPane = new JPanel(new BorderLayout(25, 25));
        contentPane.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));
        contentPane.setBackground(BACKGROUND_COLOR);
        getContentPane().add(contentPane, BorderLayout.CENTER);

        // Panel norte con header + filtros
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(createHeader());
        northPanel.add(createFilterPanelWithButton());
        contentPane.add(northPanel, BorderLayout.NORTH);

        // Tabla
        Kardex = new JTable();
        Kardex.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"Semestre", "Año", "Materia", "Nota Final", "Estado"}
        ));
        JScrollPane scrollPane = new JScrollPane(Kardex);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Panel de promedio
        JPanel promedioPanel = createPromedioPanel();
        contentPane.add(promedioPanel, BorderLayout.SOUTH);
    }


    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(18, 25, 18, 25)
        ));

        JLabel lblTitulo = new JLabel("Panel de Historial Academico");
        lblTitulo.setFont(TITLE_FONT);
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 4));
        rightPanel.setOpaque(false);

        JLabel lblUsuario = new JLabel(usuarioLogueado.getUsername().toUpperCase());
        lblUsuario.setFont(LABEL_FONT);
        lblUsuario.setForeground(Color.WHITE);
        rightPanel.add(lblUsuario);

        JButton btnSalir = new JButton("Cerrar Sesión");
        btnSalir.setFont(BUTTON_FONT);
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setBackground(BUTTON_COLOR);
        btnSalir.setFocusPainted(false);
        btnSalir.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSalir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSalir.setBackground(BUTTON_HOVER_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSalir.setBackground(BUTTON_COLOR);
            }
        });

        btnSalir.addActionListener(e -> cerrarSesion());
        rightPanel.add(btnSalir);

        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createFilterPanelWithButton() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(BACKGROUND_COLOR);

        comboSemestre = new JComboBox<>(new String[]{"1", "2"});
        comboSemestre.setFont(LABEL_FONT);
        panel.add(comboSemestre);

        JLabel lblSemestre = new JLabel("Semestre");
        lblSemestre.setFont(LABEL_FONT);
        lblSemestre.setForeground(TEXT_COLOR);
        panel.add(lblSemestre);

        textFieldAnio = new JTextField();
        textFieldAnio.setFont(LABEL_FONT);
        textFieldAnio.setColumns(10);
        panel.add(textFieldAnio);

        JLabel lblAnio = new JLabel("Año");
        lblAnio.setFont(LABEL_FONT);
        lblAnio.setForeground(TEXT_COLOR);
        panel.add(lblAnio);

        JButton btnFiltrarInfo = new JButton("Filtrar");
        btnFiltrarInfo.setFont(BUTTON_FONT);
        btnFiltrarInfo.setForeground(new Color(139, 255, 246));
        btnFiltrarInfo.setBackground(BUTTON_COLOR);
        btnFiltrarInfo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnFiltrarInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnFiltrarInfo.setBackground(BUTTON_HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnFiltrarInfo.setBackground(BUTTON_COLOR);
            }
        });

        // Acción del botón
        btnFiltrarInfo.addActionListener(e -> cargarKardex());

        panel.add(btnFiltrarInfo);

        return panel;
    }

    private void cargarKardex() {

        int semestre = Integer.parseInt(comboSemestre.getSelectedItem().toString());
        int anio = Integer.parseInt(textFieldAnio.getText());

        int idEstudiante = new UsuarioDAO()
                .getIdEstudiantePorUsuario(usuarioLogueado.getIdUsuario());

        KardexLDAO dao = new KardexLDAO();

        // Reutilizamos el método que ya tienes:
        List<KardexL> lista = dao.obtenerKardexPorEstudianteYSemestre(idEstudiante, semestre);

        DefaultTableModel model = (DefaultTableModel) Kardex.getModel();
        model.setRowCount(0);

        for (KardexL k : lista) {
            if (k.getAnio() == anio) {
                model.addRow(new Object[]{
                    k.getSemestre(),
                    k.getAnio(),
                    k.getMateria(),
                    k.getNotaFinal(),
                    k.getEstado()
                });
            }
        }
    }

    
    private void cargarKardexCompleto() {

        int idEstudiante = new UsuarioDAO()
                .getIdEstudiantePorUsuario(usuarioLogueado.getIdUsuario());

        KardexLDAO dao = new KardexLDAO();
        List<KardexL> lista = dao.obtenerKardexCompleto(idEstudiante);

        DefaultTableModel model = (DefaultTableModel) Kardex.getModel();
        model.setRowCount(0);

        for (KardexL k : lista) {
            model.addRow(new Object[]{
                k.getSemestre(),
                k.getAnio(),
                k.getMateria(),
                k.getNotaFinal(),
                k.getEstado()
            });
        }
        actualizarPromedio();
    }


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
    
    private JPanel createPromedioPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panel.setBackground(BACKGROUND_COLOR);

        lblPromedio = new JLabel("Promedio: 0.0");
        lblPromedio.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPromedio.setForeground(TEXT_COLOR);

        panel.add(lblPromedio);

        return panel;
    }
    
    private void actualizarPromedio() {
        DefaultTableModel model = (DefaultTableModel) Kardex.getModel();
        int rowCount = model.getRowCount();
        if (rowCount == 0) {
            lblPromedio.setText("Promedio: 0.0");
            return;
        }

        double suma = 0;
        for (int i = 0; i < rowCount; i++) {
            Object notaObj = model.getValueAt(i, 3); // columna 3 = Nota Final
            if (notaObj != null) {
                try {
                    suma += Double.parseDouble(notaObj.toString());
                } catch (NumberFormatException e) {
                    // Ignorar si no es un número válido
                }
            }
        }

        double promedio = suma / rowCount;
        lblPromedio.setText(String.format("Promedio: %.2f", promedio));
    }


}
