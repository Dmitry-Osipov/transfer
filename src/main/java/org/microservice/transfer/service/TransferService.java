package org.microservice.transfer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microservice.core.DepositRequestedEvent;
import org.microservice.core.WithdrawalRequestedEvent;
import org.microservice.transfer.error.TransferServiceException;
import org.microservice.transfer.model.TransferRestModel;
import org.microservice.transfer.persistence.TransferEntity;
import org.microservice.transfer.persistence.TransferRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService implements ITransferService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Environment env;
    private final RestTemplate restTemplate;
    private final TransferRepository repository;

    @Override
    @Transactional("transactionManager")
    public boolean transfer(TransferRestModel model) {
        var withdrawalEvent =
                new WithdrawalRequestedEvent(model.getSenderId(), model.getRecipientId(), model.getAmount());
        var depositEvent = new DepositRequestedEvent(model.getSenderId(), model.getRecipientId(), model.getAmount());

        try {
            var transfer = new TransferEntity();
            transfer.setId(UUID.randomUUID().toString());
            BeanUtils.copyProperties(model, transfer);
            repository.save(transfer);

            kafkaTemplate.send(env.getProperty("withdraw-money-topic", "withdraw-money-topic"),
                    withdrawalEvent);
            log.info("Sent event to withdrawal topic");

            // Business logic that causes and error
            callRemoteService();

            kafkaTemplate.send(env.getProperty("deposit-money-topic", "deposit-money-topic"),
                    depositEvent);
            log.info("Sent event to deposit topic");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TransferServiceException(e);
        }

        return true;
    }

    private ResponseEntity<String> callRemoteService() throws Exception {
        String url = "http://localhost:8090/response/200";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Received response: {}", response.getBody());
        } else {
            throw new Exception("Destination Microservice not available");
        }

        return response;
    }
}
