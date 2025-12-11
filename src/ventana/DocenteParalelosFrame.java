package ventana;

import clases.Docente;
import dao.DocenteDAO;
import dao.ParaleloDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocenteParalelosFrame extends JFrame {

    private JPanel contentPane;
    private JTable tablaParalelos;
    private JTextField textBuscador;
    private JComboBox<String> comboGestion;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    public DocenteParalelosFrame(int idUsuario) {
        // Recuperar datos del docente
        DocenteDAO docenteDAO = new DocenteDAO();
        Docente docente = docenteDAO.recupera(idUsuario); //

        setTitle("Panel Docente - " + docente.getNombre() + " " + docente.getApellido());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 245, 245));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPane);

        // --- 1. ENCABEZADO SUPERIOR ---
        JPanel panEncabezado = new JPanel(new BorderLayout());
        panEncabezado.setBackground(new Color(44, 62, 80)); // Azul oscuro
        panEncabezado.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Mis Cursos Asignados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        panEncabezado.add(lblTitulo, BorderLayout.WEST);

        JLabel lblDocenteInfo = new JLabel("Docente: " + docente.getNombre() + " " + docente.getApellido());
        lblDocenteInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDocenteInfo.setForeground(new Color(200, 200, 200));
        panEncabezado.add(lblDocenteInfo, BorderLayout.EAST);

        contentPane.add(panEncabezado, BorderLayout.NORTH);

        // --- 2. PANEL CENTRAL (Filtros y Tabla) ---
        JPanel panCentral = new JPanel(new BorderLayout(0, 10));
        panCentral.setOpaque(false);

        // A. Barra de Filtros
        JPanel panFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panFiltros.setBackground(Color.WHITE);
        panFiltros.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));

        // Filtro Semestre
        panFiltros.add(crearLabel("Filtrar por Gestión:"));
        comboGestion = new JComboBox<>();
        comboGestion.addItem("Todos");
        comboGestion.setPreferredSize(new Dimension(120, 30));
        comboGestion.addActionListener(e -> aplicarFiltros());
        panFiltros.add(comboGestion);

        // Filtro Buscador
        panFiltros.add(Box.createHorizontalStrut(20)); // Espacio
        panFiltros.add(crearLabel("Buscar Materia:"));
        textBuscador = new JTextField(20);
        textBuscador.setPreferredSize(new Dimension(200, 30));
        textBuscador.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                aplicarFiltros();
            }
        });
        panFiltros.add(textBuscador);

        panCentral.add(panFiltros, BorderLayout.NORTH);

        // B. Tabla de Resultados
        tablaParalelos = new JTable();
        estilizarTabla(tablaParalelos);
        
        // Cargar datos y configurar sorter
        cargarDatos(docente.getIdDocente());
        
        JScrollPane scrollPane = new JScrollPane(tablaParalelos);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panCentral.add(scrollPane, BorderLayout.CENTER);

        contentPane.add(panCentral, BorderLayout.CENTER);

        // --- 3. PANEL INFERIOR (Botones) ---
        JPanel panBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panBotones.setOpaque(false);

        JButton btnSalir = new JButton("Cerrar Sesión");
        estilizarBoton(btnSalir, new Color(192, 57, 43)); // Rojo
        btnSalir.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JButton btnIngresar = new JButton("Gestionar Curso Seleccionado");
        estilizarBoton(btnIngresar, new Color(39, 174, 96)); // Verde
        btnIngresar.setPreferredSize(new Dimension(250, 40));
        
        // Acción del botón Ingresar
        btnIngresar.addActionListener(e -> abrirGestionCurso());

        panBotones.add(btnSalir);
        panBotones.add(btnIngresar);
        contentPane.add(panBotones, BorderLayout.SOUTH);
    }

    // --- MÉTODOS LÓGICOS ---

    private void cargarDatos(int idDocente) {
        ParaleloDAO paraleloDAO = new ParaleloDAO();
        // Obtenemos el modelo desde el DAO existente
        modeloTabla = paraleloDAO.obtenerModeloTablaParalelos(idDocente);
        tablaParalelos.setModel(modeloTabla);
        
        // Configurar el clasificador/filtrador
        sorter = new TableRowSorter<>(modeloTabla);
        tablaParalelos.setRowSorter(sorter);

        // Ocultar Columna ID (índice 0)
        tablaParalelos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaParalelos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaParalelos.getColumnModel().getColumn(0).setWidth(0);

        // Llenar el ComboBox de Gestión basado en los datos existentes
        llenarComboGestiones();
    }

    private void llenarComboGestiones() {
        Set<String> gestiones = new HashSet<>();
        // Recorremos la tabla para encontrar semestres únicos (Columna 3 según tu DAO)
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Object val = modeloTabla.getValueAt(i, 3); // Columna SEMESTRE
            if (val != null) {
                gestiones.add(val.toString());
            }
        }
        
        // Añadir al combo
        for (String g : gestiones) {
            comboGestion.addItem(g);
        }
    }

    private void aplicarFiltros() {
        String texto = textBuscador.getText();
        String gestion = (String) comboGestion.getSelectedItem();
        
        List<RowFilter<Object, Object>> filtros = new ArrayList<>();

        // 1. Filtro de Texto (Busca en Nombre Materia - Col 1)
        if (texto != null && !texto.trim().isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)" + texto, 1, 2)); // Busca en col 1 (Materia) y 2 (Paralelo)
        }

        // 2. Filtro de Gestión (Busca en Semestre - Col 3)
        if (gestion != null && !gestion.equals("Todos")) {
            filtros.add(RowFilter.regexFilter("^" + gestion + "$", 3));
        }

        // Combinar filtros
        sorter.setRowFilter(RowFilter.andFilter(filtros));
    }

    private void abrirGestionCurso() {
        int viewRow = tablaParalelos.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un curso de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convertir índice de la vista al modelo (por si hay filtros aplicados)
        int modelRow = tablaParalelos.convertRowIndexToModel(viewRow);

        int idParalelo = Integer.parseInt(modeloTabla.getValueAt(modelRow, 0).toString());
        String materia = modeloTabla.getValueAt(modelRow, 1).toString();
        String paralelo = modeloTabla.getValueAt(modelRow, 2).toString();
        
        // Abrir la ventana que creamos anteriormente
        new GestionCursoFrame(idParalelo, materia + " - " + paralelo).setVisible(true);
    }

    // --- MÉTODOS DE ESTILO ---

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(80, 80, 80));
        return lbl;
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(35);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabla.setSelectionBackground(new Color(230, 240, 250));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(230, 230, 230));

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(new Color(60, 60, 60));
        header.setOpaque(true);
        
        // Centrar datos en columnas específicas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        // Centrar Semestre (3) e Inscritos (4)
        if(tabla.getColumnCount() > 4) {
             tabla.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
             tabla.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        }
    }

    private void estilizarBoton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}

