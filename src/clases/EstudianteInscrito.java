package clases;

public class EstudianteInscrito {
    private int idInscripcion;
    private int idEstudiante;
    private String nombreCompleto;

    public EstudianteInscrito(int idInscripcion, int idEstudiante, String nombreCompleto) {
        this.idInscripcion = idInscripcion;
        this.idEstudiante = idEstudiante;
        this.nombreCompleto = nombreCompleto;
    }

    public int getIdInscripcion() { return idInscripcion; }
    public String getNombreCompleto() { return nombreCompleto; }
    
    @Override
    public String toString() { return nombreCompleto; }
}