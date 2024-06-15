package net.security.data.microservicesocr.services;

import com.google.api.gax.rpc.FailedPreconditionException;
import com.google.cloud.documentai.v1.Document;

import io.grpc.StatusRuntimeException;
import jakarta.persistence.EntityNotFoundException;
import net.security.data.microservicesocr.Interfaces.InterfaceOCRService;
import net.security.data.microservicesocr.enums.MnemonicsEnum;
import net.security.data.microservicesocr.messages.requests.*;
import net.security.data.microservicesocr.models.DTOS.AuditoryDTO;
import net.security.data.microservicesocr.models.DTOS.ConsumosDTO;
import net.security.data.microservicesocr.models.DTOS.UserDTO;
import net.security.data.microservicesocr.models.Mappler;
import net.security.data.microservicesocr.models.entities.CatalogEntity;
import net.security.data.microservicesocr.models.entities.UserEntity;
import net.security.data.microservicesocr.models.entities.VouchersEntity;
import net.security.data.microservicesocr.repository.*;
import net.security.data.microservicesocr.messages.responses.JsonResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class OCRServiceImpl implements InterfaceOCRService {
    @Autowired
    private ConsumoGoogleService consumoGoogleService;
    @Autowired
    private VouchersRepository vouchersRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ImageManager imageManager;
    @Autowired
    private ExtractionValues extractionValues;
    @Autowired
    private CatalogRepository catalogRepository;
    @Autowired
    private AuditoryDTO auditoryDTO;
    @Autowired
    private ConsumosDTO consumosDTO;
    @Autowired
    private BankStatementsService bankStatementsService;


    private static final Logger logger = LoggerFactory.getLogger(OCRServiceImpl.class);

    // Ingresa para extraer la imagen proveniente del URL conjunatemente con su informacion
    @Override
    public JsonResponse extractDataVoucherByUrl(BankVoucherUrlDTO bankVoucherUrlDTO)
            throws IOException, InterruptedException, ExecutionException, TimeoutException, DataIntegrityViolationException, FailedPreconditionException, StatusRuntimeException {
        ImageData imageData = imageManager.processImageByUrl(bankVoucherUrlDTO);
        return processVoucherData(imageData, bankVoucherUrlDTO.getIdTransaction(), bankVoucherUrlDTO.getMnemonicSaveImage());
    }


    // Imgresa para extraer la imagen proveniente del Base64 conjunatemente con su informacion
    @Override
    public JsonResponse extractDataVoucherByBase64(BankVoucherBase64DTO bankVoucherBase64DTO)
            throws IOException, InterruptedException, ExecutionException, TimeoutException, DataIntegrityViolationException, FailedPreconditionException, StatusRuntimeException, IllegalArgumentException {
        ImageData imageData = imageManager.processImageByBase64(bankVoucherBase64DTO);
        return processVoucherData(imageData, bankVoucherBase64DTO.getIdTransaction(), bankVoucherBase64DTO.getMnemonicSaveImage());
    }


    //Logica de extraccion de la informacion mediante Google de los vouchers y persistirlos en la base de datos dicha informacion
    @Override
    public JsonResponse processVoucherData(ImageData imageData, Long idTransaction, String typeSave)
            throws IOException, InterruptedException, ExecutionException, TimeoutException, DataIntegrityViolationException, FailedPreconditionException, StatusRuntimeException {
                CatalogEntity catalogTypeSave = catalogRepository.findCatalogEntityByMnemonic(typeSave)
                .orElseThrow(() -> new EntityNotFoundException(MnemonicsEnum.CATALOG_ENTITY_NOT_FOUND.getValue()));
        List<Document.Entity> entitiesVoucher = consumoGoogleService.setProcessor(imageData.getFileByteString(), imageData.getMimeType());
        HashMap<String, String> dictEntities = extractionValues.generateDictEntities(entitiesVoucher);
        VouchersEntity voucherDataObj = new VouchersEntity(idTransaction, true,dictEntities, imageData.getUrlPath(), catalogTypeSave);
        vouchersRepository.saveAndFlush(voucherDataObj);  // Se persiste el objeto voucherDataObj
        consumosDTO.setVouchersEntity(voucherDataObj);
        logger.info("Voucher structure information successfully stored in voucher_data table");
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.SUCCESSFULLY_ENTITY_EXTRACTION.getValue())
                .orElseThrow(() -> new EntityNotFoundException(MnemonicsEnum.CATALOG_ENTITY_NOT_FOUND.getValue()));
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(MnemonicsEnum.VOUCHER_DATA_EXTRACTION.getValue());
        return new JsonResponse(true, HttpStatus.CREATED, "Successfully Process Image", voucherDataObj);
    }


    @Override
    public JsonResponse getDataVoucherById(Long idTransaction){
        List<VouchersEntity> vouchersEntities = vouchersRepository.findVouchersEntitiesByIdTransactionAndStateRegisterTrue(idTransaction);
        if(vouchersEntities.isEmpty())
            throw new EntityNotFoundException("No VouchersEntity found por Transaction: " + idTransaction);
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.SUCCESSFULLY_PROCESS.getValue())
                .orElseThrow(() -> new EntityNotFoundException(MnemonicsEnum.CATALOG_ENTITY_NOT_FOUND.getValue()));
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(MnemonicsEnum.VOUCHER_DATA_RETURN.getValue());
        logger.info("Information returned successfully");
        return new JsonResponse(true, HttpStatus.OK, "Successfully Process", vouchersEntities);
    }


    //Para la creacion de usuarios, se deberia eliminar una ve finalziado el proyecto
    @Override
    public JsonResponse userCreated(AuthLoginRequest authLoginRequest){
        UserEntity userEntity = UserEntity.builder()
                .username(authLoginRequest.getUsername())
                .password(passwordEncoder.encode(authLoginRequest.getPassword()))
                .userStatus(catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.USER_IS_ACTIVATED.getValue())
                        .orElseThrow(() -> new EntityNotFoundException(MnemonicsEnum.CATALOG_ENTITY_NOT_FOUND.getValue())))
                .build();
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.USER_CREATED_SUCCESSFULLY.getValue())
                .orElseThrow(() -> new EntityNotFoundException(MnemonicsEnum.CATALOG_ENTITY_NOT_FOUND.getValue()));
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        auditoryDTO.setRequest(MnemonicsEnum.CUSTOMER_LOGIN_INFORMATION.getValue() + ": " + authLoginRequest.getUsername());
        auditoryDTO.setUser(null);
        userRepository.saveAndFlush(userEntity);
        return new JsonResponse(true, HttpStatus.CREATED, "User Created: ", userEntity);
    }

    //Metodo para eliminar un usuario
    public JsonResponse deleteUser(DeleteUserRequest deleteUserRequest) throws  EntityNotFoundException{
        Optional<UserEntity> userEntityOptional = userRepository.findByUsernameAndStatusTrue(deleteUserRequest.getUsername());
        if (userEntityOptional.isEmpty()){
            userEntityOptional = userRepository.findByUsername(deleteUserRequest.getUsername());
            if (userEntityOptional.isEmpty())
                throw new UsernameNotFoundException("The username was not found: " + deleteUserRequest.getUsername());
        }

        UserEntity  userEntity = userEntityOptional.orElseThrow(() -> new UsernameNotFoundException("Not Found User: " + deleteUserRequest.getUsername()));
        CatalogEntity catalogEntityUserStatus = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.USER_IS_DISABLED.getValue())
                .orElseThrow(() -> new EntityNotFoundException(MnemonicsEnum.CATALOG_ENTITY_NOT_FOUND.getValue()));
        userEntity.setStatus(false);
        userEntity.setUserStatus(catalogEntityUserStatus);
        userRepository.save(userEntity);
        CatalogEntity catalogEntityAuditory = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.DELETE_USER.getValue())
                .orElseThrow(() -> new EntityNotFoundException(MnemonicsEnum.CATALOG_ENTITY_NOT_FOUND.getValue()));
        auditoryDTO.setCatalogEntity(catalogEntityAuditory);
        auditoryDTO.setResponse(catalogEntityAuditory.getDescription());
        return new JsonResponse(true, HttpStatus.OK, "Delete User: "+ deleteUserRequest.getUsername());
    }


    //MANEJAR LA EXCEPCION DE JSONPROCESSIN EXPEDICION
    public JsonResponse verifiedAcreditation(Long idTransaction) throws EntityNotFoundException, JsonProcessingException, HttpClientErrorException.NotFound{
        List<VouchersEntity> vouchersEntityList = bankStatementsService.verifyTransfer(idTransaction);
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.SUCCESSFULLY_PROCESS.getValue())
                .orElseThrow(() -> new EntityNotFoundException(MnemonicsEnum.CATALOG_ENTITY_NOT_FOUND.getValue()));
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(MnemonicsEnum.TRANSFER_VERIFICATION.getValue());
        return new JsonResponse(true, HttpStatus.OK, "Correcto", vouchersEntityList);
    }

    //MANEJAR LA EXCEPCION DE JSONPROCESSIN EXPEOCION
    public JsonResponse verifiedAcreditation(List<Long> idTransaction) throws EntityNotFoundException, JsonProcessingException, HttpClientErrorException.NotFound{
        List<VouchersEntity> vouchersEntityList = bankStatementsService.verifyTransfer(idTransaction);
        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.SUCCESSFULLY_PROCESS.getValue())
                .orElseThrow(() -> new EntityNotFoundException(MnemonicsEnum.CATALOG_ENTITY_NOT_FOUND.getValue()));
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(MnemonicsEnum.TRANSFER_VERIFICATION.getValue());
        return new JsonResponse(true, HttpStatus.OK, "Correcto", vouchersEntityList);
    }



}
