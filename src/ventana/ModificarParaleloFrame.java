package ventana;

import dao.ParaleloDAO;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModificarParaleloFrame extends JDialog {
    private JComboBox<String> comboMaterias;
    private JComboBox<String> comboDocentes;
    private JTextField txtNombreParalelo;
    private JTextField txtSemestre;  // Cambiado de JComboBox a JTextField
    private JTextField txtAnio;
    private JTextField txtCupo;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    private ParaleloDAO paraleloDAO;
    private int idParalelo;
    private VerMateriaParaleloFrame parentFrame;
    
    public ModificarParaleloFrame(VerMateriaParaleloFrame parent, int idParalelo) {
        super(parent, "Modificar Paralelo", true);
        this.parentFrame = parent;
        this.idParalelo = idParalelo;
        this.paraleloDAO = new ParaleloDAO();
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        initComponents();
        cargarDatosParalelo();
    }
    
    private void initComponents() {
        // Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Materia
        gbc.gridx = 0; gbc.gridy = 0;
        panelPrincipal.add(new JLabel("Materia:"), gbc);
        
        comboMaterias = new JComboBox<>(paraleloDAO.listarMaterias());
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        panelPrincipal.add(comboMaterias, gbc);
        
        // Docente
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Docente:"), gbc);
        
        comboDocentes = new JComboBox<>(paraleloDAO.listarDocentes());
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        panelPrincipal.add(comboDocentes, gbc);
        
        // Nombre Paralelo
        gbc.gridx = 0; gbc.gridy = 2;
        panelPrincipal.add(new JLabel("Nombre Paralelo:"), gbc);
        
        txtNombreParalelo = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        panelPrincipal.add(txtNombreParalelo, gbc);
        
        // Semestre (ahora es campo de texto numérico)
        gbc.gridx = 0; gbc.gridy = 3;
        panelPrincipal.add(new JLabel("Semestre:"), gbc);
        
        txtSemestre = new JTextField();
        // Restringir a solo números
        txtSemestre.setDocument(new NumberOnlyDocument());
        txtSemestre.setToolTipText("Ingrese solo números (ej: 1, 2, 3, etc.)");
        gbc.gridx = 1; gbc.gridy = 3;
        panelPrincipal.add(txtSemestre, gbc);
        
        // Año
        gbc.gridx = 0; gbc.gridy = 4;
        panelPrincipal.add(new JLabel("Año:"), gbc);
        
        txtAnio = new JTextField();
        // Restringir a solo números
        txtAnio.setDocument(new NumberOnlyDocument());
        txtAnio.setToolTipText("Ingrese solo números (ej: 2024)");
        gbc.gridx = 1; gbc.gridy = 4;
        panelPrincipal.add(txtAnio, gbc);
        
        // Cupo
        gbc.gridx = 0; gbc.gridy = 5;
        panelPrincipal.add(new JLabel("Cupo:"), gbc);
        
        txtCupo = new JTextField();
        // Restringir a solo números
        txtCupo.setDocument(new NumberOnlyDocument());
        txtCupo.setToolTipText("Ingrese solo números (ej: 30)");
        gbc.gridx = 1; gbc.gridy = 5;
        panelPrincipal.add(txtCupo, gbc);
        
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
    
    // Clase interna para restringir a solo números
    private class NumberOnlyDocument extends PlainDocument {
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) return;
            
            // Solo permite dígitos
            for (int i = 0; i < str.length(); i++) {
                if (!Character.isDigit(str.charAt(i))) {
                    return; // No inserta si encuentra un carácter no numérico
                }
            }
            
            super.insertString(offs, str, a);
        }
    }
    
    private void cargarDatosParalelo() {
        String[] datos = paraleloDAO.obtenerDatosParalelo(idParalelo);
        if (datos != null) {
            // Seleccionar materia
            String materiaBuscada = datos[0] + " | " + datos[6] + " - " + datos[7];
            for (int i = 0; i < comboMaterias.getItemCount(); i++) {
                if (comboMaterias.getItemAt(i).contains(materiaBuscada)) {
                    comboMaterias.setSelectedIndex(i);
                    break;
                }
            }
            
            // Seleccionar docente
            String docenteBuscado = datos[1] + " | " + datos[8];
            for (int i = 0; i < comboDocentes.getItemCount(); i++) {
                if (comboDocentes.getItemAt(i).contains(datos[8])) {
                    comboDocentes.setSelectedIndex(i);
                    break;
                }
            }
            
            txtNombreParalelo.setText(datos[2]);
            txtSemestre.setText(datos[3]); // Ahora es texto plano
            txtAnio.setText(datos[4]);
            txtCupo.setText(datos[5]);
        }
    }
    
    private void guardarCambios() {
        // Validaciones
        if (txtNombreParalelo.getText().trim().isEmpty() ||
            txtSemestre.getText().trim().isEmpty() ||
            txtAnio.getText().trim().isEmpty() ||
            txtCupo.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar que los campos numéricos no sean cero
        try {
            int semestre = Integer.parseInt(txtSemestre.getText().trim());
            int anio = Integer.parseInt(txtAnio.getText().trim());
            int cupo = Integer.parseInt(txtCupo.getText().trim());
            
            if (semestre <= 0) {
                JOptionPane.showMessageDialog(this, "El semestre debe ser mayor a 0",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (anio < 2000 || anio > 2100) {
                JOptionPane.showMessageDialog(this, "El año debe estar entre 2000 y 2100",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (cupo <= 0) {
                JOptionPane.showMessageDialog(this, "El cupo debe ser mayor a 0",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int idMateria = ParaleloDAO.extraerId((String) comboMaterias.getSelectedItem());
            int idDocente = ParaleloDAO.extraerId((String) comboDocentes.getSelectedItem());
            String nombreParalelo = txtNombreParalelo.getText().trim();
            
            boolean exito = paraleloDAO.modificarParalelo(idParalelo, idMateria, idDocente, 
                                                         nombreParalelo, 
                                                         String.valueOf(semestre), // Convertir a String
                                                         anio, cupo);
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Paralelo modificado correctamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                // Actualizar tabla principal
                if (parentFrame != null) {
                    parentFrame.refrescarTabla();
                }
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al modificar el paralelo",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Semestre, año y cupo deben ser números enteros válidos",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}