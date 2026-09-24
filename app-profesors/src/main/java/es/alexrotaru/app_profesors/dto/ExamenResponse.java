package es.alexrotaru.app_profesors.dto;

import java.time.LocalDate;

public class ExamenResponse {
    private Long id;
    private String curso;
    private LocalDate fecha;
    private int numeroPreguntas;
    private String archivoUrl;

    public ExamenResponse(Long id, String curso, LocalDate fecha, int numeroPreguntas, String archivoUrl) {
        this.id = id;
        this.curso = curso;
        this.fecha = fecha;
        this.numeroPreguntas = numeroPreguntas;
        this.archivoUrl = archivoUrl;
    }

    public String getArchivoUrl() { return archivoUrl; }
    public void setArchivoUrl(String archivoUrl) { this.archivoUrl = archivoUrl; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public int getNumeroPreguntas() { return numeroPreguntas; }
    public void setNumeroPreguntas(int numeroPreguntas) { this.numeroPreguntas = numeroPreguntas; }
    
}