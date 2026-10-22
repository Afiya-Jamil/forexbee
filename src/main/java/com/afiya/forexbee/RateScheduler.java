package com.afiya.forexbee;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RateScheduler {

    // Always tracked, even on a fresh database
    private static final List<String[]> DEFAULT_PAIRS = List.of(
            new String[]{"OMR", "INR"},
            new String[]{"USD", "INR"},
            new String[]{"EUR", "INR"});

    private final RateService rateService;
    private final RateRecordRepository repository;

    public RateScheduler(RateService rateService, RateRecordRepository repository) {
        this.rateService = rateService;
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        refreshAll(90);
    }

    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Kolkata")
    public void daily() {
        refreshAll(7);
    }

    private List<String[]> allPairs() {
        Set<String> keys = new LinkedHashSet<>();
        for (String[] p : DEFAULT_PAIRS) {
            keys.add(p[0] + "/" + p[1]);
        }
        for (RateRecordRepository.PairView v : repository.findDistinctPairs()) {
            keys.add(v.getFromCurrency() + "/" + v.getToCurrency());
        }
        List<String[]> result = new ArrayList<>();
        for (String key : keys) {
            result.add(key.split("/"));
        }
        return result;
    }

    private void refreshAll(int days) {
        for (String[] pair : allPairs()) {
            try {
                int saved = rateService.backfill(pair[0], pair[1], days);
                System.out.println("Refreshed " + pair[0] + "/" + pair[1] + ": " + saved + " new rows");
            } catch (Exception e) {
                System.out.println("Could not refresh " + pair[0] + "/" + pair[1] + ": " + e.getMessage());
            }
        }
    }
}