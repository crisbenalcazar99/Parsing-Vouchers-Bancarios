package net.security.data.microservicesocr.config.SpringSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.security.data.microservicesocr.Filters.JWTTokenValidator;
import net.security.data.microservicesocr.enums.MnemonicsEnum;
import net.security.data.microservicesocr.messages.responses.JsonResponse;
import net.security.data.microservicesocr.models.entities.AuditoryEntity;
import net.security.data.microservicesocr.models.entities.CatalogEntity;
import net.security.data.microservicesocr.models.entities.UserEntity;
import net.security.data.microservicesocr.repository.AuditoryRepository;
import net.security.data.microservicesocr.repository.CatalogRepository;
import net.security.data.microservicesocr.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private CatalogRepository catalogRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuditoryRepository auditoryRepository;

    private static final Logger logger = LoggerFactory.getLogger(JWTTokenValidator.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        logger.info("Se ingresa al Entry Point JwtAuthentication");
        ObjectMapper mapper = new ObjectMapper();
        SecurityContext context = SecurityContextHolder.getContext();
        try {
            
            JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.UNAUTHORIZED, "Validation error trows, validation error or wrong endpoint: " + authException.getMessage());
            // Convertir el objeto JsonResponse a una cadena JSON para poder enviarlo en el body
            ResponseEntity<String> responseEntity = new ResponseEntity<>(mapper.writeValueAsString(jsonResponse), HttpStatus.UNAUTHORIZED);
            
            // Establecer el tipo de contenido de la respuesta a JSON
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            UserEntity userEntity = null;
            
            if(context.getAuthentication() != null){
                userEntity = userRepository.findByUsername(context.getAuthentication().getPrincipal().toString())
                .orElse(null);
        }
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.INVALID_TOKEN.getValue())
                .orElse(null);
                
                AuditoryEntity auditoryEntity =  AuditoryEntity.builder()
                .stateRegister(true)
                .endPoint(request.getRequestURI())
                .method(request.getMethod())
                .statusCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .catalogEntity(catalogEntity)
                .response(catalogEntity == null? responseEntity.getBody(): catalogEntity.getDescription())
                .user(userEntity) //agregar user del contexto
                .request(null)
                .direccionIP(request.getRemoteAddr())
                .build();
                auditoryRepository.save(auditoryEntity);
                logger.info("Regitro de Error creado en la tabla de audotoria desde el JWT Entry Point {}", authException.getMessage() );
                
                // Escribir la ResponseEntity en la respuesta
                response.getWriter().write(responseEntity.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
