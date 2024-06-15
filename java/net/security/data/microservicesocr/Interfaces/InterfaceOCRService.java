package net.security.data.microservicesocr.Interfaces;

import com.google.api.gax.rpc.FailedPreconditionException;
import io.grpc.StatusRuntimeException;
import net.security.data.microservicesocr.messages.requests.*;
import net.security.data.microservicesocr.messages.responses.JsonResponse;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface InterfaceOCRService {

    JsonResponse extractDataVoucherByUrl(BankVoucherUrlDTO bankVoucherUrlDTO)
            throws IOException, InterruptedException, ExecutionException, TimeoutException, DataIntegrityViolationException, FailedPreconditionException, StatusRuntimeException;

    JsonResponse extractDataVoucherByBase64(BankVoucherBase64DTO bankVoucherBase64DTO)
            throws IOException, InterruptedException, ExecutionException, TimeoutException, DataIntegrityViolationException, FailedPreconditionException, StatusRuntimeException;

    JsonResponse processVoucherData(ImageData imageData, Long idTransaction, String typeSave)
            throws IOException, InterruptedException, ExecutionException, TimeoutException, DataIntegrityViolationException, FailedPreconditionException, StatusRuntimeException;

    JsonResponse getDataVoucherById(Long idTransaction);

    JsonResponse userCreated(AuthLoginRequest authLoginRequest);

    //JsonResponse consumptionByUserAndStatusCode(UserConsumptionDTO userConsumptionDTO);

    //JsonResponse consumptionAllUsersAllStatusCodes();
}
