package net.security.data.microservicesocr.Interfaces;

import net.security.data.microservicesocr.messages.requests.BankVoucherBase64DTO;
import net.security.data.microservicesocr.messages.requests.BankVoucherUrlDTO;
import net.security.data.microservicesocr.messages.requests.ImageData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

public interface InterfaceImageManager {

    ImageData processImageByUrl(BankVoucherUrlDTO bankVoucherUrlDTO) throws IOException;
    ImageData processImageByBase64(BankVoucherBase64DTO bankVoucherBase64DTO) throws IOException;
    ResponseEntity<byte[]> downloadImage(BankVoucherUrlDTO bankVoucherUrlDTO) throws HttpClientErrorException.NotFound;
    ImageData convertResponseToImageData (ResponseEntity<byte[]> response);
    ImageData convertBase64ToImageData(BankVoucherBase64DTO bankVoucherBase64DTO);

}
