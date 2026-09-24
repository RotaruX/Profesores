package es.alexrotaru.app_profesors.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;

@Service
public class ExtraccionPreguntasService {

    // Detecta lineas que empiezan por un numero de pregunta: "1.", "2)", "Pregunta 3:", etc.
	// Dos formas de detectar el inicio de una pregunta:
	// A) con palabra delante ("Ejercicio 1", "Pregunta 3:", "Cuestion 2") - la puntuacion es opcional
	// B) sin palabra delante, pero entonces la puntuacion es obligatoria ("1.", "1)", "1-", "1 -", "1:")
//	    (sin esto, cualquier numero suelto al principio de una linea se confundiria con una pregunta)
	private static final Pattern PATRON_INICIO_PREGUNTA = Pattern.compile(
	        "^(?:(?:pregunta|ejercicio|cuesti[oó]n)\\s+\\d{1,2}\\s*[\\.\\)\\-:]?\\s*"
	        + "|\\d{1,2}\\s*[\\.\\)\\-:]\\s*)",
	        Pattern.CASE_INSENSITIVE
	);

    public String extraerTexto(Path rutaArchivo, String extension) throws IOException {
        String ext = extension.toLowerCase();
        if (ext.equals(".pdf")) {
            return extraerTextoPdf(rutaArchivo);
        } else if (ext.equals(".docx")) {
            return extraerTextoDocx(rutaArchivo);
        } else {
            throw new IOException("Formato no soportado para analizar. Sube un PDF o un Word (.docx).");
        }
    }

    private String extraerTextoPdf(Path ruta) throws IOException {
        try (PDDocument documento = Loader.loadPDF(ruta.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(documento);
        }
    }

    private String extraerTextoDocx(Path ruta) throws IOException {
        try (InputStream in = Files.newInputStream(ruta);
             XWPFDocument documento = new XWPFDocument(in)) {
            StringBuilder texto = new StringBuilder();
            for (XWPFParagraph parrafo : documento.getParagraphs()) {
                texto.append(parrafo.getText()).append("\n");
            }
            return texto.toString();
        }
    }

    // Separa el texto completo en preguntas individuales, usando el patron de numeracion.
    // Cada pregunta puede ocupar varias lineas: las va juntando hasta que llega
    // la siguiente linea que empieza por un numero.
    public List<String> extraerPreguntas(String texto) {
        List<String> preguntas = new ArrayList<>();
        String[] lineas = texto.split("\\r\\n|\\r|\\n");

        StringBuilder actual = null;

        for (String linea : lineas) {
            String lineaLimpia = linea.trim();
            if (lineaLimpia.isEmpty()) continue;

            Matcher matcher = PATRON_INICIO_PREGUNTA.matcher(lineaLimpia);
            if (matcher.find()) {
                if (actual != null && actual.length() > 0) {
                    preguntas.add(actual.toString().trim());
                }
                actual = new StringBuilder();
                actual.append(lineaLimpia.substring(matcher.end()).trim());
            } else if (actual != null) {
                actual.append(" ").append(lineaLimpia);
            }
        }

        if (actual != null && actual.length() > 0) {
            preguntas.add(actual.toString().trim());
        }

        return preguntas;
    }
}