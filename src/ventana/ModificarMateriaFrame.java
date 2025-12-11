package ventana;

import dao.MateriaDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModificarMateriaFrame extends JDialog {
    private JComboBox<String> comboCarreas;
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtCreditos;
    private JTextField txtSemestre;
    private JTextField txtPrerequisitos;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    private MateriaDAO materiaDAO;
    private int idMateria;
    private VerMateriaParaleloFrame parentFrame;
    
    public ModificarMateriaFrame(VerMateriaParaleloFrame parent, int idMateria) {
        super(parent, "Modificar Materia", true);
        this.idMateria = idMateria;
        this.materiaDAO = new MateriaDAO();
        this.parentFrame = parent;
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        initComponents();
        cargarDatosMateria();
    }
    
    private void initComponents() {
        // Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Carrera
        gbc.gridx = 0; gbc.gridy = 0;
        panelPrincipal.add(new JLabel("Carrera:"), gbc);
        
        comboCarreas = new JComboBox<>(materiaDAO.listarCarrerasParaCombo());
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        panelPrincipal.add(comboCarreas, gbc);
        
        // Código
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Código:"), gbc);
        
        txtCodigo = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        panelPrincipal.add(txtCodigo, gbc);
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 2;
        panelPrincipal.add(new JLabel("Nombre:"), gbc);
        
        txtNombre = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        panelPrincipal.add(txtNombre, gbc);
        
        // Créditos
        gbc.gridx = 0; gbc.gridy = 3;
        panelPrincipal.add(new JLabel("Créditos:"), gbc);
        
        txtCreditos = new JTextField();
        gbc.gridx = 1; gbc.gridy = 3;
        panelPrincipal.add(txtCreditos, gbc);
        
        // Semestre
        gbc.gridx = 0; gbc.gridy = 4;
        panelPrincipal.add(new JLabel("Semestre:"), gbc);
        
        txtSemestre = new JTextField();
        gbc.gridx = 1; gbc.gridy = 4;
        panelPrincipal.add(txtSemestre, gbc);
        
        // Prerrequisitos
        gbc.gridx = 0; gbc.gridy = 5;
        panelPrincipal.add(new JLabel("Prerrequisitos:"), gbc);
        
        txtPrerequisitos = new JTextField();
        gbc.gridx = 1; gbc.gridy = 5;
        panelPrincipal.add(txtPrerequisitos, gbc);
        
        add(panelPrincipal, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarCambios();
            }
        });
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarDatosMateria() {
        String[] datos = materiaDAO.obtenerDatosMateria(idMateria);
        if (datos != null) {
            // Seleccionar la carrera en el combo
            String carreraBuscada = datos[0] + " | " + datos[6];
            for (int i = 0; i < comboCarreas.getItemCount(); i++) {
                if (comboCarreas.getItemAt(i).equals(carreraBuscada)) {
                    comboCarreas.setSelectedIndex(i);
                    break;
                }
            }
            
            txtCodigo.setText(datos[1]);
            txtNombre.setText(datos[2]);
            txtCreditos.setText(datos[3]);
            txtSemestre.setText(datos[4]);
            txtPrerequisitos.setText(datos[5]);
        }
    }
    
    private void guardarCambios() {
        // Validaciones
        if (txtCodigo.getText().trim().isEmpty() || 
            txtNombre.getText().trim().isEmpty() ||
            txtCreditos.getText().trim().isEmpty() ||
            txtSemestre.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Todos los campos excepto prerrequisitos son obligatorios",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int idCarrera = MateriaDAO.extraerIdDeCombo((String) comboCarreas.getSelectedItem());
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            int creditos = Integer.parseInt(txtCreditos.getText().trim());
            int semestre = Integer.parseInt(txtSemestre.getText().trim());
            String prerequisitos = txtPrerequisitos.getText().trim();
            
            boolean exito = materiaDAO.modificarMateria(idMateria, idCarrera, codigo, nombre, 
                                                      creditos, semestre, prerequisitos);
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Materia modificada correctamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                parentFrame.refrescarTabla();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al modificar la materia",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Créditos y semestre deben ser números enteros",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}