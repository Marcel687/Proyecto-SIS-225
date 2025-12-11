package ventana;

import clases.EstudianteInscrito;
import clases.EvaluacionDTO;
import dao.GestionAcademicaDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class PanelNotas extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private GestionAcademicaDAO dao;
    private int idParalelo;
    private List<EvaluacionDTO> evaluaciones;
    private List<EstudianteInscrito> estudiantes;

    public PanelNotas(int idParalelo) {
        this.idParalelo = idParalelo;
        this.dao = new GestionAcademicaDAO();
        
        // Configuración visual del panel principal
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245)); // Fondo gris suave
        setBorder(new EmptyBorder(15, 15, 15, 15)); // Margen externo

        // 1. Cargar Datos Estructurales (Evaluaciones y Estudiantes)
        evaluaciones = dao.obtenerEvaluaciones(idParalelo);
        estudiantes = dao.obtenerEstudiantesPorParalelo(idParalelo);

        // 2. Crear Tabla con Columnas Dinámicas
        crearTabla();

        // 3. Crear Panel de Botones Inferior
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(new Color(245, 245, 245));

        JButton btnRefrescar = new JButton("Recargar Tabla");
        estilizarBoton(btnRefrescar, new Color(70, 130, 180)); // Azul Acero
        btnRefrescar.addActionListener(e -> cargarDatosEnTabla());

        JButton btnGuardar = new JButton("Guardar Notas");
        estilizarBoton(btnGuardar, new Color(34, 139, 34)); // Verde Bosque
        btnGuardar.addActionListener(e -> guardarNotas());

        panelBotones.add(btnRefrescar);
        panelBotones.add(btnGuardar);

        // Añadir componentes al Panel
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void crearTabla() {
        // Estructura de columnas: 
        // 0: ID (Oculto) | 1: Estudiante | 2..N: Evaluaciones | N+1: FINAL (Kardex)
        String[] columnas = new String[3 + evaluaciones.size()];
        columnas[0] = "ID";
        columnas[1] = "Estudiante";
        
        // Agregar columnas dinámicas según evaluaciones
        for (int i = 0; i < evaluaciones.size(); i++) {
            columnas[i + 2] = evaluaciones.get(i).getNombre();
        }
        columnas[columnas.length - 1] = "FINAL ACUMULADO";

        // Configuración del Modelo de la Tabla
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo son editables las columnas de evaluaciones (no ID, no Nombre, no Final)
                return column >= 2 && column < (getColumnCount() - 1);
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Las evaluaciones ahora son ENTEROS
                if(columnIndex >= 2 && columnIndex < (getColumnCount() - 1)) return Integer.class;
                // La nota final calculada puede ser decimal (ej: 50.5)
                if(columnIndex == getColumnCount() - 1) return Double.class;
                return String.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30); // Filas más altas para mejor lectura
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Ocultar columna ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        // Ancho preferido para el nombre
        table.getColumnModel().getColumn(1).setPreferredWidth(250);

        // Estilo del Encabezado
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(50, 50, 50)); // Gris Oscuro
        header.setForeground(Color.WHITE);
        header.setOpaque(true);

        // --- RENDERER PERSONALIZADO (Colores y Formato) ---
        DefaultTableCellRenderer notaRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER); // Centrar números
                
                if (value != null && value instanceof Number) {
                    double nota = ((Number) value).doubleValue();
                    
                    // Lógica de colores: Rojo si reprueba (<51), Verde si aprueba
                    if (nota < 51) {
                        c.setForeground(Color.RED);
                        c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else {
                        c.setForeground(new Color(0, 100, 0)); // Verde oscuro
                        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    }
                } else {
                    c.setForeground(Color.BLACK);
                }
                
                // Fondo especial para la columna FINAL ACUMULADO
                if (column == table.getColumnCount() - 1) {
                    c.setBackground(new Color(230, 240, 255)); // Azul muy claro
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Negrita siempre
                } else {
                    // Mantener color de selección o blanco
                    c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                }
                
                return c;
            }
        };

        // Aplicar renderer a todas las columnas numéricas (Evaluaciones + Final)
        for (int i = 2; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(notaRenderer);
        }

        cargarDatosEnTabla();
    }

    private void cargarDatosEnTabla() {
        model.setRowCount(0); // Limpiar tabla
        for (EstudianteInscrito est : estudiantes) {
            Object[] row = new Object[model.getColumnCount()];
            row[0] = est.getIdInscripcion();
            row[1] = est.getNombreCompleto();

            // Cargar notas de evaluaciones (Ahora lee ENTEROS)
            for (int i = 0; i < evaluaciones.size(); i++) {
                int nota = dao.obtenerNota(est.getIdInscripcion(), evaluaciones.get(i).getIdEvaluacion());
                row[i + 2] = nota;
            }
            
            // Cargar Nota Final Calculada desde Kardex (Lee DOUBLE porque es promedio ponderado)
            double notaKardex = dao.obtenerNotaKardex(est.getIdInscripcion());
            row[model.getColumnCount() - 1] = notaKardex;
            
            model.addRow(row);
        }
    }

    private void guardarNotas() {
        // Detener edición si el usuario dejó una celda "abierta"
        if (table.isEditing()) table.getCellEditor().stopCellEditing();

        try {
            int totalNotas = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                // Obtener ID oculto
                Object idObj = model.getValueAt(i, 0);
                if(idObj == null) continue;
                int idInscripcion = Integer.parseInt(idObj.toString());

                // Recorrer SOLO columnas de evaluaciones editables
                for (int j = 0; j < evaluaciones.size(); j++) {
                    Object valorObj = model.getValueAt(i, j + 2);
                    
                    // Tratamiento de nulos o vacíos como 0
                    if (valorObj == null || valorObj.toString().trim().isEmpty()) {
                        valorObj = "0";
                    }

                    // --- VALIDACIÓN ESTRICTA DE ENTEROS ---
                    int nota = Integer.parseInt(valorObj.toString());

                    // Validación de Rango
                    if (nota < 0 || nota > 100) {
                        JOptionPane.showMessageDialog(this, 
                            "Error en fila " + (i+1) + ": La nota debe estar entre 0 y 100.",
                            "Nota fuera de rango", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int idEvaluacion = evaluaciones.get(j).getIdEvaluacion();
                    
                    // Guardar (Esto dispara el trigger/lógica en DAO para actualizar Kardex)
                    dao.guardarNota(idInscripcion, idEvaluacion, nota);
                    totalNotas++;
                }
            }
            
            // Recargar tabla para ver los nuevos promedios del Kardex actualizados
            cargarDatosEnTabla();
            JOptionPane.showMessageDialog(this, "Se registraron correctamente las notas y se actualizó el Kardex.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException ex) {
            // Este error salta si intentan meter decimales (ej: 80.5) o letras
            JOptionPane.showMessageDialog(this, 
                "Error de Formato: Solo se permiten números ENTEROS (Ej: 80, 51).\nNo utilice puntos decimales ni letras.", 
                "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void estilizarBoton(JButton btn, Color colorFondo) {
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}