package es.alexrotaru.app_profesors.controller;

import java.util.List;
import java.util.Optional;

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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import es.alexrotaru.app_profesors.Profesor;
import es.alexrotaru.app_profesors.dto.LoginRequest;
import es.alexrotaru.app_profesors.dto.ProfesorResponse;
import es.alexrotaru.app_profesors.dto.RegisterRequest;
import es.alexrotaru.app_profesors.repository.ProfesorRepository;
import es.alexrotaru.app_profesors.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ProfesorRepository profesorRepository;

    // Spring inyecta automaticamente los dos: AuthService y ProfesorRepository
    public AuthController(AuthService authService, ProfesorRepository profesorRepository) {
        this.authService = authService;
        this.profesorRepository = profesorRepository;
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

        if (authentication == null) {
            return ResponseEntity.status(401).body("No hay sesión activa");
        }

        String correo = authentication.getName();

        Optional<Profesor> resultado = profesorRepository.findByCorreo(correo);

        if (resultado.isEmpty()) {
            return ResponseEntity.status(404).body("Profesor no encontrado");
        }

        Profesor profesor = resultado.get();

        ProfesorResponse respuesta = new ProfesorResponse(
                profesor.getId(),
                profesor.getNombre(),
                profesor.getApellidos(),
                profesor.getCorreo(),
                profesor.getAsignatura(),
                profesor.getCursos(),
                profesor.getRol()
        );

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Sesión cerrada");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest datos, HttpServletRequest request) {
        try {
            Profesor profesor = authService.login(datos);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    profesor.getCorreo(),
                    null,
                    List.of(() -> "ROLE_" + profesor.getRol())
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            return ResponseEntity.ok(profesor);

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}