package ventana;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import clases.Paralelo;
import clases.Usuario;
import dao.ParaleloDAO;
import dao.InscripcionDAO;
import dao.UsuarioDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerMateriasInscritas extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable Materias;
    private DefaultTableModel tableModel;
    private DefaultTableModel tableModelSinCupo;  // Nuevo modelo para la tabla sin 'Cupo'
    private Usuario usuarioLogueado;

    public VerMateriasInscritas(Usuario usuario) {
        this.usuarioLogueado = usuario; // Recibimos el usuario al constructor
        
        


        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 600);  // Aumentamos el tamaño de la ventana para que quepan los elementos
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        // ================= ENCABEZADO =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(18, 25, 18, 25)
        ));

        // Etiqueta para el título de la ventana
        JLabel lblTitulo = new JLabel("Ver Materias");
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

        // ================= PANEL DE DETALLES DE LA MATERIA =================
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        getContentPane().add(mainPanel, BorderLayout.WEST);  // Cambié la posición para que esté en el centro
        mainPanel.setLayout(new GridLayout(8, 1, 0, 0));  // Ahora tenemos 8 filas para incluir todos los detalles y tablas

        // JLabels para mostrar detalles de la materia
        JLabel Codigo = new JLabel("Código: ");
        Codigo.setBorder(new LineBorder(Color.BLACK));  // Contorno negro
        mainPanel.add(Codigo);

        JLabel Materia = new JLabel("Materia: ");
        Materia.setBorder(new LineBorder(Color.BLACK));  // Contorno negro
        mainPanel.add(Materia);

        JLabel Paralelo = new JLabel("Paralelo: ");
        Paralelo.setBorder(new LineBorder(Color.BLACK));  // Contorno negro
        mainPanel.add(Paralelo);

        JLabel Docente = new JLabel("Docente: ");
        Docente.setBorder(new LineBorder(Color.BLACK));  // Contorno negro
        mainPanel.add(Docente);

        JLabel Cupo = new JLabel("Cupo: ");
        Cupo.setBorder(new LineBorder(Color.BLACK));  // Contorno negro
        mainPanel.add(Cupo);

        JLabel Dias = new JLabel("Día: ");
        Dias.setBorder(new LineBorder(Color.BLACK));  // Contorno negro
        mainPanel.add(Dias);

        JLabel Horas = new JLabel("Horas: ");
        Horas.setBorder(new LineBorder(Color.BLACK));  // Contorno negro
        mainPanel.add(Horas);

        // ================= TABLA PARA MOSTRAR LAS MATERIAS CON 'Cupo' =================
        Materias = new JTable();
        tableModel = new DefaultTableModel(
            new String[]{"Código", "Materia", "Paralelo", "Docente", "Horario", "Día", "Cupo", "Id"},
            0
        );
        Materias.setModel(tableModel);
        Materias.setBorder(new LineBorder(Color.BLACK));  // Contorno negro a la tabla
        JScrollPane scrollPane1 = new JScrollPane(Materias);
        contentPane.add(scrollPane1, BorderLayout.CENTER);
        tableModelSinCupo = new DefaultTableModel(
            new String[]{"Código", "Materia", "Paralelo", "Docente", "Horario", "Día", "Id"},  // Sin "Cupo"
            0
        );
        
     // Ocultar la columna "Id" (índice 7) de la vista
        Materias.getColumnModel().getColumn(7).setMinWidth(0);
        Materias.getColumnModel().getColumn(7).setMaxWidth(0);
        Materias.getColumnModel().getColumn(7).setWidth(0);
        Materias.getColumnModel().getColumn(7).setPreferredWidth(0);
        Materias.getColumnModel().getColumn(7).setResizable(false);

        // ActionListener para detectar la selección de fila
        Materias.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = Materias.getSelectedRow();
                if (selectedRow >= 0) {
                    // Obtener los datos de la fila seleccionada
                    String codigo = tableModel.getValueAt(selectedRow, 0).toString();
                    String materia = tableModel.getValueAt(selectedRow, 1).toString();
                    String paralelo = tableModel.getValueAt(selectedRow, 2).toString();
                    String docente = tableModel.getValueAt(selectedRow, 3).toString();
                    String horario = tableModel.getValueAt(selectedRow, 4).toString();
                    String dia = tableModel.getValueAt(selectedRow, 5).toString();
                    String cupo = tableModel.getValueAt(selectedRow, 6).toString();

                    // Actualizar los JLabels
                    Codigo.setText("Código: " + codigo);
                    Materia.setText("Materia: " + materia);
                    Paralelo.setText("Paralelo: " + paralelo);
                    Docente.setText("Docente: " + docente);
                    Horas.setText("<html>Horario:<br>" + horario + "</html>");
                    Dias.setText("Día: " + dia);
                    Cupo.setText("Cupo: "+ cupo);
                }
            }
        });

        cargarTodosLosParalelos(); // Este método carga los paralelos en la tabla con "Cupo"
        
        // ================= FOOTER CON BOTÓN INSCRIBIRSE =================
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Panel para el footer
        footer.setBackground(Color.WHITE);
        
        JButton btnVerAsistencia = new JButton("Ver asistencia");
        footer.add(btnVerAsistencia);
        btnVerAsistencia.addActionListener(e -> abrirVentanaAsistencia());
        JButton btnSalirse = new JButton("Salir Materia");
        btnSalirse.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalirse.setBackground(new Color(0, 123, 255));
        btnSalirse.setForeground(new Color(251, 94, 68));
        footer.add(btnSalirse);
        contentPane.add(footer, BorderLayout.SOUTH);  // Agregar el footer a la parte inferior de la ventana
        
     // ================= LOGICA DE INSCRIPCIÓN =================
        btnSalirse.addActionListener(e -> {
            int selectedRow = Materias.getSelectedRow();

            if (selectedRow >= 0) {
                // Obtener el idParalelo de la fila seleccionada
                int idParalelo = Integer.parseInt(tableModel.getValueAt(selectedRow, 7).toString());

                // Obtener el idEstudiante usando el UsuarioDAO
                int idEstudiante = new UsuarioDAO().getIdEstudiantePorUsuario(usuarioLogueado.getIdUsuario()); // Obtener el idEstudiante

                if (idEstudiante == -1) {
                    JOptionPane.showMessageDialog(this, "⚠ No se pudo obtener el ID del estudiante.");
                    return;
                }

                // Crear una instancia de InscripcionDAO
                InscripcionDAO inscripcionDAO = new InscripcionDAO();

                // Llamar al método inscribirEnParalelo
                String mensaje = inscripcionDAO.salirDeParalelo(idEstudiante, idParalelo);

                // Mostrar el mensaje al usuario
                JOptionPane.showMessageDialog(this, mensaje);  // Muestra un mensaje emergente con el resultado de la inscripción
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Debes seleccionar un paralelo para inscribirte.");
            }
        });
    }

    private void cargarTodosLosParalelos() {
        // Obtén el ID del estudiante logueado
        int idEstudiante = new UsuarioDAO().getIdEstudiantePorUsuario(usuarioLogueado.getIdUsuario());

        // Llama a ParaleloDAO para obtener los paralelos del estudiante
        ParaleloDAO dao = new ParaleloDAO();
        List<Paralelo> paralelosInscritos = dao.listarParalelosPorEstudiante(idEstudiante);

        // Mapa para almacenar los paralelos ya procesados y concatenar los horarios y días
        Map<Integer, Paralelo> paralelosProcesados = new HashMap<>();

        // Recorrer la lista de paralelos y cargarlos en el mapa
        for (Paralelo p : paralelosInscritos) {
            // Verificar si el paralelo ya ha sido procesado
            if (paralelosProcesados.containsKey(p.getIdParalelo())) {
                Paralelo paraleloExistente = paralelosProcesados.get(p.getIdParalelo());

                // Concatenar los horarios
                String horarioExistente = paraleloExistente.getHoraInicio() + " - " + paraleloExistente.getHoraFin();
                String horarioNuevo = p.getHoraInicio() + " - " + p.getHoraFin();

                // Combinar los horarios
                String horariosCombinados = horarioExistente + ", " + horarioNuevo;

                // Concatenar los días
                String diasCombinados = paraleloExistente.getDia() + ", " + p.getDia();

                // Actualizar el paralelo existente con los horarios y días combinados
                paraleloExistente.setHoraInicio(horariosCombinados);
                paraleloExistente.setDia(diasCombinados);

            } else {
                // Si el paralelo no ha sido procesado, lo agregamos al mapa
                paralelosProcesados.put(p.getIdParalelo(), p);
            }
        }

        // Ahora añadimos los paralelos procesados a la tabla
        for (Paralelo p : paralelosProcesados.values()) {
            // Convertir los horarios a formato cadena solo cuando los mostramos en la tabla
            String horario = "";
            if (p.getHoraInicio() != null && p.getHoraFin() != null) {
                horario = p.getHoraInicio() + " - " + p.getHoraFin();
            }

            // Añadir la fila a la tabla
            tableModel.addRow(new Object[]{
                p.getCodigo(),
                p.getMateria(),
                p.getNombre(),
                p.getDocente(),
                horario, // Mostrar los horarios concatenados
                p.getDia(),
                p.getCupo(),
                p.getIdParalelo()  // Asegúrate de que esta columna esté visible
            });
        }
    }
    
    private void abrirVentanaAsistencia() {
        int fila = Materias.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una materia.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener el valor de la columna "Id" (índice 7) de la fila seleccionada
        Object value = Materias.getValueAt(fila, 7);  // Obtén el valor de la celda en índice 7

        try {
            // Verificar si el valor es un Integer y convertirlo
            int idParalelo = (value instanceof Integer) ? (Integer) value : Integer.parseInt(value.toString());

            // id del estudiante (ya lo tienes desde usuarioLogueado)
            int idEstudiante = new UsuarioDAO().getIdEstudiantePorUsuario(usuarioLogueado.getIdUsuario());

            // Crear una nueva ventana de asistencia
            VerAsistenciaFrame ventana = new VerAsistenciaFrame(idEstudiante, idParalelo);
            ventana.setVisible(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID del paralelo no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }





}
