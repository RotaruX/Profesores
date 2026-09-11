package es.alexrotaru.app_profesors.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.alexrotaru.app_profesors.Pregunta;
import es.alexrotaru.app_profesors.Profesor;
import es.alexrotaru.app_profesors.dto.PreguntaRequest;
import es.alexrotaru.app_profesors.dto.PreguntaResponse;
import es.alexrotaru.app_profesors.repository.PreguntaRepository;
import es.alexrotaru.app_profesors.repository.ProfesorRepository;

@RestController
@RequestMapping("/preguntas")
public class PreguntaController {

    private final PreguntaRepository preguntaRepository;
    private final ProfesorRepository profesorRepository;

    public PreguntaController(PreguntaRepository preguntaRepository, ProfesorRepository profesorRepository) {
        this.preguntaRepository = preguntaRepository;
        this.profesorRepository = profesorRepository;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody PreguntaRequest datos, Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        Profesor profesor = profesorRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        Pregunta pregunta = new Pregunta(datos.getTexto(), datos.getAsignatura(), datos.getDificultad(), profesor);
        Pregunta guardada = preguntaRepository.save(pregunta);

        return ResponseEntity.ok(aRespuesta(guardada));
    }

    @GetMapping
    public ResponseEntity<?> listar(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        Profesor profesor = profesorRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        // .stream().map(...).collect(...) -> recorre la lista de Pregunta y la
        // convierte en una lista de PreguntaResponse, una por una
        List<PreguntaResponse> preguntas = preguntaRepository.findByProfesorId(profesor.getId())
                .stream()
                .map(this::aRespuesta)
                .collect(Collectors.toList());

        return ResponseEntity.ok(preguntas);
    }

    private PreguntaResponse aRespuesta(Pregunta p) {
        return new PreguntaResponse(p.getId(), p.getTexto(), p.getAsignatura(), p.getDificultad());
    }
}