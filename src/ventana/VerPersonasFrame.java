package ventana;

import dao.EstudianteDAO;
import dao.DocenteDAO;
import dao.EmpleadoDAO;

import clases.Estudiante;
import clases.Docente;
import clases.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VerPersonasFrame extends JFrame {

    private JComboBox<String> comboTipo;
    private JTextField txtBusqueda;
    private JTable tabla;
    private JButton btnEditar, btnDeshabilitar, btnHabilitar, btnInactivos;

    private boolean mostrandoInactivos = false;

    private EstudianteDAO estudianteDAO = new EstudianteDAO();
    private DocenteDAO docenteDAO = new DocenteDAO();
    private EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    public VerPersonasFrame() {
        setTitle("Personas");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // PANEL SUPERIOR
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));

        comboTipo = new JComboBox<>(new String[]{"Estudiantes", "Docentes", "Empleados"});
        txtBusqueda = new JTextField(20);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> cargarTabla());

        panelSuperior.add(new JLabel("Tipo: "));
        panelSuperior.add(comboTipo);
        panelSuperior.add(new JLabel("Buscar: "));
        panelSuperior.add(txtBusqueda);
        panelSuperior.add(btnBuscar);

        add(panelSuperior, BorderLayout.NORTH);

        // TABLA
        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        // PANEL BOTONES INFERIOR
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));

        btnEditar = new JButton("Editar");
        btnDeshabilitar = new JButton("Deshabilitar");
        btnHabilitar = new JButton("Habilitar");
        btnInactivos = new JButton("Ver Inactivos");

        panelInferior.add(btnEditar);
        panelInferior.add(btnDeshabilitar);
        panelInferior.add(btnHabilitar);
        panelInferior.add(btnInactivos);

        add(panelInferior, BorderLayout.SOUTH);

        // ACCIONES
        btnEditar.addActionListener(e -> editar());
        btnDeshabilitar.addActionListener(e -> cambiarEstado(false));
        btnHabilitar.addActionListener(e -> cambiarEstado(true));

        btnInactivos.addActionListener(e -> {
            mostrandoInactivos = !mostrandoInactivos;
            btnInactivos.setText(mostrandoInactivos ? "Ver Activos" : "Ver Inactivos");
            cargarTabla();
        });

        // Cargar inicio
        cargarTabla();
    }

    private void cargarTabla() {
        String tipo = comboTipo.getSelectedItem().toString();
        String busqueda = txtBusqueda.getText().trim();

        DefaultTableModel model = new DefaultTableModel();
        model.setRowCount(0);

        model.addColumn("ID Persona");
        model.addColumn("Nombres");
        model.addColumn("Apellidos");
        model.addColumn("Carnet");
        model.addColumn("Correo");
        model.addColumn("Teléfono");

        if (tipo.equals("Estudiantes")) {
            model.addColumn("Carrera");
            model.addColumn("Matrícula");

            List<Estudiante> lista = estudianteDAO.listarEstudiantes(busqueda, mostrandoInactivos);
            for (Estudiante est : lista) {
                model.addRow(new Object[]{
                        est.getIdPersona(),
                        est.getNombres(),
                        est.getApellidos(),
                        est.getCarnet(),
                        est.getCorreo(),
                        est.getTelefono(),
                        est.getCarrera(),
                        est.getMatricula()
                });
            }

        } else if (tipo.equals("Docentes")) {

            List<Docente> lista = docenteDAO.listarDocentes(busqueda, mostrandoInactivos);
            for (Docente d : lista) {
                model.addRow(new Object[]{
                        d.getIdPersona(),
                        d.getNombre(),
                        d.getApellido(),
                        d.getCarnet(),
                        d.getCorreo(),
                        d.getTelefono(),
                });
            }

        } else if (tipo.equals("Empleados")) {

            model.addColumn("Cargo");

            List<Empleado> lista = empleadoDAO.listarEmpleados(busqueda, mostrandoInactivos);
            for (Empleado emp : lista) {
                model.addRow(new Object[]{
                        emp.getIdPersona(),
                        emp.getNombres(),
                        emp.getApellidos(),
                        emp.getCarnet(),
                        emp.getCorreo(),
                        emp.getTelefono(),
                        emp.getCargo()
                });
            }
        }

        tabla.setModel(model);

        // Habilitar/Deshabilitar botones según vista (por ejemplo, si estoy viendo inactivos, habilitar "Habilitar")
        btnHabilitar.setEnabled(mostrandoInactivos);
        btnDeshabilitar.setEnabled(!mostrandoInactivos);
    }

    private int obtenerIdPersonaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return Integer.parseInt(tabla.getValueAt(fila, 0).toString());
    }

    private void nuevo() {
        JOptionPane.showMessageDialog(this, "Abrir formulario Nuevo.");
    }

    private void editar() {
        int idPersona = obtenerIdPersonaSeleccionada();
        if (idPersona == -1) return;

        String tipo = comboTipo.getSelectedItem().toString();
        String tipoReal = switch (tipo) {
            case "Estudiantes" -> "Estudiante";
            case "Docentes" -> "Docente";
            default -> "Administrador (Empleado)";
        };

        EditarPersonaFrame frame = new EditarPersonaFrame(idPersona, tipoReal);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                cargarTabla(); // 🔄 actualizar tabla al cerrar ventana de edición
            }
        });
    }

    private void cambiarEstado(boolean activo) {
        int idPersona = obtenerIdPersonaSeleccionada();
        if (idPersona == -1) return;

        int opcion = JOptionPane.showConfirmDialog(
                this,
                activo ? "¿Habilitar usuario?" : "¿Deshabilitar usuario?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion != JOptionPane.YES_OPTION) return;

        String tipo = comboTipo.getSelectedItem().toString();
        boolean exito = false;

        // Delegar a DAO correspondiente (estos métodos ya los tienes: cambiarEstadoEstudiante/... )
        if (tipo.equals("Estudiantes")) {
            exito = estudianteDAO.cambiarEstadoEstudiante(idPersona, activo);
        } else if (tipo.equals("Docentes")) {
            exito = docenteDAO.cambiarEstadoDocente(idPersona, activo);
        } else if (tipo.equals("Empleados")) {
            exito = empleadoDAO.cambiarEstadoEmpleado(idPersona, activo);
        }

        if (exito) {
            JOptionPane.showMessageDialog(this, "Estado actualizado correctamente.");
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar estado.");
        }
    }
}
