package es.alexrotaru.app_profesors.dto;

import java.time.LocalDate;
import java.util.List;

public class ExamenDetalleResponse {
    private Long id;
    private String curso;
    private LocalDate fecha;
    private List<PreguntaResponse> preguntas;

    public ExamenDetalleResponse() {}

    public ExamenDetalleResponse(Long id, String curso, LocalDate fecha, List<PreguntaResponse> preguntas) {
        this.id = id;
        this.curso = curso;
        this.fecha = fecha;
        this.preguntas = preguntas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public List<PreguntaResponse> getPreguntas() { return preguntas; }
    public void setPreguntas(List<PreguntaResponse> preguntas) { this.preguntas = preguntas; }
}