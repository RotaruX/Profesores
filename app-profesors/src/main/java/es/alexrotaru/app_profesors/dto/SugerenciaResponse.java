package es.alexrotaru.app_profesors.dto;

import java.time.LocalDate;

public class SugerenciaResponse {
    private Long id;
    private String texto;
    private LocalDate fecha;
    private Long preguntaId;
    private String preguntaTexto;

    public SugerenciaResponse() {}

    public SugerenciaResponse(Long id, String texto, LocalDate fecha, Long preguntaId, String preguntaTexto) {
        this.id = id;
        this.texto = texto;
        this.fecha = fecha;
        this.preguntaId = preguntaId;
        this.preguntaTexto = preguntaTexto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Long getPreguntaId() { return preguntaId; }
    public void setPreguntaId(Long preguntaId) { this.preguntaId = preguntaId; }
    public String getPreguntaTexto() { return preguntaTexto; }
    public void setPreguntaTexto(String preguntaTexto) { this.preguntaTexto = preguntaTexto; }
}