package es.alexrotaru.app_profesors.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.alexrotaru.app_profesors.Pregunta;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    // Busca todas las preguntas de un profesor concreto (su banco personal).
    // Spring entiende "ProfesorId" porque Pregunta tiene un campo "profesor",
    // y ese Profesor tiene a su vez un campo "id" -> profesor.id
    List<Pregunta> findByProfesorId(Long profesorId);
}