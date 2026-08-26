package es.alexrotaru.app_profesors.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import es.alexrotaru.app_profesors.Profesor;
import es.alexrotaru.app_profesors.dto.LoginRequest;
import es.alexrotaru.app_profesors.dto.RegisterRequest;
import es.alexrotaru.app_profesors.repository.ProfesorRepository;

// @Service -> le dice a Spring que esta clase contiene logica de negocio,
// y que la gestione el (para poder "inyectarla" despues en el Controller)
@Service
public class AuthService {

    // Dependencia hacia el Repository, para poder consultar/guardar en la base de datos
    private final ProfesorRepository profesorRepository;

    // BCryptPasswordEncoder -> herramienta para encriptar (hashear) contraseñas.
    // Nunca se guarda la contraseña "tal cual" en la base de datos.
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Constructor: Spring "inyecta" automaticamente el ProfesorRepository aqui
    public AuthService(ProfesorRepository profesorRepository) {
        this.profesorRepository = profesorRepository;
    }

    // ---------- REGISTRO ----------
    public Profesor registrar(RegisterRequest datos) {

        // 1. Comprobamos que no exista ya un profesor con ese correo
        if (profesorRepository.existsByCorreo(datos.getCorreo())) {
            throw new RuntimeException("Ya existe un profesor registrado con ese correo");
        }

        // 2. Comprobamos que no exista ya un profesor con ese DNI
        if (profesorRepository.existsByDni(datos.getDni())) {
            throw new RuntimeException("Ya existe un profesor registrado con ese DNI");
        }

        // 3. Creamos el nuevo Profesor a partir de los datos del formulario (RegisterRequest)
        Profesor profesor = new Profesor();
        profesor.setNombre(datos.getNombre());
        profesor.setApellidos(datos.getApellidos());
        profesor.setFechaNacimiento(datos.getFechaNacimiento());
        profesor.setCorreo(datos.getCorreo());
        profesor.setDni(datos.getDni());
        profesor.setAsignatura(datos.getAsignatura());
        profesor.setCursos(datos.getCursos());

        // 4. Encriptamos la contraseña antes de guardarla (jamas en texto plano)
        String passwordEncriptada = passwordEncoder.encode(datos.getPassword());
        profesor.setPassword(passwordEncriptada);

        // 5. El rol lo decide el sistema, no el usuario
        profesor.setRol("PROFESOR");

        // 6. Guardamos en la base de datos y devolvemos el profesor ya creado
        return profesorRepository.save(profesor);
    }

    // ---------- LOGIN ----------
    public Profesor login(LoginRequest datos) {

        // 1. Buscamos al profesor por su correo
        Profesor profesor = profesorRepository.findByCorreo(datos.getCorreo())
                .orElseThrow(() -> new RuntimeException("Correo o contraseña incorrectos"));

        // 2. Comparamos la contraseña que ha escrito con la que esta encriptada en la base de datos
        //    matches() se encarga de encriptar la que llega y compararla, nunca se desencripta la guardada
        boolean coincide = passwordEncoder.matches(datos.getPassword(), profesor.getPassword());

        if (!coincide) {
            throw new RuntimeException("Correo o contraseña incorrectos");
        }

        // 3. Si coincide, devolvemos el profesor (mas adelante, en vez de esto, generaremos un token JWT)
        return profesor;
    }
}