package org.lucasdf.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.lucasdf.dtos.DefaultResponse;
import org.lucasdf.dtos.NewTransactionDto;
import org.lucasdf.dtos.RequestedTransactionDto;
import org.lucasdf.integrations.TreasuryService;
import org.lucasdf.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {

    Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    TransactionService service;

    @Autowired
    TreasuryService treasuryService;

    /**
     * @param requestBody the object representing the submitted transaction
     */
    @Operation(summary = "Endpoint responsible for storing a transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction stored successfully.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DefaultResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Fields missing in requestBody",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DefaultResponse.class)) }) })
    @PostMapping
    public ResponseEntity<DefaultResponse> storeTransaction(@RequestBody NewTransactionDto requestBody) {

        logger.info("Wex Transaction - Called endpoint storeTransaction.");
        logger.debug(requestBody.toString());

        if (requestBody.description() == null || requestBody.description().isBlank())
            return new ResponseEntity<>(new DefaultResponse("The transaction requires a description.", null), HttpStatus.BAD_REQUEST);

        if (requestBody.date() == null)
            return new ResponseEntity<>(new DefaultResponse("The transaction requires a date.", null), HttpStatus.BAD_REQUEST);

        if (requestBody.amount() == null)
            return new ResponseEntity<>(new DefaultResponse("The transaction requires an amount.", null), HttpStatus.BAD_REQUEST);

        Long transactionId = service.storeTransaction(requestBody);

        return new ResponseEntity<>(new DefaultResponse("Transaction stored successfully.", transactionId), HttpStatus.OK);
    }

    /**
     * @param transactionId database identification of a stored transaction
     * @param currency as described by the Treasury Reporting Rates of Exchange API
     *
     * Of note: Parameters are required = false to avoid untreated error 500.
     *          Validation is done inside the operation and a code 400 with proper messaging is returned.
     */
    @Operation(summary = "Endpoint responsible for retrieving a transaction and adding the converted amount.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RequestedTransactionDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Parameters missing in request query.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DefaultResponse.class)) }),
            @ApiResponse(responseCode = "409", description = "Unable to find requested transaction or unable to integrate with treasury API.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DefaultResponse.class)) }) })
    @GetMapping
    public ResponseEntity<?> retrieveTransaction(@RequestParam(required = false) Long transactionId, @RequestParam(required = false) String currency) {

        logger.info("Wex Transaction - Called endpoint retrieveTransaction.");
        logger.debug("[" + transactionId + "," + currency + "]");

        if (transactionId == null)
            return new ResponseEntity<>(new DefaultResponse("You must provide the transactionId.", null), HttpStatus.BAD_REQUEST);

        if (currency == null || currency.isBlank())
            return new ResponseEntity<>(new DefaultResponse("You must provide the currency.", transactionId), HttpStatus.BAD_REQUEST);

        RequestedTransactionDto requestedTransactionDto = service.retrieveTransaction(transactionId);

        if (requestedTransactionDto == null)
            return new ResponseEntity<>(new DefaultResponse("The transactionId provided was not found.", transactionId), HttpStatus.CONFLICT);

        Float exchangeRate = treasuryService.retrieveExchangeRate(requestedTransactionDto.date(), currency);

        if (exchangeRate == 0.0f)
            return new ResponseEntity<>(new DefaultResponse("The purchase cannot be converted to the target currency.", transactionId), HttpStatus.CONFLICT);

        if (exchangeRate == null)
            return new ResponseEntity<>(new DefaultResponse("There was an error processing the request. Refer to the application log for more info.", transactionId), HttpStatus.INTERNAL_SERVER_ERROR);

        requestedTransactionDto = requestedTransactionDto.withExchangeRate(
                exchangeRate,
                Math.round(requestedTransactionDto.amount()*exchangeRate*100)/100.0f
                );

        return new ResponseEntity<>(requestedTransactionDto, HttpStatus.OK);
    }
}
