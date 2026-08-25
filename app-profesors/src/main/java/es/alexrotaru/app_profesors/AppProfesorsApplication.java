package es.alexrotaru.app_profesors;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AppProfesorsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppProfesorsApplication.class, args);
    }

    // Este método se ejecuta automáticamente al arrancar la aplicación
    @Bean
    public CommandLineRunner probarProfesor() {
        return args -> {

            // Creamos un objeto Profesor vacío (usando el constructor sin parámetros que da Lombok)
            Profesor profesor = new Profesor();

            // Rellenamos sus atributos usando los setters (los genera Lombok con @Setter)
            profesor.setNombre("Alex");
            profesor.setApellidos("Rotaru Alergus");
            profesor.setFechaNacimiento(LocalDate.of(2000, 12, 16));
            profesor.setCorreo("rotarualex1612@gmail.com");
            profesor.setDni("61011880X");
            profesor.setAsignatura("Informatica");
            profesor.setCursos("2º ESO, 4º ESO");
            profesor.setPassword("password123"); // más adelante irá encriptada
            profesor.setRol("PROFESOR");

            // Mostramos los datos por consola usando los getters (los genera Lombok con @Getter)
            System.out.println("----- Datos del profesor -----");
            System.out.println("Nombre: " + profesor.getNombre());
            System.out.println("Apellidos: " + profesor.getApellidos());
            System.out.println("Fecha nacimiento: " + profesor.getFechaNacimiento());
            System.out.println("Correo: " + profesor.getCorreo());
            System.out.println("DNI: " + profesor.getDni());
            System.out.println("Asignatura: " + profesor.getAsignatura());
            System.out.println("Cursos: " + profesor.getCursos());
            System.out.println("Rol: " + profesor.getRol());
        };
    }
}