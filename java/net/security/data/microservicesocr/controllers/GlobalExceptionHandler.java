package net.security.data.microservicesocr.controllers;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.gax.rpc.FailedPreconditionException;
import com.google.api.gax.rpc.InvalidArgumentException;
import io.grpc.StatusRuntimeException;
import jakarta.persistence.EntityNotFoundException;

import net.security.data.microservicesocr.enums.MnemonicsEnum;
import net.security.data.microservicesocr.messages.responses.JsonResponse;
import net.security.data.microservicesocr.models.DTOS.AuditoryDTO;
import net.security.data.microservicesocr.models.entities.CatalogEntity;
import net.security.data.microservicesocr.repository.CatalogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.HttpClientErrorException;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private AuditoryDTO auditoryDTO;

    private static final Logger logger = LoggerFactory.getLogger(OCRController.class);

    //Maneja excepciones en los cuales una base de datos devuelve 2 o mas registros y el se espera un unico registro
    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<JsonResponse> handleIncorrectResultSizeDataAccessException(IncorrectResultSizeDataAccessException ex){
        logger.info("Error en la excepción de acceso a datos por tamaño incorrecto del resultado: {}", ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.DATA_INTEGRITY_VIOLATION.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.UNPROCESSABLE_ENTITY, "Se esperaba un resultado, pero se encontró cero o más de uno: " + ex.getMessage());
        return new ResponseEntity<>(jsonResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({ExecutionException.class, InterruptedException.class, TimeoutException.class})
    public ResponseEntity<JsonResponse> handleGoogleServices(Exception ex){
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.SERVICE_UNAVAILABLE,"Error en Google Services:",null);
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.INVALID_PROCESSOR.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        logger.error("Error ocurrido en los servicios de Google Cloud. : {}", ex.getMessage());
        return new ResponseEntity<>(jsonResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    //Excepcion que maneja posibles violaaciones a la propiedades de las bases de datos
    //Posibles ingresos duplicados en campos unicos o ingresos nulos en campos que no aceptan estos parametros
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<JsonResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex){
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.CONFLICT,"Violacion a  las restricciones de integridad o entradas duplicadas: " + ex.getMessage(),null);
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.DATA_INTEGRITY_VIOLATION.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        logger.error("Violacion a  las restricciones de integridad o entradas duplicadas: {}", ex.getMessage());
        return new ResponseEntity<>(jsonResponse, HttpStatus.CONFLICT);
    }

    //
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException(IOException ex) {
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.CONFLICT, "IOException Arrojado: " + ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.STATUS_RUNTIME_EXCEPTION.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        logger.error("IOException Arrojado: {}", ex.getMessage());
        auditoryDTO.setResponse(catalogEntity.getDescription());
        return new ResponseEntity<>(jsonResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    //Maneja la excepcion producto de loggearse con un usuario no registrado en la base de datos
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.UNAUTHORIZED, "Excepcion arrojado usuario no encontrado: " + ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.USERNAME_NOT_FOUND.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        auditoryDTO.setRequest(catalogEntity.getDescription() +", " + ex.getMessage().split(":")[2]);
        logger.error("Excepcion arrojado usuario no encontrado: {}", ex.getMessage());
        return new ResponseEntity<>(jsonResponse, HttpStatus.UNAUTHORIZED);
    }

    //Manejo de excepciones dentro de la tabla de usuarios
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Object> handleSQLException(SQLException ex){
        JsonResponse jsonResponse;
        if(ex.getSQLState().startsWith("23505")){
            jsonResponse = new JsonResponse(false, HttpStatus.CONFLICT, "Violacion de la restriccion de unicidad del username" + ex.getMessage());
            logger.error("Violacion de la restriccion de unicidad del username {}", ex.getSQLState());
        }else{
            jsonResponse = new JsonResponse(false, HttpStatus.CONFLICT, "Error Base de Datos" + ex.getMessage());
            logger.error("Error Base de Datos");
        }
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.USERNAME_NOT_FOUND.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(MnemonicsEnum.SQL_EXCEPTION.getValue());
        return new ResponseEntity<>(jsonResponse, HttpStatus.CONFLICT);
    }


    // Maneja la excepcion producto de no encontrar un registro en un base de datos que no sea la de usuarios
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.NOT_FOUND, "Entidad No Encontrada: " + ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.ENTITY_NOT_FOUND.getValue())
                .orElse(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        logger.error("Entidad No Encontrada: {}", ex.getMessage());
        return new ResponseEntity<>(jsonResponse, HttpStatus.NOT_FOUND);
    }

    // Manejo de errores relacionado con errores de condiciones previa de google cloud
    @ExceptionHandler(FailedPreconditionException.class)
    public ResponseEntity<Object> handleFailedPreconditionException(FailedPreconditionException ex){
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.SERVICE_UNAVAILABLE, "Fallo de Precondicion: "+ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.INVALID_PROCESSOR.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        logger.error("Fallo de Precondiciones de Google Services: {}", ex.getMessage());
        return new ResponseEntity<>(jsonResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Manejo de errores relacionados con el proceso de descarga de la imagen desde la URL. URL incorrecto o imagen no encontrada.
    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<Object> handleHttpClientErrorExceptionNotFound(HttpClientErrorException.NotFound ex){
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.NOT_FOUND, "Recurso no encontrado en la URL");
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.VOUCHER_SOURCE_ERROR.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        logger.error("Recurso no encontrado en la URL ");
        return new ResponseEntity<>(jsonResponse, HttpStatus.NOT_FOUND);
    }

    // Manejo de excepciones relacionados con errores en los atributos ingresados en la request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){

        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.INCORRECT_FIELDS_IN_REQUEST.getValue())
                .orElseThrow(null);
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.BAD_REQUEST, "Falló la validación de los parámetros del cuerpo. " + catalogEntity.getDescription());
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        logger.error("Falló la validación de los parámetros del cuerpo.: {}", catalogEntity.getDescription());
        return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
    }

    // Manejo de errores relacionados con problemas en los voucher
    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<Object> handlerInvalidArgumentException(InvalidArgumentException ex){
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.UNPROCESSABLE_ENTITY, "Contenido invalido en la Imagen : " + ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.VOUCHER_SOURCE_ERROR.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        logger.error("Contenido invalido en la Imagen:  {}", ex.getMessage());
        return new ResponseEntity<>(jsonResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // Manejo de excepciones Status Runtime Exception en el servicio de Google. Request had invalid authentication credentials.
    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<Object> handlerStatusRuntimeException(StatusRuntimeException ex){
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.UNAUTHORIZED, "Excepcion en estado de ejecucion: " + ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.STATUS_RUNTIME_EXCEPTION.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        logger.error("Excepcion en estado de ejecucion: {}", ex.getMessage());
        return new ResponseEntity<>(jsonResponse, HttpStatus.UNAUTHORIZED);
    }

    //Maneja la excepcion producto de ingresar un usuario desactivado
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<JsonResponse> handleDisabledException(DisabledException ex){
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.UNAUTHORIZED, " Cuenta del usuario esta deshabilitada. Contactarse con Soporte. " + ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.USER_IS_DISABLED.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setRequest(catalogEntity.getDescription() + ": " + ex.getMessage().split(":")[1]);
        logger.error("Cuenta del usuario esta deshabilitada. Contactarse con Soporte: {}", ex.getMessage());
        return new ResponseEntity<>(jsonResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<JsonResponse> handleJsonProcessingException(JsonProcessingException ex){
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.UNPROCESSABLE_ENTITY, "Error Procesando archivo Json" + ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.USER_IS_DISABLED.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        logger.error("Error Procesando archivo Json: {}", ex.getMessage());
        return new ResponseEntity<>(jsonResponse, HttpStatus.UNAUTHORIZED);
    }

    // Error en eL parsing de JsonParseException Relaiconado con un error en el envio del JSON
    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<JsonResponse> handleJsonParseException(JsonParseException ex){
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.BAD_REQUEST, "Error al analizar el archivo Json: " + ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.USER_IS_DISABLED.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        logger.error("Error al analizar el archivo Json:: {}", ex.getMessage());
        return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);

    }

    // Escepcion producto de una contrasena errora
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<JsonResponse> handleBadCredentialsException(BadCredentialsException ex){
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.UNAUTHORIZED, "Error de Autenticacion: " + ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.WRONG_PASSWORD.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setRequest(catalogEntity.getDescription() +": " +ex.getMessage().split(":")[1]);
        logger.error("Error de Autenticacion: Contrasena Invalida");
        return new ResponseEntity<>(jsonResponse, HttpStatus.UNAUTHORIZED);
    }

    //  se produce cuando intentas decodificar una cadena Base64 que no es válida o está mal formada.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<JsonResponse> handleIllegalArgumentException(IllegalArgumentException ex){
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.BAD_REQUEST, "Error al decodificar una cadena Base64 que es inválida o está malformada: " + ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.INCORRECT_FIELDS_IN_REQUEST.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        logger.error("Error al decodificar una cadena Base64 que es inválida o está malformada:, {}", ex.getMessage());
        return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);

    }

    // Manejar la excepción de falta de permisos para crear el directorio
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<JsonResponse> SecurityException (SecurityException  ex){
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.CONFLICT, "No tienes permisos para crear el directorio." + ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.VOUCHER_SOURCE_ERROR.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        logger.error("No tienes permisos para crear el directorio.");
        return new ResponseEntity<>(jsonResponse, HttpStatus.CONFLICT);
    }

    // Manejar la excepción de El sistema no puede encontrar la ruta especificada
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<JsonResponse> FileNotFoundException (FileNotFoundException  ex){
        JsonResponse jsonResponse = new JsonResponse(false, HttpStatus.NOT_FOUND, "Directorio/Archivo no encontrado" + ex.getMessage());
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.NOT_FOUND_PATH_SERVER.getValue())
                .orElseThrow(null);
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        logger.error("La direccion de guardado de archivos en el server no fue encontrada");
        return new ResponseEntity<>(jsonResponse, HttpStatus.NOT_FOUND);
    }



}


