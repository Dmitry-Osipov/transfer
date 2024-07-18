package org.microservice.transfer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microservice.transfer.model.TransferRestModel;
import org.microservice.transfer.service.ITransferService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/transfers")
public class TransferController {
    private final ITransferService service;

    @PostMapping
    public boolean transfer(@RequestBody TransferRestModel model) {
        return service.transfer(model);
    }
}
