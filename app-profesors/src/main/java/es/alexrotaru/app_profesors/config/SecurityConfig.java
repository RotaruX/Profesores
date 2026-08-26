package es.alexrotaru.app_profesors.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration -> le dice a Spring que esta clase define configuracion para la aplicacion,
// en este caso, la configuracion de seguridad
@Configuration
public class SecurityConfig {

    // @Bean -> le dice a Spring "gestiona tu este objeto", para poder usarlo en toda la app
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desactivamos CSRF: es una proteccion pensada para formularios HTML tradicionales
            // con sesiones; nuestra API usa JSON + (mas adelante) tokens JWT, no la necesitamos
            .csrf(csrf -> csrf.disable())

            // Configuramos que rutas son publicas y cuales necesitan estar logueado
            .authorizeHttpRequests(auth -> auth
                // /auth/register y /auth/login deben ser accesibles SIN estar autenticado
                // (logico: para hacer login todavia no tienes sesion)
                .requestMatchers("/auth/**").permitAll()
                // el resto de rutas si necesitaran autenticacion (cuando las creemos)
                .anyRequest().authenticated()
            )

            // No usamos sesiones tradicionales de servidor (HttpSession).
            // Mas adelante, con JWT, cada peticion llevara su propio token,
            // no necesitamos que el servidor "recuerde" quien esta logueado
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Desactivamos el formulario de login por defecto de Spring Security
            // y la autenticacion basica (la que generaba esa contraseña aleatoria en consola)
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}