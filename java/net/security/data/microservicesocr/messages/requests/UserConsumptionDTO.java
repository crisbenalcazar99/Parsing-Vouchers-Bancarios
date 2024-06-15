package net.security.data.microservicesocr.messages.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//De momento queda en Standby para preguntar si nos sirve de algo
public class UserConsumptionDTO {
    @NotNull
    private List<String> usernames;
}
