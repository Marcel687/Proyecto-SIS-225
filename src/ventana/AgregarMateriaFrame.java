// paquete: ventana
package ventana;

import clases.Carrera;
import dao.CarreraDAO;
import dao.MateriaDAO;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AgregarMateriaFrame extends JFrame {

    private JTextField txtCodigo, txtNombre, txtCreditos, txtSemestre, txtPrerrequisitos;
    private JComboBox<Carrera> comboCarrera;

    public AgregarMateriaFrame() {
        setTitle("Registrar Materia");
        setSize(650, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Registro de Materia", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(new Color(30, 30, 30));
        panel.add(titulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(250, 250, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        // Combo de carrera
        comboCarrera = new JComboBox<>();
        cargarCarreras();
        agregarCampoCombo(form, "Carrera:", comboCarrera, y++);

        txtCodigo = agregarCampo(form, "Código (ej. MAT101):", y++);
        txtNombre = agregarCampo(form, "Nombre de la materia:", y++);
        txtCreditos = agregarCampo(form, "Créditos:", y++);
        txtSemestre = agregarCampo(form, "Semestre:", y++);

        txtPrerrequisitos = agregarCampo(form, "Prerrequisitos (códigos separados por coma):", y++);
        
        JButton btnPrerrequisitos = new JButton("Gestionar Prerrequisitos →");
        btnPrerrequisitos.setBackground(new Color(40, 167, 69));
        btnPrerrequisitos.setForeground(Color.WHITE);
        btnPrerrequisitos.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnPrerrequisitos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrerrequisitos.addActionListener(e -> new PrerrequisitosFrame());

        // Lo agregamos en la siguiente fila
        GridBagConstraints gbcBoton = new GridBagConstraints();
        gbcBoton.gridx = 1;
        gbcBoton.gridy = y++;
        gbcBoton.insets = new Insets(15, 10, 10, 10);
        gbcBoton.anchor = GridBagConstraints.EAST;
        form.add(btnPrerrequisitos, gbcBoton);
        
        txtPrerrequisitos.setToolTipText("Ejemplo: MAT101, FIS102, ING001 (opcional)");

        panel.add(form, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        botones.setBackground(new Color(250, 250, 250));

        JButton btnGuardar = new JButton("Guardar Materia");
        btnGuardar.setBackground(new Color(60, 105, 240));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGuardar.setPreferredSize(new Dimension(180, 46));
        btnGuardar.addActionListener(e -> guardar());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(150, 46));
        btnCancelar.addActionListener(e -> dispose());

        botones.add(btnGuardar);
        botones.add(btnCancelar);
        panel.add(botones, BorderLayout.SOUTH);

        add(new JScrollPane(panel));
        setVisible(true);
    }

    private void cargarCarreras() {
        CarreraDAO dao = new CarreraDAO();
        List<Carrera> lista = dao.listarCarreras();
        comboCarrera.removeAllItems();
        comboCarrera.addItem(new Carrera(0, "-- Seleccione una carrera --"));
        for (Carrera c : lista) {
            comboCarrera.addItem(c);
        }
    }

    private void guardar() {
        Carrera carrera = (Carrera) comboCarrera.getSelectedItem();
        if (carrera == null || carrera.getIdCarrera() == 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una carrera", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String creditosStr = txtCreditos.getText().trim();
        String semestreStr = txtSemestre.getText().trim();
        String prerreq = txtPrerrequisitos.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || creditosStr.isEmpty() || semestreStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int creditos, semestre;
        try {
            creditos = Integer.parseInt(creditosStr);
            semestre = Integer.parseInt(semestreStr);
            if (creditos < 1 || semestre < 1 || semestre > 20) throw new Exception();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Créditos y semestre deben ser números válidos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MateriaDAO dao = new MateriaDAO();
        int idMateria = dao.registrarMateria(
            carrera.getIdCarrera(),
            codigo,
            nombre,
            creditos,
            semestre,
            prerreq.isEmpty() ? null : prerreq
        );

        if (idMateria > 0) {
            JOptionPane.showMessageDialog(this,
                "Materia registrada exitosamente\n" + codigo + " - " + nombre,
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al registrar la materia.\nPosiblemente el código ya existe.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Métodos auxiliares (iguales que antes)
    private JTextField agregarCampo(JPanel panel, String label, int row) {

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        l.setPreferredSize(new Dimension(180, 25)); // ← ancho fijo del label

        JTextField campo = new JTextField(25);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        panel.add(l, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(campo, gbc);

        return campo;
    }

    private void agregarCampoCombo(JPanel panel, String labelText, JComboBox<?> combo, int row) {

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setPreferredSize(new Dimension(180, 25)); // ← ancho fijo del label

        combo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(combo, gbc);
    }

}