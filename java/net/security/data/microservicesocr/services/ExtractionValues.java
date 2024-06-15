package net.security.data.microservicesocr.services;

import com.google.cloud.documentai.v1.Document;
import net.security.data.microservicesocr.Interfaces.InterfaceExtractionValues;
import net.security.data.microservicesocr.enums.BanksNameEnum;
import net.security.data.microservicesocr.enums.FieldsVouchersEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExtractionValues implements InterfaceExtractionValues {

    private static final Logger logger = LoggerFactory.getLogger(OCRServiceImpl.class);

    @Override
    public HashMap<String, String> generateDictEntities(List<Document.Entity> entities){
        final String DEFAULT_DATE = "2024-02-29";
        HashMap<String, String> dictEntities = extractValues(entities);
        // En caso que no se haya reconocido una fecha se agrega una fecha por default
        dictEntities.putIfAbsent(FieldsVouchersEnum.TRANSFER_DATE.getValue(), DEFAULT_DATE);
        dictEntities.putIfAbsent(FieldsVouchersEnum.DESTINATION_BANK_NAME.getValue(), dictEntities.get(FieldsVouchersEnum.ORIGEN_BANK_NAME.getValue()));
        logger.info("Informacion Extraida del Voucher Estandarizada: {}", dictEntities);
        return dictEntities;
    }

    @Override
    public HashMap<String, String> extractValues(List<Document.Entity> entities){
        HashMap<String, String> dictEntities = new HashMap<>();
        String keyHash;
        String valueHash;
        Set<String> LABELS_TO_STANDARD = new HashSet<>();
        LABELS_TO_STANDARD.add(FieldsVouchersEnum.DESTINATION_BANK_NAME.getValue());  // Nombre del banco
        LABELS_TO_STANDARD.add(FieldsVouchersEnum.ORIGEN_BANK_NAME.getValue()); // Nombre del Banco
        LABELS_TO_STANDARD.add(FieldsVouchersEnum.DESTINATION_ACCOUNT_HOLDER_NAME.getValue());  //Nombre de Security Data


        for (Document.Entity entity : entities){
            keyHash = entity.getType();
            if (!entity.getNormalizedValue().getText().isEmpty()) valueHash = entity.getNormalizedValue().getText();
            else valueHash = entity.getMentionText();

            //En caso que el valueHas este vacio significa que existe entidades anidadas, por lo cual se ejecuta
            //una recursividad para extraer los elementos internos y agregarlos al mismo HashMap
            if (valueHash.isEmpty()) dictEntities.putAll(extractValues(entity.getPropertiesList()));
            else if (LABELS_TO_STANDARD.contains(keyHash)){
                valueHash = standardizationNames(valueHash);
                dictEntities.put(keyHash, valueHash);
            }else dictEntities.put(keyHash, valueHash);
        }
        return dictEntities;
    }

    // Proceso para estandarizar los nombres de los bancos y de Security Data
    @Override
    public String standardizationNames(String name){
        Map<String, String> SET_NAME_STANDAR = BanksNameEnum.standardNames();
        for(String key : SET_NAME_STANDAR.keySet()){
            if (name.toLowerCase().contains(SET_NAME_STANDAR.get(key))){
                name = key;
                break;
            }
        }
        return name;
    }
}
