package net.security.data.microservicesocr.services;

import com.google.protobuf.ByteString;

import net.security.data.microservicesocr.Interfaces.InterfaceImageManager;
import net.security.data.microservicesocr.messages.requests.ImageData;
import net.security.data.microservicesocr.enums.MnemonicsEnum;
import net.security.data.microservicesocr.repository.ImageSaver;
import org.springframework.beans.factory.annotation.Autowired;
import net.security.data.microservicesocr.messages.requests.BankVoucherBase64DTO;
import net.security.data.microservicesocr.messages.requests.BankVoucherUrlDTO;
import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ImageManager implements InterfaceImageManager {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ImageSaver imageSaver;

    private static final Logger logger = LoggerFactory.getLogger(ImageManager.class);

    @Override
    public ImageData processImageByUrl(BankVoucherUrlDTO bankVoucherUrlDTO) throws IOException {
        // Se realiza la solicitud get al URl y se procesa la respuesta recibida.
        ResponseEntity<byte[]> response = downloadImage(bankVoucherUrlDTO);
        logger.info("Successfully retrieved response from the URL.");
        ImageData imageData = convertResponseToImageData(response);

        String imageUrlPath = null;

        // Almacena la imagen en caso que el mnemonic lo indique
        if (bankVoucherUrlDTO.getMnemonicSaveImage().equals(MnemonicsEnum.SAVE_FILE_SERVER.getValue())) {
            imageUrlPath = imageSaver.saveImage(bankVoucherUrlDTO.getDni(), bankVoucherUrlDTO.getIdTransaction(),
                    imageData);
        }
        imageData.setUrlPath(imageUrlPath);

        return imageData;
    }

    @Override
    public ImageData processImageByBase64(BankVoucherBase64DTO bankVoucherBase64DTO)
            throws IOException, IllegalArgumentException {
        ImageData imageData = convertBase64ToImageData(bankVoucherBase64DTO);
        String imageUrlPath = null;
        if (bankVoucherBase64DTO.getMnemonicSaveImage().equals(MnemonicsEnum.SAVE_FILE_SERVER.getValue())) {
            imageUrlPath = imageSaver.saveImage(bankVoucherBase64DTO.getDni(), bankVoucherBase64DTO.getIdTransaction(),
                    imageData);
        }
        imageData.setUrlPath(imageUrlPath);
        return imageData;
    }

    @Override
    public ResponseEntity<byte[]> downloadImage(BankVoucherUrlDTO bankVoucherUrlDTO)
            throws HttpClientErrorException.NotFound {
        String urlImage = bankVoucherUrlDTO.getUrlImage();

        // Configurar las cabeceras de la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG, MediaType.APPLICATION_PDF));

        // Configurar la solicitud con las cabeceras
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Realizar la solicitud GET a la URL de la imagen y en caso de existir un error
        // levantara un excepcion
        return restTemplate.exchange(urlImage, HttpMethod.GET, entity, byte[].class);
    }

    @Override
    public ImageData convertResponseToImageData(ResponseEntity<byte[]> response) {
        // Extraer el ByteString correspondiente a la imagen
        byte[] fileByteArray = Objects.requireNonNull(response.getBody());
        ByteString fileByteString = ByteString.copyFrom(fileByteArray);

        // Extraer la extension y el content type del archivo descargado
        MediaType fileContentType = Objects.requireNonNull(response.getHeaders().getContentType());
        String fileExtension = fileContentType.getSubtype();
        String mimeType = fileContentType.toString();

        // Contruir el objeto Image Data a ser retornado
        ImageData imageData = ImageData.builder()
                .fileName("NameFile")
                .fileByteArray(fileByteArray)
                .fileByteString(fileByteString)
                .fileExtension(fileExtension)
                .mimeType(mimeType)
                .build();
        return imageData;
    }

    @Override
    public ImageData convertBase64ToImageData(BankVoucherBase64DTO bankVoucherBase64DTO)
            throws IllegalArgumentException {
        // Se decodifica la imagen recibida por el body y se transforma en ByteString

        byte[] fileByteArray = Base64.getDecoder().decode(bankVoucherBase64DTO.getContentBase64());
        ByteString fileByteString = ByteString.copyFrom(fileByteArray);

        // Se procesa la informacion sobre la img
        String mimeType = bankVoucherBase64DTO.getMimeType();
        String fileExtension = mimeType.split("/")[1];
        String fileName = bankVoucherBase64DTO.getFileName();
        // Contruir el objeto Image Data a ser retornado
        ImageData imageData = ImageData.builder()
                .fileName("NameFile")
                .fileByteArray(fileByteArray)
                .fileByteString(fileByteString)
                .fileExtension(fileExtension)
                .mimeType(mimeType)
                .build();
        logger.info("Image successfully retrieved from a base64-encoded file.");
        return imageData;

    }

}
