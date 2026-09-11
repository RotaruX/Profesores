package es.alexrotaru.app_profesors.dto;

import java.time.LocalDate;
import java.util.List;

public class ExamenRequest {
    private String curso;
    private LocalDate fecha;
    private List<Long> preguntaIds; // ids de las preguntas seleccionadas

    public ExamenRequest() {}

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public List<Long> getPreguntaIds() { return preguntaIds; }
    public void setPreguntaIds(List<Long> preguntaIds) { this.preguntaIds = preguntaIds; }
}