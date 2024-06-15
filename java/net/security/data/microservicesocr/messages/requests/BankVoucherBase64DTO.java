package net.security.data.microservicesocr.messages.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

// information sent in the body of the request at the endpoint "/extract/voucher/url"
public class BankVoucherBase64DTO {
    @NotNull(message = "Error ID")
    //@Pattern(regexp = "[0-9\\-]+", message = "ID must contain only numbers and hyphens")
    private Long idTransaction;

    @NotNull(message = "Error DNI")
    //@Pattern(regexp = "[0-9\\-]+", message = "DNI must contain only numbers and hyphens")
    private Long dni;

    @NotNull
    @Pattern(regexp = "(application/pdf|image/(gif|jpeg|png|bmp|webp))", message = "The file type is invalid")
    private String mimeType;

    @NotNull(message = "The File name must be specified")
    private String fileName;

    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9+/]+={0,2}$", message = "Base 64 string is invalid")
    private String contentBase64;

    @NotNull
    @Pattern(regexp = "SAVE_FILE_SERVER|NO_SAVE", message = "Sent mnemonic is not a valid option")
    private String mnemonicSaveImage;

}
