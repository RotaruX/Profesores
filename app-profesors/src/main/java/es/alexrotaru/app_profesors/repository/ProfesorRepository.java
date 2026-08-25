package es.alexrotaru.app_profesors.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.alexrotaru.app_profesors.Profesor;

// JpaRepository<Profesor, Long> -> Profesor es la entidad, Long es el tipo del @Id
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {

    // Spring genera automáticamente el SQL a partir del nombre del método:
    // busca un profesor por su correo, útil para el login
    Optional<Profesor> findByCorreo(String correo);

    // comprueba si ya existe un profesor con ese correo, para el registro
    boolean existsByCorreo(String correo);

    // comprueba si ya existe un profesor con ese DNI
    boolean existsByDni(String dni);
}