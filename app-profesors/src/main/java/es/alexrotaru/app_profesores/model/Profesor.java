package es.alexrotaru.app_profesores.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Entity -> le dice a Spring que esta clase es una TABLA de la base de datos
@Entity
// @Table -> nombre exacto que tendrá la tabla en MySQL (si no se pone, usa el nombre de la clase)
@Table(name = "profesores")
// @Getter -> Lombok genera automáticamente TODOS los getters (getNombre(), getCorreo(), etc.)
@Getter
// @Setter -> Lombok genera automáticamente TODOS los setters (setNombre(), setCorreo(), etc.)
@Setter
// @NoArgsConstructor -> Lombok genera el constructor vacío Profesor() {}, obligatorio para JPA
@NoArgsConstructor
public class Profesor {

    // @Id -> marca este atributo como la clave primaria (identificador único de cada fila)
    @Id
    // @GeneratedValue -> el valor del id se genera solo (autoincremental: 1, 2, 3...), no hay que asignarlo a mano
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable = false -> este campo es obligatorio, no puede guardarse vacío
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    // name = "..." -> nombre de la columna en MySQL (fecha_nacimiento en vez de fechaNacimiento)
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    // unique = true -> no puede haber dos profesores con el mismo correo
    @Column(nullable = false, unique = true)
    private String correo;

    // unique = true -> no puede haber dos profesores con el mismo DNI
    @Column(nullable = false, unique = true)
    private String dni;

    // Sin @Column -> JPA crea la columna igualmente, usando el nombre de la variable tal cual
    private String asignatura;

    private String cursos;

    // Contraseña del profesor (más adelante se guardará encriptada, no en texto plano)
    @Column(nullable = false)
    private String password;

    // Rol del profesor dentro de la app (por defecto "PROFESOR", útil si en el futuro hay administradores)
    @Column(nullable = false)
    private String rol = "PROFESOR";
}