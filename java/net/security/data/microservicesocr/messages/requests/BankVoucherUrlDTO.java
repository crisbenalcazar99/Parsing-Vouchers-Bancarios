package net.security.data.microservicesocr.messages.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor

// information sent in the body of the request at the endpoint "/extract/voucher/base64"
public class BankVoucherUrlDTO {

    @NotNull(message = "Error ID")
    //@Pattern(regexp = "[0-9\\-]+", message = "ID must contain only numbers and hyphens")
    private Long idTransaction;

    @NotNull(message = "Error DNI")
    //@Pattern(regexp = "[0-9\\-]+", message = "DNI must contain only numbers and hyphens")
    private Long dni;

    @NotNull
    @URL(message = "Image URL is invalid")
    private String urlImage;

    @NotNull
    @Pattern(regexp = "SAVE_FILE_SERVER|NO_SAVE", message = "Sent mnemonic is not a valid option")
    private String mnemonicSaveImage;
    {}


}
