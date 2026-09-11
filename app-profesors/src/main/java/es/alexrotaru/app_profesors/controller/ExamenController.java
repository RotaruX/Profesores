package es.alexrotaru.app_profesors.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.alexrotaru.app_profesors.Examen;
import es.alexrotaru.app_profesors.Pregunta;
import es.alexrotaru.app_profesors.Profesor;
import es.alexrotaru.app_profesors.dto.ExamenDetalleResponse;
import es.alexrotaru.app_profesors.dto.ExamenRequest;
import es.alexrotaru.app_profesors.dto.ExamenResponse;
import es.alexrotaru.app_profesors.dto.PreguntaResponse;
import es.alexrotaru.app_profesors.repository.ExamenRepository;
import es.alexrotaru.app_profesors.repository.PreguntaRepository;
import es.alexrotaru.app_profesors.repository.ProfesorRepository;

@RestController
@RequestMapping("/examenes")
public class ExamenController {

    private final ExamenRepository examenRepository;
    private final PreguntaRepository preguntaRepository;
    private final ProfesorRepository profesorRepository;

    public ExamenController(ExamenRepository examenRepository, PreguntaRepository preguntaRepository,
                             ProfesorRepository profesorRepository) {
        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
        this.profesorRepository = profesorRepository;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ExamenRequest datos, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        Profesor profesor = profesorRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        Examen examen = new Examen(datos.getCurso(), datos.getFecha(), profesor);

        if (datos.getPreguntaIds() != null && !datos.getPreguntaIds().isEmpty()) {
            List<Pregunta> preguntas = preguntaRepository.findAllById(datos.getPreguntaIds());
            examen.setPreguntas(preguntas);
        }

        Examen guardado = examenRepository.save(examen);
        return ResponseEntity.ok(aRespuesta(guardado));
    }

    @GetMapping
    public ResponseEntity<?> listar(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        Profesor profesor = profesorRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        List<ExamenResponse> examenes = examenRepository.findByProfesorId(profesor.getId())
                .stream()
                .map(this::aRespuesta)
                .collect(Collectors.toList());

        return ResponseEntity.ok(examenes);
    }

    // Devuelve un examen concreto, CON el detalle completo de sus preguntas
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        Profesor profesor = profesorRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        Examen examen = examenRepository.findById(id).orElse(null);
        if (examen == null) {
            return ResponseEntity.status(404).body("Examen no encontrado");
        }

        // Comprobamos que el examen es SUYO, no de otro profesor
        if (!examen.getProfesor().getId().equals(profesor.getId())) {
            return ResponseEntity.status(403).body("No tienes permiso para ver este examen");
        }

        return ResponseEntity.ok(aDetalle(examen));
    }

    // Actualiza un examen existente: curso, fecha, y la lista de preguntas
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ExamenRequest datos,
                                         Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        Profesor profesor = profesorRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        Examen examen = examenRepository.findById(id).orElse(null);
        if (examen == null) {
            return ResponseEntity.status(404).body("Examen no encontrado");
        }

        if (!examen.getProfesor().getId().equals(profesor.getId())) {
            return ResponseEntity.status(403).body("No tienes permiso para editar este examen");
        }

        examen.setCurso(datos.getCurso());
        examen.setFecha(datos.getFecha());

        if (datos.getPreguntaIds() != null) {
            List<Pregunta> preguntas = preguntaRepository.findAllById(datos.getPreguntaIds());
            examen.setPreguntas(preguntas);
        }

        Examen actualizado = examenRepository.save(examen);
        return ResponseEntity.ok(aDetalle(actualizado));
    }

    private ExamenResponse aRespuesta(Examen e) {
        return new ExamenResponse(e.getId(), e.getCurso(), e.getFecha(), e.getPreguntas().size());
    }

    private ExamenDetalleResponse aDetalle(Examen e) {
        List<PreguntaResponse> preguntas = e.getPreguntas().stream()
                .map(p -> new PreguntaResponse(p.getId(), p.getTexto(), p.getAsignatura(), p.getDificultad()))
                .collect(Collectors.toList());
        return new ExamenDetalleResponse(e.getId(), e.getCurso(), e.getFecha(), preguntas);
    }
}