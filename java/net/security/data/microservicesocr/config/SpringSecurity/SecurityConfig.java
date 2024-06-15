package net.security.data.microservicesocr.config.SpringSecurity;
import net.security.data.microservicesocr.Filters.JWTTokenValidator;
import net.security.data.microservicesocr.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

// Este código configura la seguridad de una aplicación web utilizando Spring Security.
// Establece una lista de rutas que no requieren autenticación, define la gestión de
// sesiones como "stateless" (sin estado), configura un filtro para validar tokens JWT,
// proporciona un administrador de autenticación y configura un proveedor de
// autenticación con codificación de contraseñas.

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    @Autowired
    private JWTTokenValidator jwtTokenValidator;
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/api/v1/version",
            "/api/public/**",
            "/api/public/authenticate",
            "/actuator/*",
            "/swagger-ui/**",
            "/auth/**"
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception, AuthenticationServiceException {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->{
                    auth.requestMatchers(AUTH_WHITELIST).permitAll().anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Configuracion del Session Management como Stateless
                .formLogin((AbstractHttpConfigurer::disable))
                // Se agrega el filtro del JWT
                .addFilterBefore(jwtTokenValidator, BasicAuthenticationFilter.class)
                //Se agrega un Entry Point para el manejo de funciones dentro del contexto de errores de autenticacion
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
    // Se define los parametros del proveedor de autenticacion con un DaoAuthenticationProvider
    // el BCryptPasswordEncoder como metodo de codificacion de la contrasena y un userDetailsService personalizado
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        //Se define el passwordEncoder con el metodo BCryptPasswordEncoder
        return new BCryptPasswordEncoder();
    }
}
