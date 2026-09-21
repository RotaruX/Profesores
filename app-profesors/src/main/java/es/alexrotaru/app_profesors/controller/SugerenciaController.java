package es.alexrotaru.app_profesors.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.alexrotaru.app_profesors.Pregunta;
import es.alexrotaru.app_profesors.Profesor;
import es.alexrotaru.app_profesors.Sugerencia;
import es.alexrotaru.app_profesors.dto.SugerenciaRequest;
import es.alexrotaru.app_profesors.dto.SugerenciaResponse;
import es.alexrotaru.app_profesors.repository.PreguntaRepository;
import es.alexrotaru.app_profesors.repository.ProfesorRepository;
import es.alexrotaru.app_profesors.repository.SugerenciaRepository;

@RestController
@RequestMapping("/sugerencias")
public class SugerenciaController {

    private final SugerenciaRepository sugerenciaRepository;
    private final PreguntaRepository preguntaRepository;
    private final ProfesorRepository profesorRepository;

    public SugerenciaController(SugerenciaRepository sugerenciaRepository, PreguntaRepository preguntaRepository,
                                 ProfesorRepository profesorRepository) {
        this.sugerenciaRepository = sugerenciaRepository;
        this.preguntaRepository = preguntaRepository;
        this.profesorRepository = profesorRepository;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody SugerenciaRequest datos, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        Profesor profesor = profesorRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        Pregunta pregunta = preguntaRepository.findById(datos.getPreguntaId()).orElse(null);
        if (pregunta == null) {
            return ResponseEntity.status(404).body("Pregunta no encontrada");
        }
        if (!pregunta.getProfesor().getId().equals(profesor.getId())) {
            return ResponseEntity.status(403).body("No puedes sugerir sobre una pregunta que no es tuya");
        }

        // La fecha la pone el servidor, no el usuario
        Sugerencia sugerencia = new Sugerencia(datos.getTexto(), LocalDate.now(), pregunta, profesor);
        Sugerencia guardada = sugerenciaRepository.save(sugerencia);

        return ResponseEntity.ok(aRespuesta(guardada));
    }

    @GetMapping
    public ResponseEntity<?> listarMias(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        Profesor profesor = profesorRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        List<SugerenciaResponse> sugerencias = sugerenciaRepository.findByProfesorId(profesor.getId())
                .stream()
                .map(this::aRespuesta)
                .collect(Collectors.toList());

        return ResponseEntity.ok(sugerencias);
    }

    @GetMapping("/pregunta/{preguntaId}")
    public ResponseEntity<?> listarPorPregunta(@PathVariable Long preguntaId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        Profesor profesor = profesorRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        Pregunta pregunta = preguntaRepository.findById(preguntaId).orElse(null);
        if (pregunta == null) {
            return ResponseEntity.status(404).body("Pregunta no encontrada");
        }
        if (!pregunta.getProfesor().getId().equals(profesor.getId())) {
            return ResponseEntity.status(403).body("No tienes permiso para ver esto");
        }

        List<SugerenciaResponse> sugerencias = sugerenciaRepository.findByPreguntaId(preguntaId)
                .stream()
                .map(this::aRespuesta)
                .collect(Collectors.toList());

        return ResponseEntity.ok(sugerencias);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        Profesor profesor = profesorRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        Sugerencia sugerencia = sugerenciaRepository.findById(id).orElse(null);
        if (sugerencia == null) {
            return ResponseEntity.status(404).body("Sugerencia no encontrada");
        }
        if (!sugerencia.getProfesor().getId().equals(profesor.getId())) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar esta sugerencia");
        }

        sugerenciaRepository.delete(sugerencia);
        return ResponseEntity.ok("Sugerencia eliminada");
    }

    private SugerenciaResponse aRespuesta(Sugerencia s) {
        return new SugerenciaResponse(
                s.getId(), s.getTexto(), s.getFecha(),
                s.getPregunta().getId(), s.getPregunta().getTexto()
        );
    }
}