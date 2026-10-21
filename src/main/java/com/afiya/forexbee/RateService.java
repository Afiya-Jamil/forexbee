package com.afiya.forexbee;

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
}