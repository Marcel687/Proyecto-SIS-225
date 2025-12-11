package ventana;
import conexion.ConexionDB;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AgregarHorarioFrame extends JFrame {
    private JComboBox<String> comboDia;
    private JTextField txtHoraInicio, txtHoraFin, txtUbicacion;
    private int idParalelo;
    private String nombreParalelo;

    // Constructor normal (si lo llamas desde otro lado)
    public AgregarHorarioFrame() {
        this(-1, null);
    }

    // Constructor que recibe el paralelo recién creado
    public AgregarHorarioFrame(int idParalelo, String nombreParalelo) {
        this.idParalelo = idParalelo;
        this.nombreParalelo = nombreParalelo;
        setTitle("Asignar Horarios - " + (nombreParalelo != null ? nombreParalelo : "Seleccione paralelo"));
        setSize(600, 560);  // Aumentado para el nuevo campo
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(new Color(245, 246, 250));
        JLabel titulo = new JLabel("Asignar Horario al Paralelo", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        panel.add(titulo, gbc(0, 0, 2));
        if (nombreParalelo != null) {
            JLabel lblInfo = new JLabel("Paralelo: " + nombreParalelo, SwingConstants.CENTER);
            lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            lblInfo.setForeground(new Color(41, 128, 185));
            panel.add(lblInfo, gbc(0, 1, 2));
        }
        String[] dias = {"LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO"};
        comboDia = new JComboBox<>(dias);
        agregarCampoCombo(panel, "Día:", comboDia, 2);
        txtHoraInicio = agregarCampo(panel, "Hora inicio (HH:MM):", 3);
        txtHoraFin = agregarCampo(panel, "Hora fin (HH:MM):", 4);
        txtUbicacion = agregarCampo(panel, "Aula (ej. Aula A101):", 5);
        JButton btnAgregar = new JButton("Agregar Horario");
        btnAgregar.setBackground(new Color(46, 125, 50));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAgregar.addActionListener(e -> agregarOtroHorario());
        JButton btnCerrar = new JButton("Finalizar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel botones = new JPanel();
        botones.add(btnAgregar);
        botones.add(btnCerrar);
        panel.add(botones, gbc(0, 6, 2));  // Ajustado a fila 6
        add(panel);
        setVisible(true);
    }

    private void agregarOtroHorario() {
        String dia = (String) comboDia.getSelectedItem();
        String inicio = txtHoraInicio.getText().trim();
        String fin = txtHoraFin.getText().trim();
        String ubicacion = txtUbicacion.getText().trim();
        if (inicio.isEmpty() || fin.isEmpty() || ubicacion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!inicio.matches("^([0-1][0-9]|2[0-3]):[0-5][0-9]$") ||
            !fin.matches("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")) {
            JOptionPane.showMessageDialog(this, "Formato HH:MM requerido (ej. 07:00)", "Formato inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Opcional: Validar longitud o formato de ubicación si es necesario
        if (ubicacion.length() > 50) {  // Ejemplo de validación simple
            JOptionPane.showMessageDialog(this, "Ubicación demasiado larga (máx. 50 caracteres)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String sql = "INSERT INTO horario (id_paralelo, dia, hora_inicio, hora_fin, aula) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idParalelo);
            ps.setString(2, dia);
            ps.setString(3, inicio);
            ps.setString(4, fin);
            ps.setString(5, ubicacion);  // Nuevo parámetro
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                "Horario agregado: " + dia + " " + inicio + " - " + fin + " en " + ubicacion,
                "Guardado", JOptionPane.INFORMATION_MESSAGE);
            txtHoraInicio.setText("");
            txtHoraFin.setText("");
            txtUbicacion.setText("");  // Limpiar nuevo campo
            txtHoraInicio.requestFocus();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(this, "Ya existe ese horario para este paralelo", "Duplicado", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private GridBagConstraints gbc(int x, int y, int w) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x; g.gridy = y; g.gridwidth = w;
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }

    private JTextField agregarCampo(JPanel p, String label, int row) {
        p.add(new JLabel(label), gbc(0, row, 1));
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(150, 40));
        p.add(tf, gbc(1, row, 1));
        return tf;
    }

    private void agregarCampoCombo(JPanel p, String label, JComboBox<?> cb, int row) {
        p.add(new JLabel(label), gbc(0, row, 1));
        p.add(cb, gbc(1, row, 1));
    }
}