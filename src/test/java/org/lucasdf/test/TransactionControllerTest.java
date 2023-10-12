package org.lucasdf.test;

import org.junit.jupiter.api.Test;
import org.lucasdf.controllers.TransactionController;
import org.lucasdf.dtos.DefaultResponse;
import org.lucasdf.dtos.NewTransactionDto;
import org.lucasdf.dtos.RequestedTransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TransactionControllerTest {

    @Autowired
    private TransactionController transactionController;

    @Value(value="${local.server.port}")
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate("user", "password");
    String url = "http://localhost:8080/wex/transaction";

    @Test
    public void contextLoads() throws Exception {
        assertThat(transactionController).isNotNull();
    }

    @Test
    public void securitySetup() throws Exception {

        TestRestTemplate restTemplateBadUser = new TestRestTemplate("resu", "drowssad");

        ResponseEntity<DefaultResponse> response =
                restTemplateBadUser.getForEntity(url + "?transactionId=1", DefaultResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    public void storeTransactionSuccess() {

        NewTransactionDto newTransactionDto = new NewTransactionDto(
                "Test transaction",
                LocalDate.parse("2023-10-11"),
                3333.33f
        );

        ResponseEntity<DefaultResponse> response =
                this.restTemplate.postForEntity(url, newTransactionDto, DefaultResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().transactionId()).isGreaterThan(0);
    }

    @Test
    public void storeTransactionBadRequestDescription() {

        NewTransactionDto newTransactionDto = new NewTransactionDto(
                null,
                LocalDate.parse("2023-10-11"),
                3333.33f
        );

        ResponseEntity<DefaultResponse> response =
                this.restTemplate.postForEntity(url, newTransactionDto, DefaultResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().message()).contains("description");
    }

    @Test
    public void storeTransactionBadRequestDate() {

        NewTransactionDto newTransactionDto = new NewTransactionDto(
                "Test transaction",
                null,
                3333.33f
        );

        ResponseEntity<DefaultResponse> response =
                this.restTemplate.postForEntity(url, newTransactionDto, DefaultResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().message()).contains("date");
    }

    @Test
    public void storeTransactionBadRequestAmount() {

        NewTransactionDto newTransactionDto = new NewTransactionDto(
                "Test transaction",
                LocalDate.parse("2023-10-11"),
                null
        );

        ResponseEntity<DefaultResponse> response =
                this.restTemplate.postForEntity(url, newTransactionDto, DefaultResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().message()).contains("amount");
    }

    @Test
    public void retrieveTransactionConflictNotFound() {

        ResponseEntity<DefaultResponse> response =
                this.restTemplate.getForEntity(url + "?transactionId=-1&currency=Real", DefaultResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody().message()).contains("not found");
    }

    @Test
    public void retrieveTransactionBadRequestTransactionId() {

        ResponseEntity<DefaultResponse> response =
                this.restTemplate.getForEntity(url + "?currency=Real", DefaultResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().message()).contains("transactionId");
    }

    @Test
    public void retrieveTransactionBadRequestCurrency() {

        ResponseEntity<DefaultResponse> response =
                this.restTemplate.getForEntity(url + "?transactionId=1", DefaultResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().message()).contains("currency");
    }

    @Test
    public void retrieveTransactionSuccess() {

        NewTransactionDto newTransactionDto = new NewTransactionDto(
                "Retrieve Test transaction",
                LocalDate.parse("2023-10-11"),
                3333.33f
        );

        ResponseEntity<DefaultResponse> response =
                this.restTemplate.postForEntity(url, newTransactionDto, DefaultResponse.class);

        System.out.println(response.getStatusCode());

        ResponseEntity<RequestedTransactionDto> responseRetrieve =
                this.restTemplate.getForEntity(url + "?currency=Real&transactionId=" + response.getBody().transactionId(), RequestedTransactionDto.class);

        assertThat(responseRetrieve.getStatusCode().value()).isEqualTo(200);
        assertThat(responseRetrieve.getBody().description()).isEqualTo("Retrieve Test transaction");
        assertThat(responseRetrieve.getBody().convertedAmount()).isGreaterThan(0.0f);
    }
}
