package es.alexrotaru.app_profesors.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.alexrotaru.app_profesors.Sugerencia;

public interface SugerenciaRepository extends JpaRepository<Sugerencia, Long> {

    // Todas las sugerencias de una pregunta concreta
    List<Sugerencia> findByPreguntaId(Long preguntaId);

    // Todas las sugerencias escritas por un profesor (para la pagina general de "Sugerencias")
    List<Sugerencia> findByProfesorId(Long profesorId);
}