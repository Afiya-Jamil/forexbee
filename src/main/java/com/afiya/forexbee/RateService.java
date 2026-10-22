package com.afiya.forexbee;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RateService {

    private final RestClient client = RestClient.create();

    public RateResponse getLatest(String from, String to) {
        String url = "https://api.frankfurter.dev/v2/rate/"
                + from.toLowerCase() + "/" + to.toLowerCase();
        ApiRate result = client.get().uri(url).retrieve().body(ApiRate.class);
        return new RateResponse(from.toUpperCase(), to.toUpperCase(), result.rate());
    }

        public ConversionResponse convert(BigDecimal amount, String from, String to) {
        RateResponse r = getLatest(from, to);
        BigDecimal result = amount.multiply(r.rate()).setScale(2, RoundingMode.HALF_UP);
        return new ConversionResponse(amount, r.from(), r.to(), r.rate(), result);
    }

        public List<CurrencyInfo> getCurrencies() {
        CurrencyInfo[] list = client.get()
                .uri("https://api.frankfurter.dev/v2/currencies")
                .retrieve()
                .body(CurrencyInfo[].class);
        return Arrays.asList(list);
    }
}