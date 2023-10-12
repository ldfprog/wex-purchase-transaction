package org.lucasdf.services;

import org.lucasdf.controllers.TransactionController;
import org.lucasdf.dtos.NewTransactionDto;
import org.lucasdf.dtos.RequestedTransactionDto;
import org.lucasdf.models.WexTransaction;
import org.lucasdf.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TransactionService {

    Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionRepository repository;

    public Long storeTransaction(NewTransactionDto newTransactionDto) {

        logger.info("Wex Transaction - Called service storeTransaction.");

        Optional<WexTransaction> optionalWexTransaction = repository.findByDescription(newTransactionDto.description());

        AtomicReference<Long> transactionId = new AtomicReference<>();

        optionalWexTransaction.ifPresentOrElse(
                (wexTransaction) -> {
                    logger.info("Wex Transaction - Updating transaction.");
                    wexTransaction.setDate(newTransactionDto.date());
                    wexTransaction.setAmount(newTransactionDto.amount());

                    repository.save(wexTransaction);

                    transactionId.set(wexTransaction.getId());
                },
                () -> {
                    logger.info("Wex Transaction - Creating transaction.");
                    WexTransaction wexTransaction = new WexTransaction(
                            newTransactionDto.description(),
                            newTransactionDto.date(),
                            newTransactionDto.amount()
                    );

                    wexTransaction = repository.save(wexTransaction);
                    transactionId.set(wexTransaction.getId());
                }
        );

        return transactionId.get();
    }

    public RequestedTransactionDto retrieveTransaction(Long transactionId) {

        Optional<WexTransaction> optionalWexTransaction = repository.findById(transactionId);

        if (optionalWexTransaction.isPresent()) {

            WexTransaction transaction = optionalWexTransaction.get();

            RequestedTransactionDto requestedTransactionDto =
                    new RequestedTransactionDto(
                            transaction.getId(),
                            transaction.getDescription(),
                            transaction.getDate(),
                            transaction.getAmount(),
                            null, //Attribute is going to be filled by the exchange integration
                            null //Attribute is going to be filled by the exchange integration
                    );

            return requestedTransactionDto;
        }

        return null;
    }
}
