package es.alexrotaru.app_profesors.dto;

public class PreguntaResponse {
    private Long id;
    private String texto;
    private String asignatura;
    private String dificultad;

    public PreguntaResponse() {}

    public PreguntaResponse(Long id, String texto, String asignatura, String dificultad) {
        this.id = id;
        this.texto = texto;
        this.asignatura = asignatura;
        this.dificultad = dificultad;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }
    public String getDificultad() { return dificultad; }
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }
}