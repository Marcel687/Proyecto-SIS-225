package ventana;

import java.awt.*;
import java.util.List;
import java.util.TreeMap;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import clases.Horario;
import clases.Usuario;
import dao.HorarioDAO;
import dao.UsuarioDAO;

public class VerHorarioFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Usuario usuarioLogueado;
    private JTable tableHorario;
    private JLabel lblDetalle;

    public VerHorarioFrame(Usuario usuario) {
        this.usuarioLogueado = usuario;
        initializeUI();
        cargarHorarioAlumno();
    }

    private void initializeUI() {
        setTitle("Horario del Estudiante");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        // ================= ENCABEZADO =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(18, 25, 18, 25)
        ));

        // Etiqueta para el título de la ventana
        JLabel lblTitulo = new JLabel("Horario del Estudiante");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(32, 32, 32));

        // Etiqueta para el nombre del usuario logueado
        JLabel lblUsuario = new JLabel("Usuario: " + usuarioLogueado.getUsername().toUpperCase());
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblUsuario.setForeground(new Color(80, 80, 80));

        // Panel de encabezado de usuario
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 4));
        rightPanel.setOpaque(false);
        rightPanel.add(lblUsuario);

        header.add(lblTitulo, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        contentPane.add(header, BorderLayout.NORTH);
        // ================= FIN ENCABEZADO =================

        // Tabla para mostrar el horario
        tableHorario = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableHorario);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para detalles de la materia
        JPanel panelDetalle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelDetalle.setBorder(BorderFactory.createTitledBorder("Detalles de la clase"));
        lblDetalle = new JLabel("Seleccione una materia para ver los detalles...");
        lblDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelDetalle.add(lblDetalle);
        contentPane.add(panelDetalle, BorderLayout.SOUTH);

        // Listener para selección de celda
        tableHorario.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                actualizarDetalle();
            }
        });
        
        tableHorario.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tableHorario.rowAtPoint(evt.getPoint());
                int col = tableHorario.columnAtPoint(evt.getPoint());

                if (fila >= 0 && col > 0) { // columna 0 = Horas
                    Object celda = tableHorario.getValueAt(fila, col);
                    if (celda != null && !celda.toString().isEmpty()) {
                        String[] partes = celda.toString().split(" - ");
                        if (partes.length == 3) {
                            String materia = partes[0];
                            String paralelo = partes[1];
                            String aula = partes[2];
                            String hora = tableHorario.getValueAt(fila, 0).toString();
                            String dia = tableHorario.getColumnName(col);

                            lblDetalle.setText(String.format(
                                "<html><b>Materia:</b> %s &nbsp; <b>Paralelo:</b> %s &nbsp; <b>Aula:</b> %s &nbsp; <b>Horario:</b> %s - %s</html>",
                                materia, paralelo, aula, dia, hora
                            ));
                        }
                    } else {
                        lblDetalle.setText("Seleccione una materia para ver los detalles...");
                    }
                }
            }
        });

    }

    private void cargarHorarioAlumno() {
        int idEstudiante = new UsuarioDAO().getIdEstudiantePorUsuario(usuarioLogueado.getIdUsuario());
        HorarioDAO horarioDAO = new HorarioDAO();
        List<Horario> listaHorarios = horarioDAO.obtenerHorarioPorEstudiante(idEstudiante);

        String[] dias = {"Horas", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};

        TreeMap<String, Integer> horasMapa = new TreeMap<>();
        for (Horario h : listaHorarios) {
            horasMapa.putIfAbsent(h.getHoras(), 0);
        }

        DefaultTableModel model = new DefaultTableModel();
        for (String dia : dias) {
            model.addColumn(dia);
        }

        for (String hora : horasMapa.keySet()) {
            Object[] fila = new Object[dias.length];
            fila[0] = hora;
            for (int i = 1; i < dias.length; i++) {
                fila[i] = "";
            }
            model.addRow(fila);
        }

        for (Horario h : listaHorarios) {
            int col = -1;
            switch (h.getDia().toLowerCase()) {
                case "lunes": col = 1; break;
                case "martes": col = 2; break;
                case "miercoles": col = 3; break;
                case "jueves": col = 4; break;
                case "viernes": col = 5; break;
            }
            if (col != -1) {
                for (int r = 0; r < model.getRowCount(); r++) {
                    if (model.getValueAt(r, 0).equals(h.getHoras())) {
                        model.setValueAt(h.getNombreM() + " - " + h.getNombreP() + " - " + h.getAula(), r, col);
                        break;
                    }
                }
            }
        }

        tableHorario.setModel(model);
    }

    private void actualizarDetalle() {
        int fila = tableHorario.getSelectedRow();
        int col = tableHorario.getSelectedColumn();

        if (fila >= 0 && col > 0) { // columna 0 = Horas
            Object celda = tableHorario.getValueAt(fila, col);
            if (celda != null && !celda.toString().isEmpty()) {
                String[] partes = celda.toString().split(" - ");
                if (partes.length == 3) {
                    String materia = partes[0];
                    String paralelo = partes[1];
                    String aula = partes[2];
                    String hora = tableHorario.getValueAt(fila, 0).toString();
                    String dia = tableHorario.getColumnName(col);
                    lblDetalle.setText(String.format(
                            "<html><b>Materia:</b> %s &nbsp; <b>Paralelo:</b> %s &nbsp; <b>Aula:</b> %s &nbsp; <b>Horario:</b> %s - %s</html>",
                            materia, paralelo, aula, dia, hora));
                }
            } else {
                lblDetalle.setText("Seleccione una materia para ver los detalles...");
            }
        }
    }
}
