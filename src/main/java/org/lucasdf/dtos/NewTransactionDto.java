package org.lucasdf.dtos;

import java.time.LocalDate;

public record NewTransactionDto(
        String description,
        LocalDate date,
        Float amount
) {

}
