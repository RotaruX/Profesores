package es.alexrotaru.app_profesors.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.alexrotaru.app_profesors.Profesor;
import es.alexrotaru.app_profesors.dto.LoginRequest;
import es.alexrotaru.app_profesors.dto.RegisterRequest;
import es.alexrotaru.app_profesors.services.AuthService;

// @RestController -> le dice a Spring que esta clase recibe peticiones HTTP
// y que las respuestas se devuelven directamente como datos (JSON), no como vistas HTML
@RestController
// @RequestMapping -> prefijo comun para todas las rutas de este controller
// es decir, todo empezara por /auth (ej: /auth/register, /auth/login)
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // Spring inyecta automaticamente el AuthService aqui
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // @PostMapping -> esta funcion responde a peticiones POST en /auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest datos) {
        try {
            // Llamamos al Service, que hace toda la logica (validar, encriptar, guardar)
            Profesor nuevoProfesor = authService.registrar(datos);

            // 200 OK + el profesor creado (sin la contraseña, mas adelante lo mejoramos)
            return ResponseEntity.ok(nuevoProfesor);

        } catch (RuntimeException e) {
            // Si el Service lanza una excepcion (correo/dni duplicado), devolvemos error 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // @PostMapping -> esta funcion responde a peticiones POST en /auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest datos) {
        try {
            Profesor profesor = authService.login(datos);

            // Por ahora devolvemos el profesor; mas adelante aqui devolveremos un token JWT
            return ResponseEntity.ok(profesor);

        } catch (RuntimeException e) {
            // Si el correo no existe o la contraseña no coincide, devolvemos error 401 (no autorizado)
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}