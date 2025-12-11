package ventana;

import dao.MateriaDAO;
import javax.swing.*;
import java.awt.*;

public class PrerrequisitosFrame extends JFrame {

    private JComboBox<String> comboMateria;
    private JComboBox<String> comboRequisito;
    private DefaultListModel<String> modeloLista;
    private JList<String> listaAsignados;

    public PrerrequisitosFrame() {
        setTitle("Gestión de Prerrequisitos");
        setSize(750, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        MateriaDAO dao = new MateriaDAO();
        String[] materias = dao.listarMateriasParaCombo();

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(250, 250, 250));

        JLabel titulo = new JLabel("Asignar Prerrequisitos entre Materias", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        panel.add(titulo, BorderLayout.NORTH);

        // Panel central
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(new Color(250, 250, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        // MATERIA
        comboMateria = new JComboBox<>(materias);
        estilizarCombo(comboMateria);
        agregarCampoCombo(centro, "Materia:", comboMateria, y++);

        // REQUISITO
        comboRequisito = new JComboBox<>(materias);
        estilizarCombo(comboRequisito);
        agregarCampoCombo(centro, "Materia Requisito:", comboRequisito, y++);

        // BOTÓN AGREGAR
        JButton btnAgregar = new JButton("Asignar Prerrequisito");
        btnAgregar.setBackground(new Color(40, 167, 69));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregar.addActionListener(e -> asignarPrerrequisito(dao));

        gbc.gridx = 1;
        gbc.gridy = y++;
        gbc.anchor = GridBagConstraints.EAST;
        centro.add(btnAgregar, gbc);

        // LISTA DE ASIGNADOS
        modeloLista = new DefaultListModel<>();
        listaAsignados = new JList<>(modeloLista);

        listaAsignados.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JScrollPane scroll = new JScrollPane(listaAsignados);
        scroll.setPreferredSize(new Dimension(520, 180));

        JLabel lblLista = new JLabel("Prerrequisitos asignados:");
        lblLista.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        agregarCampoScroll(centro, lblLista, scroll, y++);

        panel.add(centro, BorderLayout.CENTER);

        // BOTÓN CERRAR
        JButton cerrar = new JButton("Cerrar");
        cerrar.setPreferredSize(new Dimension(120, 40));
        cerrar.addActionListener(e -> dispose());

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.setBackground(new Color(250, 250, 250));
        sur.add(cerrar);

        panel.add(sur, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    // -----------------------------
    // ESTILO UNIFICADO (labels + combos iguales que en AgregarMateriaFrame)
    // -----------------------------
    private void agregarCampoCombo(JPanel panel, String labelText, JComboBox<?> combo, int row) {

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setPreferredSize(new Dimension(160, 25)); // ← ANCHO FIJO DEL LABEL

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(combo, gbc);
    }

    private void agregarCampoScroll(JPanel panel, JLabel label, JScrollPane scroll, int row) {

        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setPreferredSize(new Dimension(160, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; 
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1; 
        gbc.weightx = 1;
        panel.add(scroll, gbc);
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
    }

    // ----------------------------------------
    // ASIGNACIÓN DE PRERREQUISITOS
    // ----------------------------------------
    private void asignarPrerrequisito(MateriaDAO dao) {
        String materiaSel = (String) comboMateria.getSelectedItem();
        String requisitoSel = (String) comboRequisito.getSelectedItem();

        if (materiaSel == null || requisitoSel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione ambas materias");
            return;
        }

        int idMateria = MateriaDAO.extraerIdDeCombo(materiaSel);
        int idRequisito = MateriaDAO.extraerIdDeCombo(requisitoSel);

        if (idMateria == idRequisito) {
            JOptionPane.showMessageDialog(this, "Una materia no puede ser prerrequisito de sí misma");
            return;
        }

        if (dao.agregarPrerrequisito(idMateria, idRequisito)) {
            String texto = requisitoSel.split(" \\| ")[1] + " → " + materiaSel.split(" \\| ")[1];
            modeloLista.addElement(texto);
            JOptionPane.showMessageDialog(this, "Prerrequisito asignado correctamente");
        } else {
            JOptionPane.showMessageDialog(this, "Error al asignar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
