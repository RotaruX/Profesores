package es.alexrotaru.app_profesors.dto;

import java.time.LocalDate;

public class RegisterRequest {
	private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String correo;
    private String dni;
    private String asignatura;
    private String cursos;
    private String password;
    
    //Constructor vacío
    public RegisterRequest() {
    }
    
    // Constructor con todos los campos
    public RegisterRequest(String nombre, String apellidos, LocalDate fechaNacimiento,
                            String correo, String dni, String asignatura, String cursos, String password) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.correo = correo;
        this.dni = dni;
        this.asignatura = asignatura;
        this.cursos = cursos;
        this.password = password;
    }

    // Getters y Setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public String getCursos() {
        return cursos;
    }

    public void setCursos(String cursos) {
        this.cursos = cursos;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
