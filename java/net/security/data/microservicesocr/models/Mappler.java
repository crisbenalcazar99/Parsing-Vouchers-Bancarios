package net.security.data.microservicesocr.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.security.data.microservicesocr.models.DTOS.BankStatementsDTO;
import net.security.data.microservicesocr.models.DTOS.UserDTO;
import net.security.data.microservicesocr.models.DTOS.VouchersDTO;
import net.security.data.microservicesocr.models.entities.AuditoryEntity;
import net.security.data.microservicesocr.models.entities.ConsumosEntity;
import net.security.data.microservicesocr.models.entities.UserEntity;
import net.security.data.microservicesocr.models.entities.VouchersEntity;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Mappler {

    public static VouchersDTO convertVoucherEntityToDto(VouchersEntity vouchersEntity){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(vouchersEntity, VouchersDTO.class);
    }

    public static List<VouchersDTO> convertVoucherEntityToDto(List<VouchersEntity> vouchersEntityList) {
        return vouchersEntityList.stream()
                .map(Mappler::convertVoucherEntityToDto)
                .collect(Collectors.toList());
    }

    public static UserDTO convertUserEntityToDto(UserEntity userEntity){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userEntity, UserDTO.class);
    }

    public static UserEntity convertUserDtoToEntity(UserDTO userDTO){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userDTO, UserEntity.class);
    }

    public static ConsumosEntity convertAuditorytoConsumos(AuditoryEntity auditoryEntity){
        ModelMapper modelMapper = new ModelMapper();
        ConsumosEntity consumosEntity = modelMapper.map(auditoryEntity, ConsumosEntity.class);
        return consumosEntity;
    }

    public static BankStatementsDTO convertJsonToBankStatementsDto(String json){
        ObjectMapper objectMapper = new ObjectMapper();
         return objectMapper.convertValue(json, BankStatementsDTO.class);
    }
    public static List<BankStatementsDTO> convertJsonToBankStatementsDtoList(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, new TypeReference<List<BankStatementsDTO>>() {});
    }

    public static String prettyJson(String string)throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(string);
        if (jsonNode.has("contentBase64")){
            ObjectNode objectNode = (ObjectNode) jsonNode;
            objectNode.put("contentBase64", "Image in base 64");
            jsonNode = objectNode;
        }
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
    }

    public static String jsonToTextPlain(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);
        if (jsonNode.has("contentBase64")){
            ObjectNode objectNode = (ObjectNode) jsonNode;
            objectNode.put("contentBase64", "Image in base 64");
            jsonNode = objectNode;
        }
        Map<String, Object> jsonMap = objectMapper.readValue(objectMapper.writeValueAsString(jsonNode), Map.class);
        return objectMapper.writeValueAsString(jsonMap);
    }


}
