package net.security.data.microservicesocr.Interfaces;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.cloud.documentai.v1.Document;
import com.google.protobuf.ByteString;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface InterfaceGoogleServices {
    List<Document.Entity> setProcessor(ByteString fileByteString, String fileContentType) throws IOException, InterruptedException, ExecutionException, TimeoutException;

    List<Document.Entity> processDocument(String projectId, String location, String processorId, ByteString byteStringVoucher, String fileContentType)
            throws IOException, InterruptedException, ExecutionException, TimeoutException;
}
