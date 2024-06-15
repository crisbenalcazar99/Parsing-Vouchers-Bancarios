package net.security.data.microservicesocr.messages.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Modelo Request para logging y que me devuelva el Token
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginRequest{
    @NotNull
    private String username;
    @NotNull
    private String password;
}
