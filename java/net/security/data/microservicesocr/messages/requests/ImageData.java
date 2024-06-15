package net.security.data.microservicesocr.messages.requests;

import com.google.protobuf.ByteString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

// Data model of a processed image
public class ImageData {
    private String fileName;
    private ByteString fileByteString;
    private byte[] fileByteArray;
    private String mimeType;
    private String fileExtension;
    private String urlPath;
    public String getDotExtension(){
        return "."+fileExtension;
    }
}
