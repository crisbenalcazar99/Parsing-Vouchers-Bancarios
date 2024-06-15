package net.security.data.microservicesocr.Interfaces;

import com.google.cloud.documentai.v1.Document;

import java.util.HashMap;
import java.util.List;

public interface InterfaceExtractionValues {
    HashMap<String, String> generateDictEntities(List<Document.Entity> entities);
    HashMap<String, String> extractValues(List<Document.Entity> entities);
    String standardizationNames(String name);
}
