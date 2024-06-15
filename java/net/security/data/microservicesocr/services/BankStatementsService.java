package net.security.data.microservicesocr.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import net.security.data.microservicesocr.models.DTOS.BankStatementsDTO;
import net.security.data.microservicesocr.models.DTOS.VouchersDTO;
import net.security.data.microservicesocr.models.Mappler;
import net.security.data.microservicesocr.models.entities.VouchersEntity;
import net.security.data.microservicesocr.repository.VouchersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BankStatementsService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private BankStatementsDTO bankStatementsDTO;
    @Autowired
    private VouchersRepository vouchersRepository;
    @Value("${app.url.microservice.state}")
    private String urlBankStatement;

    private static final Logger logger = LoggerFactory.getLogger(BankStatementsService.class);

    // Utilizamos estas divisiones en los metodos para poder overload
    // Este metodo se basa en acceder a todos los registros de voucherData que tenga el mismo idTransaction. Dichos
    // registros son enviado al microservicio de estados bancarios, el mismo que debe devolver los registros correspondientes
    // a los estados bancarios con el idVoucher y en caso de existir un registro devolver un elemento con los
    // elementos en nulo y el atrbuto isVerify como falso. En caso que no encuntre un objeto dentro de bankStatementsDTOList
    // que corresponda al idVoucher lanzara una excepcion al considerar que hay elementos faltantes.


    // Se encarga de validar si existe un registro de una transaccion aprobada para valores indivuales de idTransaction
    public List<VouchersEntity> verifyTransfer(Long idTransaction) throws JsonProcessingException, HttpClientErrorException.NotFound {
        List<VouchersEntity> vouchersEntities = vouchersRepository.findVouchersEntitiesByIdTransactionAndStateRegisterTrue(idTransaction);
        // Verifica que exista un registro de un comprobante bancario con dicho idTransaction
        if (vouchersEntities.isEmpty())
            throw new EntityNotFoundException(String.format("No voucher associated with the idTransaccion %s has been found", idTransaction));
        List<VouchersDTO> vouchersDTOList = Mappler.convertVoucherEntityToDto(vouchersEntities);
        return processBankStatementResponse(vouchersEntities);
    }

    // Se encarga de validar si existe un registro de una transaccion aprobada para una listas  de idTransactions
    public List<VouchersEntity> verifyTransfer(List<Long> idTransactionList) throws JsonProcessingException, HttpClientErrorException.NotFound {
        List<VouchersEntity> vouchersEntities = idTransactionList.stream()
                .flatMap(idTransaction -> vouchersRepository.findVouchersEntitiesByIdTransactionAndStateRegisterTrue(idTransaction).stream())
                .toList();
        // Verifica que exista un registro de un comprobante bancario con dicho idTransaction
        if (vouchersEntities.isEmpty())
            throw new EntityNotFoundException(String.format("No voucher associated with the idTransaccion  has been found"));
        List<VouchersDTO> vouchersDTOList = Mappler.convertVoucherEntityToDto(vouchersEntities);
        return processBankStatementResponse(vouchersEntities);
    }



    public List<VouchersEntity> processBankStatementResponse(List<VouchersEntity> vouchersEntities) throws JsonProcessingException, HttpClientErrorException.NotFound {

        // Consumir el microservicio de los estados bancarios
        //ResponseEntity<String> response = generateHttpRequest(vouchersDTOList);

        //****************** TESTING  ***************************
        String json = "[{\"idBankStatement\":987654321,\"idVoucher\":6,\"idTransaction\":123481,\"banco\":\"Banco Ejemplo\",\"transferDate\":\"2024-05-11T12:30:00Z\",\"reference\":\"Pago de factura\",\"amount\":1500.75,\"isVerify\":true}]";
        //String json = "[{\"idBankStatement\":987654321,\"idVoucher\":4,\"idTransaction\":123482,\"banco\":\"Banco Ejemplo\",\"transferDate\":\"2024-05-11T12:30:00Z\",\"reference\":\"Pago de factura\",\"amount\":1500.75,\"isVerify\":true},{\"idBankStatement\":987654321,\"idVoucher\":5,\"idTransaction\":123482,\"banco\":\"Banco Ejemplo\",\"transferDate\":\"2024-05-11T12:30:00Z\",\"reference\":\"Pago de factura\",\"amount\":1500.75,\"isVerify\":true}]";
        //String json = "[{\"idBankStatement\":987654321,\"idVoucher\":4,\"idTransaction\":123482,\"banco\":\"Banco Ejemplo\",\"transferDate\":\"2024-05-11T12:30:00Z\",\"reference\":\"Pago de factura\",\"amount\":1500.75,\"isVerify\":true},{\"idBankStatement\":987654321,\"idVoucher\":5,\"idTransaction\":123482,\"banco\":\"Banco Ejemplo\",\"transferDate\":\"2024-05-11T12:30:00Z\",\"reference\":\"Pago de factura\",\"amount\":1500.75,\"isVerify\":true},{\"idBankStatement\":987654321,\"idVoucher\":6,\"idTransaction\":123481,\"banco\":\"Banco Ejemplo\",\"transferDate\":\"2024-05-11T12:30:00Z\",\"reference\":\"Pago de factura\",\"amount\":1500.75,\"isVerify\":true}]";
        //****************** TESTING  ***************************

        //Transforma el body a una lista de entidades bankStatementsDto para su mejor procesamiento
        List<BankStatementsDTO> bankStatementsDTOList = Mappler.convertJsonToBankStatementsDtoList(json);
        // Actualiza el estado verified_transfer de cada registro de vouchersEntities
        bankStatementsDTOList.forEach(bankStatementsDTO -> {
            vouchersEntities.stream()
                    .filter(vouchersEntity -> vouchersEntity.getIdVoucher().equals(bankStatementsDTO.getIdVoucher()))
                    .findFirst()
                    .ifPresentOrElse(
                            vouchersEntity -> {
                                vouchersEntity.setVerifiedTransfer(bankStatementsDTO.getIsVerify());
                                vouchersRepository.save(vouchersEntity);
                            },
                            () -> {
                                throw new EntityNotFoundException("No corresponding entity found for BankStatementsDTO with voucher ID: " + bankStatementsDTO.getIdVoucher());
                            }
                    );
        });
        return vouchersEntities;
    }

    // Proceso de conusmo del microservicio de los comprobantes bancarios
    public ResponseEntity<String> generateHttpRequest(List<VouchersDTO> requestBody)throws HttpClientErrorException.NotFound{
        //Configuracion de los Headers a ser recibidos
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //Configurar la solicitud con las cabeceras
        HttpEntity<List<VouchersDTO>> entity = new HttpEntity<>(requestBody, headers);

        //Realizar la solicitud GET a la URL de la imagen y en caso de existir un error levantara un excepcion
        return restTemplate.exchange(urlBankStatement, HttpMethod.POST, entity, String.class);
    }


}
