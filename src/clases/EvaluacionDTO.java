package clases;

public class EvaluacionDTO {
    private int idEvaluacion;
    private String nombre; // Ej: "Parcial 1", "Final"
    private double peso;

    public EvaluacionDTO(int idEvaluacion, int numero, String tipo, double peso) {
        this.idEvaluacion = idEvaluacion;
        this.nombre = tipo.toUpperCase() + " " + numero;
        this.peso = peso;
    }

    public int getIdEvaluacion() { return idEvaluacion; }
    public String getNombre() { return nombre; }
}