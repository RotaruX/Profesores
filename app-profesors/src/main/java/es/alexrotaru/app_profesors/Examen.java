package es.alexrotaru.app_profesors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "examenes")
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String curso; // uno de los cursos del propio profesor (ej: "4º ESO C")

    @Column(name = "fecha_examen")
    private LocalDate fecha;

    // El profesor dueño del examen (igual que en Pregunta)
    @ManyToOne
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;

    // @ManyToMany -> un examen tiene varias preguntas, y una pregunta
    // puede estar en varios examenes. @JoinTable crea la tabla intermedia
    // "examen_preguntas" que guarda esas relaciones, sin que la toques tu.
    @ManyToMany
    @JoinTable(
        name = "examen_preguntas",
        joinColumns = @JoinColumn(name = "examen_id"),
        inverseJoinColumns = @JoinColumn(name = "pregunta_id")
    )
    private List<Pregunta> preguntas = new ArrayList<>();

    public Examen() {
    }

    public Examen(String curso, LocalDate fecha, Profesor profesor) {
        this.curso = curso;
        this.fecha = fecha;
        this.profesor = profesor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Profesor getProfesor() { return profesor; }
    public void setProfesor(Profesor profesor) { this.profesor = profesor; }

    public List<Pregunta> getPreguntas() { return preguntas; }
    public void setPreguntas(List<Pregunta> preguntas) { this.preguntas = preguntas; }
}