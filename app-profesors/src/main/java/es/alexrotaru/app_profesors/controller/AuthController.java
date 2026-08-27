package es.alexrotaru.app_profesors.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
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
    
    @GetMapping("/perfil")
    public ResponseEntity<?> perfil(Authentication authentication) {

        // Si no hay sesion activa, Spring Security ya habria bloqueado la peticion
        // antes de llegar aqui (gracias a anyRequest().authenticated() en SecurityConfig).
        // Si llegamos a este punto, es porque "authentication" contiene al usuario logueado.

        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        // authentication.getName() devuelve el "nombre" que le dimos al crear el token en el login:
        // en nuestro caso, el correo del profesor (mira UsernamePasswordAuthenticationToken en login())
        String correo = authentication.getName();

        return ResponseEntity.ok("Sesión activa para: " + correo);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest datos, HttpServletRequest request) {
        try {
            Profesor profesor = authService.login(datos);

            // Creamos un "Authentication": el objeto que Spring Security usa
            // para saber quien esta autenticado y con que permisos (roles)
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    profesor.getCorreo(),
                    null,
                    List.of(() -> "ROLE_" + profesor.getRol())
            );

            // Guardamos esa autenticacion en el "contexto de seguridad" de Spring
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // Y lo persistimos en la sesion HTTP, para que sobreviva a esta peticion
            // y se recuerde en las siguientes
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            System.out.println("Sesión creada con id: " + session.getId());

            return ResponseEntity.ok(profesor);

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}