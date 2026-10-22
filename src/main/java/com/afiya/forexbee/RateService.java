package com.afiya.forexbee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RateService {

    // Currencies that use 3 decimal places
    private static final Set<String> THREE_DECIMALS =
            Set.of("OMR", "KWD", "BHD", "JOD", "IQD", "LYD", "TND");

    private final RestClient client = RestClient.create();
    private final RateRecordRepository repository;

    public RateService(RateRecordRepository repository) {
        this.repository = repository;
    }

    public RateResponse getLatest(String from, String to) {
        String url = "https://api.frankfurter.dev/v2/rate/"
                + from.toLowerCase() + "/" + to.toLowerCase();
        ApiRate result = client.get().uri(url).retrieve().body(ApiRate.class);
        String f = from.toUpperCase();
        String t = to.toUpperCase();
        saveToday(f, t, result.rate());
        return new RateResponse(f, t, result.rate());
    }

    private void saveToday(String from, String to, BigDecimal rate) {
        LocalDate today = LocalDate.now();
        if (!repository.existsByFromCurrencyAndToCurrencyAndRateDate(from, to, today)) {
            repository.save(new RateRecord(from, to, today, rate));
        }
    }

    public List<RateRecord> getHistory(String from, String to) {
        return repository.findByFromCurrencyAndToCurrencyOrderByRateDateAsc(
                from.toUpperCase(), to.toUpperCase());
    }

    public ConversionResponse convert(BigDecimal amount, String from, String to) {
        RateResponse r = getLatest(from, to);
        int scale = THREE_DECIMALS.contains(r.to()) ? 3 : 2;
        BigDecimal result = amount.multiply(r.rate()).setScale(scale, RoundingMode.HALF_UP);
        return new ConversionResponse(amount, r.from(), r.to(), r.rate(), result);
    }

    public List<CurrencyInfo> getCurrencies() {
        CurrencyInfo[] list = client.get()
                .uri("https://api.frankfurter.dev/v2/currencies")
                .retrieve()
                .body(CurrencyInfo[].class);
        return Arrays.asList(list);
    }

    public int backfill(String from, String to, int days) {
        String f = from.toUpperCase();
        String t = to.toUpperCase();
        LocalDate start = LocalDate.now().minusDays(days);
        String url = "https://api.frankfurter.dev/v2/rates?from=" + start
                + "&base=" + f + "&quotes=" + t;
        ApiHistoryRate[] rows = client.get().uri(url).retrieve().body(ApiHistoryRate[].class);
        if (rows == null) {
            return 0;
        }
        int saved = 0;
        for (ApiHistoryRate row : rows) {
            if (!repository.existsByFromCurrencyAndToCurrencyAndRateDate(f, t, row.date())) {
                repository.save(new RateRecord(f, t, row.date(), row.rate()));
                saved++;
            }
        }
        return saved;
    }
}