package es.alexrotaru.app_profesors.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import es.alexrotaru.app_profesors.Profesor;
import es.alexrotaru.app_profesors.dto.LoginRequest;
import es.alexrotaru.app_profesors.dto.RegisterRequest;
import es.alexrotaru.app_profesors.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest datos) {
        try {
            Profesor nuevoProfesor = authService.registrar(datos);
            return ResponseEntity.ok(nuevoProfesor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest datos, HttpServletRequest request) {
        try {
            Profesor profesor = authService.login(datos);

            // Creamos un "Authentication": el objeto que Spring Security usa
            // para saber quien esta autenticado y con que permisos (roles)
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    profesor.getCorreo(),
                    null, // no hace falta la contraseña aqui, ya la validamos en authService.login()
                    List.of(() -> "ROLE_" + profesor.getRol()) // el rol del profesor, ej: ROLE_PROFESOR
            );

            // Guardamos esa autenticacion en el "contexto de seguridad" de Spring
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // Y lo persistimos en la sesion HTTP, para que sobreviva a esta peticion
            // y se recuerde en las siguientes
            HttpSession session = request.getSession(true); // true = crea la sesion si no existe
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            return ResponseEntity.ok(profesor);

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}