package ventana;

import dao.GestionAcademicaDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PanelResumenAsistencia extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private GestionAcademicaDAO dao;
    private int idParalelo;

    public PanelResumenAsistencia(int idParalelo) {
        this.idParalelo = idParalelo;
        this.dao = new GestionAcademicaDAO();

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Botón para actualizar el reporte
        JButton btnActualizar = new JButton("Actualizar Resumen");
        estilizarBoton(btnActualizar);
        btnActualizar.addActionListener(e -> cargarResumen());
        
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTop.setBackground(new Color(245, 245, 245));
        panelTop.add(btnActualizar);
        add(panelTop, BorderLayout.NORTH);

        // Tabla Inicial vacía
        table = new JTable();
        estilizarTabla();
        add(new JScrollPane(table), BorderLayout.CENTER);

        cargarResumen();
    }

    private void cargarResumen() {
        // 1. Obtener datos crudos de la BD
        List<Object[]> historial = dao.obtenerHistorialCompleto(idParalelo);

        if (historial.isEmpty()) {
            model = new DefaultTableModel(new String[]{"Sin datos"}, 0);
            table.setModel(model);
            return;
        }

        // 2. Procesar Datos: Identificar Fechas Únicas y Estudiantes Únicos
        Set<Date> fechasSet = new TreeSet<>(); // TreeSet ordena las fechas automáticamente
        Map<String, Map<Date, Integer>> dataMatriz = new LinkedHashMap<>(); // Mapa: Nombre -> {Fecha -> Estado}

        for (Object[] reg : historial) {
            String nombre = (String) reg[0];
            Date fecha = (Date) reg[1];
            int estado = (int) reg[2];

            fechasSet.add(fecha);

            dataMatriz.putIfAbsent(nombre, new HashMap<>());
            dataMatriz.get(nombre).put(fecha, estado);
        }

        List<Date> columnasFechas = new ArrayList<>(fechasSet);

        // 3. Construir Columnas del Modelo (Estudiante | Fecha 1 | Fecha 2... | % Asistencia)
        Vector<String> columnas = new Vector<>();
        columnas.add("Estudiante");
        for (Date d : columnasFechas) {
            columnas.add(new java.text.SimpleDateFormat("dd/MM").format(d));
        }
        columnas.add("% Asist.");

        // 4. Llenar Filas
        Vector<Vector<Object>> data = new Vector<>();
        
        for (Map.Entry<String, Map<Date, Integer>> entry : dataMatriz.entrySet()) {
            String estudiante = entry.getKey();
            Map<Date, Integer> asistenciaEstudiante = entry.getValue();
            
            Vector<Object> fila = new Vector<>();
            fila.add(estudiante);

            int contadorAsistencias = 0; // Presente (1), Tarde (3), Justificado (4) suman
            int totalClases = columnasFechas.size();

            for (Date fecha : columnasFechas) {
                Integer estado = asistenciaEstudiante.get(fecha);
                if (estado == null) {
                    fila.add("-"); // No estaba inscrito o no se registró
                } else {
                    // Mapeo visual simple para la celda
                    String sigla = switch (estado) {
                        case 1 -> "P"; // Presente
                        case 2 -> "F"; // Falta (Ausente)
                        case 3 -> "T"; // Tarde
                        case 4 -> "J"; // Justificado
                        default -> "?";
                    };
                    fila.add(sigla);

                    // Lógica de porcentaje: P, T y J cuentan como "No faltó"
                    if (estado != 2) { 
                        contadorAsistencias++;
                    }
                }
            }

            // Calcular Porcentaje
            int porcentaje = (totalClases > 0) ? (contadorAsistencias * 100 / totalClases) : 0;
            fila.add(porcentaje + "%");
            
            data.add(fila);
        }

        // 5. Asignar Modelo a la Tabla
        model = new DefaultTableModel(data, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Reporte de solo lectura
            }
        };
        table.setModel(model);
        
        // 6. Configurar Renderizado de Colores (Celda por Celda)
        aplicarRenderizadoColores();
    }

    private void aplicarRenderizadoColores() {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);

                String val = (value != null) ? value.toString() : "";

                // Colores según contenido
                if (val.equals("P")) { // Presente
                    c.setBackground(new Color(200, 255, 200)); // Verde claro
                    c.setForeground(new Color(0, 100, 0));
                } else if (val.equals("F")) { // Falta
                    c.setBackground(new Color(255, 200, 200)); // Rojo claro
                    c.setForeground(Color.RED);
                } else if (val.equals("T")) { // Tarde
                    c.setBackground(new Color(255, 228, 181)); // Naranja claro
                    c.setForeground(new Color(200, 100, 0));
                } else if (val.equals("J")) { // Justificado
                    c.setBackground(new Color(200, 220, 255)); // Azul claro
                    c.setForeground(Color.BLUE);
                } else if (val.contains("%")) { // Columna Porcentaje
                    c.setBackground(new Color(240, 240, 240));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                    try {
                        int num = Integer.parseInt(val.replace("%", ""));
                        if (num < 70) c.setForeground(Color.RED); // Alerta de baja asistencia
                        else c.setForeground(Color.BLACK);
                    } catch (Exception e) { c.setForeground(Color.BLACK); }
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                }

                return c;
            }
        });
        
        // Ajustar ancho columna nombre
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(200);
        }
    }
    
    private void estilizarTabla() {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Importante para muchas columnas de fechas
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(50, 50, 50));
        header.setForeground(Color.WHITE);
    }

    private void estilizarBoton(JButton btn) {
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }
}