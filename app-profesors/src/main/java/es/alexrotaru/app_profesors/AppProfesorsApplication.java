package es.alexrotaru.app_profesors;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import es.alexrotaru.app_profesors.repository.ProfesorRepository;

@SpringBootApplication
public class AppProfesorsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppProfesorsApplication.class, args);
    }

    // Spring "inyecta" automáticamente el ProfesorRepository como parámetro de este método
    @Bean
    public CommandLineRunner probarProfesor(ProfesorRepository profesorRepository) {
        return args -> {

            // Creamos un profesor nuevo en memoria
            Profesor profesor = new Profesor();
            profesor.setNombre("Laura");
            profesor.setApellidos("Martínez García");
            profesor.setFechaNacimiento(LocalDate.of(1990, 5, 12));
            profesor.setCorreo("laura.martinez@example.com");
            profesor.setDni("12345678A");
            profesor.setAsignatura("Matemáticas");
            profesor.setCursos("2º ESO, 3º ESO");
            profesor.setPassword("password123");
            profesor.setRol("PROFESOR");

            // save() lo guarda en MySQL de verdad (INSERT). Devuelve el mismo profesor, pero ya con su id asignado
            Profesor profesorGuardado = profesorRepository.save(profesor);
            System.out.println("----- Profesor guardado en la base de datos -----");
            System.out.println("Id asignado por MySQL: " + profesorGuardado.getId());

            // Ahora lo buscamos de nuevo, esta vez por su correo, usando el metodo que creamos en el Repository
            Optional<Profesor> resultado = profesorRepository.findByCorreo("laura.martinez@example.com");

            if (resultado.isPresent()) {
                Profesor encontrado = resultado.get();
                System.out.println("----- Profesor encontrado por correo -----");
                System.out.println("Nombre: " + encontrado.getNombre());
                System.out.println("Correo: " + encontrado.getCorreo());
            } else {
                System.out.println("No se encontró ningún profesor con ese correo.");
            }

            // Comprobamos cuantos profesores hay en total (metodo que viene gratis con JpaRepository)
            long total = profesorRepository.count();
            System.out.println("Total de profesores en la base de datos: " + total);
        };
    }
}