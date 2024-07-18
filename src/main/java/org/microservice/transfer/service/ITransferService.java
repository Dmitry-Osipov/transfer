package org.microservice.transfer.service;

import org.microservice.transfer.model.TransferRestModel;

public interface ITransferService {
    boolean transfer(TransferRestModel model);
}
