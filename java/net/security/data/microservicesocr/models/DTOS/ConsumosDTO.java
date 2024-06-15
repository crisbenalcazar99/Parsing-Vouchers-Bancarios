package net.security.data.microservicesocr.models.DTOS;

import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.security.data.microservicesocr.models.entities.VouchersEntity;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor

// DTO del modelo de un AuditoryEntity para el modelo de consumo

public class ConsumosDTO {
    private String username;
    private String endPoint;
    private String statusCode;
    private VouchersEntity vouchersEntity;

    @PreDestroy
    public void cleanup() {
        // Realiza tareas de limpieza aquí
        username = null;
        endPoint = null;
        statusCode = null;
        vouchersEntity = null;
        //log.info("Destruccion del Bean Auditory DTO");
    }
}
