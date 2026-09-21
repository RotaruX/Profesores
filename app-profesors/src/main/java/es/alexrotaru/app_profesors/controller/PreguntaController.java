package es.alexrotaru.app_profesors.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

        List<PreguntaResponse> preguntas = preguntaRepository.findByProfesorId(profesor.getId())
                .stream()
                .map(this::aRespuesta)
                .collect(Collectors.toList());

        return ResponseEntity.ok(preguntas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody PreguntaRequest datos,
                                         Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        Profesor profesor = profesorRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        Pregunta pregunta = preguntaRepository.findById(id).orElse(null);
        if (pregunta == null) {
            return ResponseEntity.status(404).body("Pregunta no encontrada");
        }

        // Comprobamos que la pregunta es SUYA, no de otro profesor
        if (!pregunta.getProfesor().getId().equals(profesor.getId())) {
            return ResponseEntity.status(403).body("No tienes permiso para editar esta pregunta");
        }

        pregunta.setTexto(datos.getTexto());
        pregunta.setAsignatura(datos.getAsignatura());
        pregunta.setDificultad(datos.getDificultad());

        Pregunta actualizada = preguntaRepository.save(pregunta);
        return ResponseEntity.ok(aRespuesta(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        Profesor profesor = profesorRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        Pregunta pregunta = preguntaRepository.findById(id).orElse(null);
        if (pregunta == null) {
            return ResponseEntity.status(404).body("Pregunta no encontrada");
        }

        if (!pregunta.getProfesor().getId().equals(profesor.getId())) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar esta pregunta");
        }

        try {
            preguntaRepository.delete(pregunta);
            return ResponseEntity.ok("Pregunta eliminada");
        } catch (DataIntegrityViolationException e) {
            // Salta si la pregunta esta siendo usada en algun examen (tabla examen_preguntas)
            return ResponseEntity.badRequest()
                    .body("Esta pregunta está siendo usada en uno o más exámenes. Quítala de esos exámenes antes de eliminarla.");
        }
    }

    private PreguntaResponse aRespuesta(Pregunta p) {
        return new PreguntaResponse(p.getId(), p.getTexto(), p.getAsignatura(), p.getDificultad());
    }
}