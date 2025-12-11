// ventana/AgregarParaleloFrame.java
package ventana;

import dao.ParaleloDAO;
import javax.swing.*;
import java.awt.*;

public class AgregarParaleloFrame extends JFrame {

    private JComboBox<String> comboMateria, comboDocente;
    private JTextField txtNombre, txtSemestre, txtAño, txtCupo;

    public AgregarParaleloFrame() {
        setTitle("Registrar Nuevo Paralelo");
        setSize(650, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titulo = new JLabel("Registro de Paralelo", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(new Color(30, 30, 30));
        panel.add(titulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(250, 250, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ParaleloDAO dao = new ParaleloDAO();

        int y = 0;
        comboMateria = new JComboBox<>(dao.listarMaterias());
        agregarCampoCombo(form, "Materia:", comboMateria, y++);

        comboDocente = new JComboBox<>(dao.listarDocentes());
        agregarCampoCombo(form, "Docente:", comboDocente, y++);

        txtNombre = agregarCampo(form, "Nombre del paralelo (ej. A, B, 001):", y++);
        txtSemestre = agregarCampo(form, "Semestre (1 o 2):", y++);
        txtAño = agregarCampo(form, "Año:", y++);
        txtAño.setText(String.valueOf(java.time.Year.now().getValue()));

        txtCupo = agregarCampo(form, "Cupo máximo:", y++);

        panel.add(form, BorderLayout.CENTER);

        // BOTONES
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        botones.setBackground(new Color(250, 250, 250));

        

        JButton btnGuardarYHorario = new JButton("Crear y Asignar Horarios");
        btnGuardarYHorario.setBackground(new Color(52, 152, 219));
        btnGuardarYHorario.setForeground(Color.WHITE);
        btnGuardarYHorario.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGuardarYHorario.setPreferredSize(new Dimension(260, 50));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(150, 50));
        btnCancelar.addActionListener(e -> dispose());

        
        botones.add(btnGuardarYHorario);
        botones.add(btnCancelar);
        panel.add(botones, BorderLayout.SOUTH);

        // ACCIONES
        
        btnGuardarYHorario.addActionListener(e -> guardarParalelo(dao, true));

        add(panel);
        setVisible(true);
    }

    private void guardarParalelo(ParaleloDAO dao, boolean abrirHorarios) {
        try {
            int idMateria = ParaleloDAO.extraerId((String) comboMateria.getSelectedItem());
            int idDocente = ParaleloDAO.extraerId((String) comboDocente.getSelectedItem());
            String nombre = txtNombre.getText().trim().toUpperCase();
            String semestre = txtSemestre.getText().trim();
            int anio = Integer.parseInt(txtAño.getText().trim());
            int cupo = Integer.parseInt(txtCupo.getText().trim());

            if (idMateria <= 0 || idDocente <= 0 || nombre.isEmpty() || semestre.isEmpty() || cupo < 1) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos correctamente", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Registrar paralelo y obtener su ID
            int idParaleloCreado = dao.registrarParaleloYDevolverId(idMateria, idDocente, nombre, semestre, anio, cupo);

            if (idParaleloCreado > 0) {
                JOptionPane.showMessageDialog(this,
                    "Paralelo creado: " + nombre + " - " + semestre + "S " + anio,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

                if (abrirHorarios) {
                    // Abrir ventana de horarios con el paralelo ya seleccionado
                    new AgregarHorarioFrame(idParaleloCreado, nombre + " (" + semestre + "S-" + anio + ")");
                    dispose();
                } else {
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear el paralelo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Año y cupo deben ser números", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Métodos auxiliares (iguales)
    private JTextField agregarCampo(JPanel panel, String label, int row) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        campo.setPreferredSize(new Dimension(300, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(l, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(campo, gbc);
        return campo;
    }

    private void agregarCampoCombo(JPanel panel, String labelText, JComboBox<?> combo, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(label, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(combo, gbc);
    }
}