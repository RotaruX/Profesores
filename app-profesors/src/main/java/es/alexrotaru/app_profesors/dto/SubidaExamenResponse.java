package es.alexrotaru.app_profesors.dto;

import java.util.List;

public class SubidaExamenResponse {
    private Long examenId;
    private String archivoUrl;
    private List<String> preguntasDetectadas;

    public SubidaExamenResponse() {}

    public SubidaExamenResponse(Long examenId, String archivoUrl, List<String> preguntasDetectadas) {
        this.examenId = examenId;
        this.archivoUrl = archivoUrl;
        this.preguntasDetectadas = preguntasDetectadas;
    }

    public Long getExamenId() { return examenId; }
    public void setExamenId(Long examenId) { this.examenId = examenId; }
    public String getArchivoUrl() { return archivoUrl; }
    public void setArchivoUrl(String archivoUrl) { this.archivoUrl = archivoUrl; }
    public List<String> getPreguntasDetectadas() { return preguntasDetectadas; }
    public void setPreguntasDetectadas(List<String> preguntasDetectadas) { this.preguntasDetectadas = preguntasDetectadas; }
}