package es.alexrotaru.app_profesors.dto;

public class PreguntaRequest {
    private String texto;
    private String asignatura;
    private String dificultad;

    public PreguntaRequest() {}

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }
    public String getDificultad() { return dificultad; }
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }
}