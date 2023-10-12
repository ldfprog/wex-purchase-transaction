package org.lucasdf.integrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lucasdf.controllers.TransactionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class TreasuryService {

    static final String TREASURY_API_URL = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange";

    Logger logger = LoggerFactory.getLogger(TransactionController.class);

    public Float retrieveExchangeRate(LocalDate date, String currency) {

        StringBuilder stringBuilder = new StringBuilder(TREASURY_API_URL);
        String query = stringBuilder
                .append("?page[number]=1&page[size]=1&fields=exchange_rate,record_date&sort=-record_date")
                .append(
                        String.format(
                                "&filter=record_date:lte:%s,record_date:gte:%s,currency:eq:%s",
                                date,
                                date.minusMonths(6),
                                currency
                        )
                ).toString();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(query))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.error("There was an issue requesting the treasury API. Status Code: %s Body: %s", response.statusCode(), response.body());
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> exchangeData = mapper.readValue(response.body(), Map.class);
            List<Map<String, String>> data = (List)exchangeData.get("data");

            if (data.isEmpty())
                return 0.0f;

            Float exchangeRate = Float.valueOf(data.get(0).get("exchange_rate"));
            return exchangeRate;

        } catch (URISyntaxException | IOException | InterruptedException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
