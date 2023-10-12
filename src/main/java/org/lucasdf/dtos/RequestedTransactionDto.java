package org.lucasdf.dtos;

import java.time.LocalDate;

public record RequestedTransactionDto(
        Long id,
        String description,
        LocalDate date,
        Float amount,
        Float exchangeRate,
        Float convertedAmount
) {

    public RequestedTransactionDto withExchangeRate(Float exchangeRate, Float convertedAmount) {
        return new RequestedTransactionDto(
                id(),
                description(),
                date(),
                amount(),
                exchangeRate,
                convertedAmount
        );
    }
}
