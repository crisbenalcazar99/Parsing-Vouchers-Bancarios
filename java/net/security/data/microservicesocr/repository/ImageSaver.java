package net.security.data.microservicesocr.repository;

import net.security.data.microservicesocr.messages.requests.ImageData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;

import lombok.Cleanup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Repository
public class ImageSaver {

    @Value("${app.path}")
    private String pathSaveVoucher;

    private static final Logger log = LoggerFactory.getLogger(ImageSaver.class);

    public String saveImage(Long DNI, Long idTransaction, ImageData imageData) throws SecurityException, IOException {

        // Definir nombre de las carpetas a ser utilizadas
        String nameFileToSave = idTransaction + imageData.getDotExtension();
        // Creo las carpetas y subcarpetas requeridas
        Path pathFolderIdTransaction = Paths.get(pathSaveVoucher).resolve(DNI.toString())
                .resolve(idTransaction.toString());

        File fileFolderIdTransaction = pathFolderIdTransaction.toFile();
        File fileImageToSave = pathFolderIdTransaction.resolve(nameFileToSave).toFile();

        try {
            if (!fileFolderIdTransaction.exists()) {
                fileFolderIdTransaction.mkdirs();
            }
        } catch (SecurityException ex) {
            // Manejar la excepción de falta de permisos para crear el directorio
            throw new SecurityException("You do not have permissions to create the directory");
        }

        // Definir el nombre base del archivo y la extensión
        String nameWithoutExtension = idTransaction.toString();
        String extension = "." + imageData.getFileExtension();

        // Inicializar un contador
        int counter = 1;

        // Mientras el archivo ya exista, incrementar el contador y probar con un nuevo
        // nombre de archivo
        while (fileImageToSave.exists()) {
            fileImageToSave = pathFolderIdTransaction.resolve(nameWithoutExtension + "-" + counter + extension)
                    .toFile();
            counter++;
        }

        try {
            @Cleanup
            FileOutputStream outputStream = new FileOutputStream(fileImageToSave);
            FileCopyUtils.copy(imageData.getFileByteArray(), outputStream);
            log.info("Se guarda en archivo en el server path: {}", fileImageToSave);
        } catch (FileNotFoundException ex) {
            // Manejar la excepción de archivo no encontrado al abrir el archivo para
            // escritura
            throw new FileNotFoundException("Excepcion arrojada en ImageSaver. " + ex.getMessage());
        } catch (IOException ex) {
            throw new IOException("Error when saving the document in ImageSaver" + ex.getMessage());
        }

        String url = fileImageToSave.toString().replace("\\", "/");

        if (!fileImageToSave.exists()) {
            throw new IOException("Imagen no guardada " + url);
        }
        return url;
    }

}
