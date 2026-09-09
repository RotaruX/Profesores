package es.alexrotaru.app_profesors.dto;

public class ProfesorResponse {
    private Long id;
    private String nombre;
    private String apellidos;
    private String correo;
    private String asignatura;
    private String cursos;
    private String rol;

    public ProfesorResponse() {
    }

    public ProfesorResponse(Long id, String nombre, String apellidos, String correo,
                             String asignatura, String cursos, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.asignatura = asignatura;
        this.cursos = cursos;
        this.rol = rol;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }

    public String getCursos() { return cursos; }
    public void setCursos(String cursos) { this.cursos = cursos; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}