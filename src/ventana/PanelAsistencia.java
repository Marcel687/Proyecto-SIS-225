package ventana;

import dao.GestionAcademicaDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PanelAsistencia extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private GestionAcademicaDAO dao;
    private JSpinner dateSpinner;
    private int idParalelo;
    
    // Usamos nuestra clase interna 'EstadoItem' en lugar del DTO externo
    private List<EstadoItem> listaEstados;

    public PanelAsistencia(int idParalelo) {
        this.idParalelo = idParalelo;
        this.dao = new GestionAcademicaDAO();
        
        // Inicializamos los datos aquí mismo
        inicializarEstados();

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- PANEL SUPERIOR ---
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));

        JLabel lblFecha = new JLabel("Fecha de Asistencia:");
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 14));

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        dateSpinner.setPreferredSize(new Dimension(130, 30));
        dateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnCargar = new JButton("Consultar");
        estilizarBoton(btnCargar, new Color(70, 130, 180));
        btnCargar.addActionListener(e -> cargarDatos());

        panelSuperior.add(lblFecha);
        panelSuperior.add(dateSpinner);
        panelSuperior.add(btnCargar);
        add(panelSuperior, BorderLayout.NORTH);

        // --- TABLA CENTRAL ---
        String[] columnas = {"ID", "Estudiante", "Estado"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; 
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if(columnIndex == 2) return EstadoItem.class; // Usamos la clase interna
                return String.class;
            }
        };

        table = new JTable(model);
        estilizarTabla();
        
        // Configurar ComboBox
        JComboBox<EstadoItem> comboEstados = new JComboBox<>();
        for (EstadoItem estado : listaEstados) {
            comboEstados.addItem(estado);
        }
        comboEstados.setRenderer(new EstadoVisualRenderer());
        
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboEstados));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- PANEL INFERIOR ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setBackground(new Color(245, 245, 245));

        JButton btnGuardar = new JButton("Guardar Asistencia");
        estilizarBoton(btnGuardar, new Color(34, 139, 34));
        btnGuardar.addActionListener(e -> guardarAsistencia());
        
        panelInferior.add(btnGuardar);
        add(panelInferior, BorderLayout.SOUTH);

        cargarDatos();
    }
    
    // --- LÓGICA INTERNA DE DATOS ---
    private void inicializarEstados() {
        listaEstados = new ArrayList<>();
        // Definimos ID, Nombre y Color directamente aquí
        listaEstados.add(new EstadoItem(1, "Presente", new Color(34, 139, 34))); // Verde
        listaEstados.add(new EstadoItem(2, "Ausente", new Color(220, 20, 60)));  // Rojo
        listaEstados.add(new EstadoItem(3, "Tarde", new Color(255, 140, 0)));    // Naranja
        listaEstados.add(new EstadoItem(4, "Justificado", new Color(30, 144, 255))); // Azul
    }
   
    private void cargarDatos() {
        // 1. Limpiar visualmente la tabla antes de cargar
        model.setRowCount(0); 
        
        Date fechaSeleccionada = (Date) dateSpinner.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaSQL = sdf.format(fechaSeleccionada);

        // 2. Traer datos frescos de la BD
        List<Object[]> datos = dao.obtenerAsistenciaPorFecha(idParalelo, fechaSQL);

        if (datos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay estudiantes inscritos en este paralelo.");
            return;
        }

        // 3. Llenar el modelo
        for (Object[] fila : datos) {
            int idInsc = (int) fila[0];
            String nombre = (String) fila[1];
            
            // Convertir de forma segura el objeto de BD a entero
            int idEstadoBD = 1; 
            if (fila[2] != null) {
                idEstadoBD = Integer.parseInt(fila[2].toString());
            }

            // Buscar el EstadoItem correspondiente en la lista local
            int finalId = idEstadoBD;
            EstadoItem estadoObjeto = listaEstados.stream()
                .filter(e -> e.id == finalId)
                .findFirst()
                .orElse(listaEstados.get(0)); // Default Presente

            model.addRow(new Object[]{idInsc, nombre, estadoObjeto});
        }
        
        // 4. Forzar repintado de la tabla (Clave para solucionar tu error)
        model.fireTableDataChanged(); 
        table.repaint();
    }
/*
    private void cargarDatos() {
        model.setRowCount(0);
        Date fechaSeleccionada = (Date) dateSpinner.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaSQL = sdf.format(fechaSeleccionada);

        List<Object[]> datos = dao.obtenerAsistenciaPorFecha(idParalelo, fechaSQL);

        for (Object[] fila : datos) {
            int idInsc = (int) fila[0];
            String nombre = (String) fila[1];
            
            // Determinar ID del estado que viene de la BD
            int idEstadoBD = 1; 
            if (fila[2] instanceof Number) {
                idEstadoBD = ((Number) fila[2]).intValue();
            }

            // Buscar en nuestra lista interna
            int finalId = idEstadoBD;
            EstadoItem estadoObjeto = listaEstados.stream()
                .filter(e -> e.id == finalId)
                .findFirst()
                .orElse(listaEstados.get(0));

            model.addRow(new Object[]{idInsc, nombre, estadoObjeto});
        }
    }*/

    private void guardarAsistencia() {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();

        Date fechaSeleccionada = (Date) dateSpinner.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaSQL = sdf.format(fechaSeleccionada);

        try {
            for (int i = 0; i < model.getRowCount(); i++) {
                int idInscripcion = Integer.parseInt(model.getValueAt(i, 0).toString());
                EstadoItem estadoSeleccionado = (EstadoItem) model.getValueAt(i, 2);
                
                dao.registrarAsistencia(idInscripcion, estadoSeleccionado.id, fechaSQL);
            }
            JOptionPane.showMessageDialog(this, "Asistencia guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }

    private void estilizarTabla() {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(50, 50, 50));
        table.getTableHeader().setForeground(Color.WHITE);
        
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.getColumnModel().getColumn(2).setCellRenderer(new TableCellRenderer() {
            EstadoVisualRenderer renderer = new EstadoVisualRenderer();
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return renderer.getListCellRendererComponent(null, (EstadoItem) value, 0, isSelected, hasFocus);
            }
        });
    }

    private void estilizarBoton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private class EstadoItem {
        int id;
        String nombre;
        Color color;

        public EstadoItem(int id, String nombre, Color color) {
            this.id = id;
            this.nombre = nombre;
            this.color = color;
        }

        @Override
        public String toString() { return nombre; }
    }

    private class EstadoVisualRenderer extends JLabel implements ListCellRenderer<EstadoItem> {
        public EstadoVisualRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends EstadoItem> list, EstadoItem value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                setText(value.nombre);
                setIcon(new Icon() {
                    @Override
                    public void paintIcon(Component c, Graphics g, int x, int y) {
                        g.setColor(value.color);
                        g.fillOval(x, y, 12, 12);
                        g.setColor(Color.GRAY);
                        g.drawOval(x, y, 12, 12);
                    }
                    @Override
                    public int getIconWidth() { return 20; }
                    @Override
                    public int getIconHeight() { return 12; }
                });
            }
            if (isSelected) {
                setBackground(new Color(220, 240, 255));
                setForeground(Color.BLACK);
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
            return this;
        }
    }
}