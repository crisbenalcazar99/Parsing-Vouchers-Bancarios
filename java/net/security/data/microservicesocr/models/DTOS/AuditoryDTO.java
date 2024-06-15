package net.security.data.microservicesocr.models.DTOS;

import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.security.data.microservicesocr.models.entities.CatalogEntity;
import net.security.data.microservicesocr.models.entities.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoryDTO {

    private static final Logger log = LoggerFactory.getLogger(AuditoryDTO.class);
    private boolean stateRegister;
    private String endPoint;
    private String method;
    private CatalogEntity catalogEntity;
    private Integer statusCode;
    private String response;
    private String request;
    private String direccionIP;
    private UserEntity user;

    @PreDestroy
    public void cleanup() {
        // Realiza tareas de limpieza aquí
        response = null;
        request = null;
        catalogEntity = null;
        //log.info("Destruccion del Bean Auditory DTO");
    }

}
