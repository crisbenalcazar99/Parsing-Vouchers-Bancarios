package net.security.data.microservicesocr.messages.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdTransactionRequest {
    private List<Long> idTransactionList;
}
