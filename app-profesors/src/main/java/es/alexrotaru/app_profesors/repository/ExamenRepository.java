package es.alexrotaru.app_profesors.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.alexrotaru.app_profesors.Examen;

public interface ExamenRepository extends JpaRepository<Examen, Long> {

    // Busca todos los examenes de un profesor concreto, para mostrarlos como cards
    List<Examen> findByProfesorId(Long profesorId);
}