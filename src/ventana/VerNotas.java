package ventana;

import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import clases.Notas;
import dao.NotasDAO;

public class VerNotas extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private int idEstudiante;
    private int idParalelo;
    private JLabel lblSuma;
    private JLabel lblPromedio;

    public VerNotas(int idEstudiante, int idParalelo) {
        this.idEstudiante = idEstudiante;
        this.idParalelo = idParalelo;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 450);
        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        JLabel lblTitulo = new JLabel("Notas del Estudiante");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblTitulo, BorderLayout.NORTH);

        // Crear JTable
        table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para suma y promedio
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        lblSuma = new JLabel("Suma de Notas Ponderadas: 0");
        lblSuma.setFont(new Font("Arial", Font.BOLD, 14));
        lblPromedio = new JLabel("Promedio: 0");
        lblPromedio.setFont(new Font("Arial", Font.BOLD, 14));
        panelInferior.add(lblSuma);
        panelInferior.add(lblPromedio);

        contentPane.add(panelInferior, BorderLayout.SOUTH);

        // Cargar datos
        cargarNotas();
    }

    private void cargarNotas() {
        NotasDAO dao = new NotasDAO();
        List<Notas> lista = dao.listarNotasPorEstudiante(idEstudiante, idParalelo);

        String[] columnas = {"Nro Evaluación", "Peso", "Nota", "Nota Ponderada", "Semana"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        double sumaPonderada = 0;

        for (Notas n : lista) {
            Object[] fila = {
                n.getNroEvaluacion(),
                n.getPeso(),
                n.getNota(),
                n.getNotaPonderada(),
                n.getSemana()
            };
            model.addRow(fila);

            sumaPonderada += n.getNotaPonderada();
        }

        table.setModel(model);

        // Calcular promedio
        double promedio = lista.isEmpty() ? 0 : sumaPonderada / lista.size();

        lblSuma.setText(String.format("Suma de Notas Ponderadas: %.2f", sumaPonderada));
        lblPromedio.setText(String.format("Promedio: %.2f", promedio));
    }
}
