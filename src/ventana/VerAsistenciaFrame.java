package ventana;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import clases.Asistencia;
import dao.AsistenciaDAO;

public class VerAsistenciaFrame extends JFrame {

    private JPanel contentPane;
    private JTable tablaAsistencia;
    private DefaultTableModel modeloTabla;

    private int idEstudiante;
    private int idParalelo;

    public VerAsistenciaFrame(int idEstudiante, int idParalelo) {
        this.idEstudiante = idEstudiante;
        this.idParalelo = idParalelo;

        setTitle("Registro de Asistencia");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 750, 450);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        // ============ TABLA ===============
        modeloTabla = new DefaultTableModel(
            new String[]{"Fecha", "Estado", "Materia", "Paralelo", "Semestre", "Año"}, 
            0
        );

        tablaAsistencia = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaAsistencia);

        contentPane.add(scrollPane, BorderLayout.CENTER);

        cargarAsistencias();
    }

    private void cargarAsistencias() {

        AsistenciaDAO dao = new AsistenciaDAO();
        List<Asistencia> lista = dao.listarAsistenciasPorParalelo(idEstudiante, idParalelo);

        modeloTabla.setRowCount(0); // limpiar filas anteriores

        for (Asistencia a : lista) {
            modeloTabla.addRow(new Object[]{
                a.getFecha(),
                a.getEstadoAsis(),
                a.getMateria(),
                a.getParalelo(),
                a.getSemestre(),
                a.getAnio()
            });
        }

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "No existen registros de asistencia para este paralelo.",
                "Sin registros",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
