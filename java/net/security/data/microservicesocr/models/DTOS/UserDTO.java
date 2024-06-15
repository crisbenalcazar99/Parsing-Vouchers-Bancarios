package net.security.data.microservicesocr.models.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.security.data.microservicesocr.models.entities.CatalogEntity;
import net.security.data.microservicesocr.models.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private CatalogEntity userStatus;
    private Date recordDate;
    private Date updateDate;
    private UserEntity userEntity;
    private Boolean status;
}
