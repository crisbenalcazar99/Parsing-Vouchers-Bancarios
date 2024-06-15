package net.security.data.microservicesocr.services;


import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.documentai.v1.*;
import com.google.protobuf.ByteString;

import io.grpc.StatusRuntimeException;
import net.security.data.microservicesocr.Interfaces.InterfaceGoogleServices;

import net.security.data.microservicesocr.Utils.GlobalFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class ConsumoGoogleService implements InterfaceGoogleServices {
    private static final Logger logger = LoggerFactory.getLogger(ConsumoGoogleService.class);

    @Value("${app.projectId}")
    private String projectId;
    @Value("${app.location}")
    private String location;
    @Value("${app.processorId}")
    private String processorId;


    @Override
    public List<Document.Entity> setProcessor(ByteString fileByteString, String fileContentType) throws IOException, InvalidArgumentException, StatusRuntimeException {
        logger.info("image sent to be processed in google cloud");
        long initTime = System.currentTimeMillis(); //Mediciones de tiempo del consumo de google
        List<Document.Entity> documentEntityList = processDocument(projectId, location, processorId, fileByteString, fileContentType);
        long endTime = System.currentTimeMillis(); //Mediciones de tiempo del consumo de google
        logger.info("Data Extraction Process in Google Cloud successfully completed in :{} ms", endTime - initTime);
        return documentEntityList;
    }

    //Se envia a procesador el voucher al servicio de Google
    @Override
    public List<Document.Entity> processDocument(String projectId, String location, String processorId, ByteString fileByteString, String fileContentType)
            throws IOException, InvalidArgumentException, StatusRuntimeException {
        String endpoint = String.format("%s-documentai.googleapis.com:443", location);
        String pathFile = "src/main/resources/service-account-file.json";
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(pathFile))
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
            // Create a DocumentProcessorServiceSettings object and set the credentials property to the ServiceAccountCredentials object.
        DocumentProcessorServiceSettings settings =
                DocumentProcessorServiceSettings.newBuilder()
                        .setEndpoint(endpoint)
                        .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                        .build();
        GlobalFlags.myFlag.set(true);
        try (DocumentProcessorServiceClient client = DocumentProcessorServiceClient.create(settings)) {
            // The full resource name of the processor, e.g.:
            // projects/project-id/locations/location/processor/processor-id
            // You must create new processors in the Cloud Console first
            String name =
                    String.format("projects/%s/locations/%s/processors/%s", projectId, location, processorId);

            // definir el archivo que va a se enviado al procesador de google documentai
            ByteString content = fileByteString;

            RawDocument document =
                    RawDocument.newBuilder().setContent(content).setMimeType(fileContentType).build();

            // Configure the process request.
            ProcessRequest request =
                    ProcessRequest.newBuilder().setName(name).setRawDocument(document)
                            .build();

            // Recognizes text entities in the PDF document
            ProcessResponse result = client.processDocument(request);

            return result.getDocument().getEntitiesList();
        }
    }
}
