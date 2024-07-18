package org.microservice.transfer.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class TransferRestModel {
    private String senderId;
    private String recipientId;
    private BigDecimal amount;
}
