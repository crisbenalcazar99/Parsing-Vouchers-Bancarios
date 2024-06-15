package net.security.data.microservicesocr.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import net.security.data.microservicesocr.messages.requests.BankVoucherBase64DTO;
import net.security.data.microservicesocr.messages.requests.BankVoucherUrlDTO;
import net.security.data.microservicesocr.messages.requests.IdTransactionRequest;
import net.security.data.microservicesocr.messages.responses.JsonResponse;
import net.security.data.microservicesocr.services.OCRServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/api/v1")
public class OCRController {

    @Autowired
    private OCRServiceImpl ocrService;

    private static final Logger logger = LoggerFactory.getLogger(OCRController.class);


    @GetMapping("/version")
    public String getMethodName() {
        return new StringBuilder("Nos encontramos en la versión").append(" ").append("1.0.0").toString();
    }

    //Recibe un vocuher por URL y extrae la informacion
    @PostMapping("/extract/voucher/url")
    public ResponseEntity<JsonResponse> extractDataVoucherByUrl(@Valid @RequestBody BankVoucherUrlDTO bankVoucherUrlDTO)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        logger.info("Method for extracting information from the bank statement from URL.");
        return ResponseEntity.ok()
                .body(ocrService.extractDataVoucherByUrl(bankVoucherUrlDTO));
    }

    //Recibe un voucher en base 64 por el body de la request y extrae la informacion
    @PostMapping("/extract/voucher/base64")
    public ResponseEntity<JsonResponse> extractDataVoucherBase64(@Valid @RequestBody BankVoucherBase64DTO bankVoucherBase64DTO)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        logger.info("Method for extracting information from the bank statement in Base 64");
        return ResponseEntity.ok()
                .body(ocrService.extractDataVoucherByBase64(bankVoucherBase64DTO));
    }

    //Retorna toda la informacion de los comprobantes bancarios ingresados con el idTransaction
    @GetMapping("/verify/voucher/{idTransaction}")
    public ResponseEntity<JsonResponse> verifyVoucherById(@PathVariable("idTransaction") Long idTransaction){
        logger.info("Method for obtain data from a register un table vouchers_data");
        return ResponseEntity.ok()
                .body(ocrService.getDataVoucherById(idTransaction));
    }


    @GetMapping("/verified/acreditation/{idTransaction}")
    public ResponseEntity<JsonResponse> verifiedAcreditationByIdTransaction(@PathVariable("idTransaction") Long idTransaction)
            throws JsonProcessingException {
        return ResponseEntity.ok()
                .body(ocrService.verifiedAcreditation(idTransaction));
    }

    @GetMapping("/verified/acreditation/list")
    public ResponseEntity<JsonResponse> verifiedAcreditationByListIdTransaction(@Valid @RequestBody IdTransactionRequest idTransactionRequest)
            throws JsonProcessingException {
        return ResponseEntity.ok()
                // .contentType(MediaType.APPLICATION_JSON)
                .body(ocrService.verifiedAcreditation(idTransactionRequest.getIdTransactionList()));
    }

}