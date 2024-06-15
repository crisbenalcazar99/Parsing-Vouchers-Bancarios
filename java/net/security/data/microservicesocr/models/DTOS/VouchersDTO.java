package net.security.data.microservicesocr.models.DTOS;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa la informacion de un comprobante bancario")

//Data vouchers DTO
public class VouchersDTO {
    private Long idVoucher;
    private Long transferReference;
    private Timestamp transferDate;

    private Double transferredValue;
    private Double debitedValue;
    private Double comissionValue;

    private String destinationBankName;
    private String destinationBankAccount;
    private String destinationAccountHolderName;

    private String origenBankName;
    private String origenBankAccount;
    private String origenAccountHolderName;

}
