package ventana;

import dao.MateriaDAO;
import dao.ParaleloDAO;
import clases.Materia;
import clases.Paralelo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VerMateriaParaleloFrame extends JFrame {
    private JComboBox<String> comboTipo;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton btnModificar;
    private JButton btnHabilitarDeshabilitar;
    private JButton btnActualizar;
    private JTextField txtBusqueda;
    private JLabel lblResultados;

    private MateriaDAO materiaDAO;
    private ParaleloDAO paraleloDAO;

    public VerMateriaParaleloFrame() {
        materiaDAO = new MateriaDAO();
        paraleloDAO = new ParaleloDAO();

        setTitle("Gestión de Materias y Paralelos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);

        initComponents();
        cargarDatos("Materias");
    }

    private void initComponents() {
        // Panel superior con búsqueda
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tipo
        gbc.gridx = 0; gbc.gridy = 0;
        panelSuperior.add(new JLabel("Tipo:"), gbc);

        comboTipo = new JComboBox<>(new String[]{"Materias", "Paralelos"});
        comboTipo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipo = (String) comboTipo.getSelectedItem();
                cargarDatos(tipo);
                txtBusqueda.setText("");
            }
        });
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.2;
        panelSuperior.add(comboTipo, gbc);

        // Barra de búsqueda
        gbc.gridx = 2; gbc.gridy = 0;
        panelSuperior.add(new JLabel("Buscar:"), gbc);

        txtBusqueda = new JTextField(20);
        txtBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabla();
            }
        });
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.6;
        panelSuperior.add(txtBusqueda, gbc);

        // Botón Actualizar
        btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarDatos((String) comboTipo.getSelectedItem());
            }
        });
        gbc.gridx = 4; gbc.gridy = 0; gbc.weightx = 0.2;
        panelSuperior.add(btnActualizar, gbc);

        // Etiqueta de resultados
        lblResultados = new JLabel("Resultados: 0");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 5;
        panelSuperior.add(lblResultados, gbc);

        // Tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabla = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Renderer para colorear la columna de estado
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                // Colorear solo la columna de Estado
                if (value != null && value.toString().contains("Habilit")) {
                    c.setForeground(new Color(0, 100, 0)); // Verde oscuro
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (value != null && value.toString().contains("Deshabilit")) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
                
                return c;
            }
        });
        
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        JScrollPane scrollPane = new JScrollPane(tabla);

        // Panel inferior con botones
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarRegistro();
            }
        });
        panelInferior.add(btnModificar);

        btnHabilitarDeshabilitar = new JButton("Habilitar/Deshabilitar");
        btnHabilitarDeshabilitar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarEstadoRegistro();
            }
        });
        panelInferior.add(btnHabilitarDeshabilitar);

        // Layout principal
        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void cargarDatos(String tipo) {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);

        if (tipo.equals("Materias")) {
            cargarMaterias();
        } else {
            cargarParalelos();
        }
        
        ajustarAnchoColumnas();
        actualizarContadorResultados();
    }

    private void cargarMaterias() {
        String[] columnas = {"ID", "Código", "Nombre", "Carrera", "Créditos", "Semestre", "Prerrequisitos", "Estado"};
        modeloTabla.setColumnIdentifiers(columnas);

        try {
            String sql = "SELECT m.id_materia, m.codigo, m.nombre, m.creditos, m.semestre, " +
                        "m.prerequisitos, m.estado, c.nombre_carrera " +
                        "FROM materias m " +
                        "JOIN carrera c ON m.id_carrera = c.id_carrera " +
                        "ORDER BY m.codigo";
            
            java.sql.Connection conn = conexion.ConexionDB.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                boolean estado = rs.getBoolean("estado");
                String estadoTexto = estado ? "Habilitada" : "Deshabilitada";
                
                Object[] fila = {
                    rs.getInt("id_materia"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("nombre_carrera"),
                    rs.getInt("creditos"),
                    rs.getInt("semestre"),
                    rs.getString("prerequisitos") != null ? rs.getString("prerequisitos") : "Sin prerrequisitos",
                    estadoTexto
                };
                modeloTabla.addRow(fila);
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar materias: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarParalelos() {
        String[] columnas = {"ID", "Código", "Materia", "Paralelo", "Docente", "Semestre", "Año", "Cupo", "Estado"};
        modeloTabla.setColumnIdentifiers(columnas);

        try {
            String sql = "SELECT p.id_paralelo, m.codigo, m.nombre as materia_nombre, " +
                        "p.nombre as paralelo_nombre, " +
                        "CONCAT(per.nombres, ' ', per.apellidos) as docente_nombre, " +
                        "p.semestre, p.anio, p.cupo, p.estado " +
                        "FROM paralelos p " +
                        "JOIN materias m ON p.id_materia = m.id_materia " +
                        "JOIN docentes d ON p.id_docente = d.id_docente " +
                        "JOIN persona per ON d.id_persona = per.id_persona " +
                        "ORDER BY m.codigo, p.nombre";
            
            java.sql.Connection conn = conexion.ConexionDB.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                boolean estado = rs.getBoolean("estado");
                String estadoTexto = estado ? "Habilitado" : "Deshabilitado";
                
                Object[] fila = {
                    rs.getInt("id_paralelo"),
                    rs.getString("codigo"),
                    rs.getString("materia_nombre"),
                    rs.getString("paralelo_nombre"),
                    rs.getString("docente_nombre"),
                    rs.getString("semestre"),
                    rs.getInt("anio"),
                    rs.getInt("cupo"),
                    estadoTexto
                };
                modeloTabla.addRow(fila);
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar paralelos: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajustarAnchoColumnas() {
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            int ancho = 100;
            String columnName = tabla.getColumnName(i);
            
            if (columnName.equals("ID")) {
                ancho = 50;
            } else if (columnName.equals("Código")) {
                ancho = 80;
            } else if (columnName.equals("Nombre") || columnName.equals("Materia")) {
                ancho = 150;
            } else if (columnName.equals("Carrera") || columnName.equals("Docente")) {
                ancho = 120;
            } else if (columnName.equals("Paralelo")) {
                ancho = 80;
            } else if (columnName.equals("Prerrequisitos")) {
                ancho = 200;
            } else if (columnName.equals("Estado")) {
                ancho = 110;
            } else if (columnName.equals("Semestre") || columnName.equals("Año") || 
                       columnName.equals("Cupo") || columnName.equals("Créditos")) {
                ancho = 70;
            }
            tabla.getColumnModel().getColumn(i).setPreferredWidth(ancho);
        }
    }

    private void filtrarTabla() {
        String texto = txtBusqueda.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)" + texto);
                sorter.setRowFilter(rf);
            } catch (java.util.regex.PatternSyntaxException e) {
                return;
            }
        }
        actualizarContadorResultados();
    }

    private void actualizarContadorResultados() {
        int totalFilas = tabla.getRowCount();
        int filasVisibles = tabla.getRowSorter().getViewRowCount();
        lblResultados.setText("Mostrando " + filasVisibles + " de " + totalFilas + " registros");
    }

    private void modificarRegistro() {
        int filaSeleccionada = tabla.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un registro para modificar", 
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaSeleccionada);
        String tipo = (String) comboTipo.getSelectedItem();
        int id = (int) modeloTabla.getValueAt(filaModelo, 0);
        
        // Verificar estado usando los modelos
        boolean estaHabilitado = false;
        
        if (tipo.equals("Materias")) {
            Materia materia = materiaDAO.obtenerMateriaPorId(id);
            if (materia != null) {
                estaHabilitado = materia.getEstado();
            }
        } else {
            Paralelo paralelo = paraleloDAO.obtenerParaleloPorId(id);
            if (paralelo != null) {
                estaHabilitado = paralelo.getEstado();
            }
        }
        
        if (!estaHabilitado) {
            String textoEstado = tipo.equals("Materias") ? "deshabilitada" : "deshabilitado";
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "Este registro está " + textoEstado + ". ¿Desea habilitarlo primero antes de modificar?",
                    "Registro deshabilitado", JOptionPane.YES_NO_OPTION);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                alternarEstado(id, tipo);
            }
            return;
        }

        if (tipo.equals("Materias")) {
            ModificarMateriaFrame frameModificar = new ModificarMateriaFrame(this, id);
            frameModificar.setVisible(true);
        } else {
            ModificarParaleloFrame frameModificar = new ModificarParaleloFrame(this, id);
            frameModificar.setVisible(true);
        }
    }
    
    public void refrescarTabla() {
        String tipo = (String) comboTipo.getSelectedItem();
        cargarDatos(tipo);
    }

    private void cambiarEstadoRegistro() {
        int filaSeleccionada = tabla.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un registro para cambiar estado", 
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaSeleccionada);
        String tipo = (String) comboTipo.getSelectedItem();
        int id = (int) modeloTabla.getValueAt(filaModelo, 0);
        String nombre = modeloTabla.getValueAt(filaModelo, 2).toString();
        
        // Obtener estado actual usando los modelos
        boolean estadoActual = false;
        String estadoTexto = "";
        
        if (tipo.equals("Materias")) {
            Materia materia = materiaDAO.obtenerMateriaPorId(id);
            if (materia != null) {
                estadoActual = materia.getEstado();
                estadoTexto = estadoActual ? "Habilitada" : "Deshabilitada";
            }
        } else {
            Paralelo paralelo = paraleloDAO.obtenerParaleloPorId(id);
            if (paralelo != null) {
                estadoActual = paralelo.getEstado();
                estadoTexto = estadoActual ? "Habilitado" : "Deshabilitado";
            }
        }
        
        String accion = estadoActual ? "deshabilitar" : "habilitar";
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de " + accion + " " + tipo.toLowerCase() + ": " + nombre + "?\n" +
                "Estado actual: " + estadoTexto,
                "Confirmar cambio de estado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            alternarEstado(id, tipo);
        }
    }
    
    private void alternarEstado(int id, String tipo) {
        boolean exito = false;
        
        try {
            if (tipo.equals("Materias")) {
                // Obtener el estado actual
                Materia materia = materiaDAO.obtenerMateriaPorId(id);
                if (materia != null) {
                    boolean nuevoEstado = !materia.getEstado();
                    exito = materiaDAO.cambiarEstadoMateria(id, nuevoEstado);
                }
            } else {
                // Obtener el estado actual
                Paralelo paralelo = paraleloDAO.obtenerParaleloPorId(id);
                if (paralelo != null) {
                    boolean nuevoEstado = !paralelo.getEstado();
                    exito = paraleloDAO.cambiarEstadoParalelo(id, nuevoEstado);
                }
            }
            
            if (exito) {
                // Obtener nuevo estado para mostrar
                String nuevoEstadoTexto = "";
                if (tipo.equals("Materias")) {
                    Materia materia = materiaDAO.obtenerMateriaPorId(id);
                    nuevoEstadoTexto = materia.getEstado() ? "Habilitada" : "Deshabilitada";
                } else {
                    Paralelo paralelo = paraleloDAO.obtenerParaleloPorId(id);
                    nuevoEstadoTexto = paralelo.getEstado() ? "Habilitado" : "Deshabilitado";
                }
                
                JOptionPane.showMessageDialog(this, 
                        tipo + " " + nuevoEstadoTexto.toLowerCase() + " correctamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                refrescarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al cambiar el estado del registro",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método main para probar
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VerMateriaParaleloFrame().setVisible(true);
            }
        });
    }
}