/**package ventana;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import clases.Docente;
import dao.DocenteDAO;
import dao.ParaleloDAO;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.Color;

public class DocenteParalelosFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tablaParalelos;
	private JTextField textBuscador;
	private DefaultTableModel modeloTabla;

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//DocenteParalelosFrame frame = new DocenteParalelosFrame(int idUsuario);
					//frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public DocenteParalelosFrame(int idUsuario) {
		
		DocenteDAO docenteDAO = new DocenteDAO();
		Docente docente = docenteDAO.recupera(idUsuario);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1170, 566);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panEncabezado = new JPanel();
		contentPane.add(panEncabezado, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("Paralelos");
		lblNewLabel.setFont(new Font("Arial Black", Font.BOLD, 25));
		panEncabezado.add(lblNewLabel);
		
		JPanel panBotones = new JPanel();
		contentPane.add(panBotones, BorderLayout.SOUTH);
		panBotones.setLayout(new BorderLayout(0, 0));
		
		// ... código anterior dentro del constructor de DocenteParalelosFrame ...

		JButton btnIngresar = new JButton("Ingresar");
		btnIngresar.setBackground(new Color(192, 192, 192));
		btnIngresar.setFont(new Font("Arial", Font.PLAIN, 15));
		btnIngresar.setHorizontalAlignment(SwingConstants.RIGHT);

		// --- INICIO DE MODIFICACIÓN ---
		btnIngresar.addActionListener(e -> {
		    int filaSeleccionada = tablaParalelos.getSelectedRow();
		    
		    if (filaSeleccionada == -1) {
		        javax.swing.JOptionPane.showMessageDialog(this, "Por favor seleccione un paralelo de la lista.");
		        return;
		    }
		    
		    // Obtener datos de la fila seleccionada (ID está en columna 0, Materia en columna 1)
		    int idParalelo = (int) tablaParalelos.getValueAt(filaSeleccionada, 0);
		    String nombreMateria = (String) tablaParalelos.getValueAt(filaSeleccionada, 1) + " - " + 
		                           (String) tablaParalelos.getValueAt(filaSeleccionada, 2); // Materia + Grupo
		    
		    // Abrir la nueva ventana
		    GestionCursoFrame gestionFrame = new GestionCursoFrame(idParalelo, nombreMateria);
		    gestionFrame.setVisible(true);
		});
		// --- FIN DE MODIFICACIÓN ---

		panBotones.add(btnIngresar);

		// ... resto del código ...
		
		JPanel panPrincipal = new JPanel();
		contentPane.add(panPrincipal, BorderLayout.CENTER);
		panPrincipal.setLayout(new BorderLayout(0, 0));
		
		tablaParalelos = new JTable();
		tablaParalelos.setBackground(new Color(192, 192, 192));
		panPrincipal.add(tablaParalelos);
		
		JPanel pan_buscador = new JPanel();
		panPrincipal.add(pan_buscador, BorderLayout.NORTH);
		
		JLabel lblNewLabel_1 = new JLabel("Nombre Materia:");
		pan_buscador.add(lblNewLabel_1);
		
		textBuscador = new JTextField();
		pan_buscador.add(textBuscador);
		textBuscador.setColumns(30);
		
		JLabel lblNewLabel_2 = new JLabel("Semestre");
		pan_buscador.add(lblNewLabel_2);
		
		JComboBox BoxSemestre = new JComboBox();
		BoxSemestre.setEditable(true);
		pan_buscador.add(BoxSemestre);

		cargarTabla(docente.getIdDocente());
	}

	private void cargarTabla(int idDocente) {
		ParaleloDAO paraleloDAO = new ParaleloDAO();
		modeloTabla = paraleloDAO.obtenerModeloTablaParalelos(idDocente);
	    tablaParalelos.setModel(modeloTabla);
		
	}
}**/