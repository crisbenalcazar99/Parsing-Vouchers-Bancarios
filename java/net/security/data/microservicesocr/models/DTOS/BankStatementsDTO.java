package net.security.data.microservicesocr.models.DTOS;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankStatementsDTO {
    private Long idBankStatement;
    private Long idVoucher;
    private Long idTransaction;
    private String banco;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Timestamp transferDate;
    private String reference;
    private Double amount;
    private Boolean isVerify;
}
