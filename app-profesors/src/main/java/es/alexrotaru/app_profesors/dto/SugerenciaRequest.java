package es.alexrotaru.app_profesors.dto;

public class SugerenciaRequest {
    private Long preguntaId;
    private String texto;

    public SugerenciaRequest() {}

    public Long getPreguntaId() { return preguntaId; }
    public void setPreguntaId(Long preguntaId) { this.preguntaId = preguntaId; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}