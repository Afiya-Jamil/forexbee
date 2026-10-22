package com.afiya.forexbee;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RateScheduler {

    private static final List<String[]> PAIRS = List.of(
            new String[]{"OMR", "INR"},
            new String[]{"USD", "INR"},
            new String[]{"EUR", "INR"});

    private final RateService rateService;

    public RateScheduler(RateService rateService) {
        this.rateService = rateService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        for (String[] pair : PAIRS) {
            try {
                int saved = rateService.backfill(pair[0], pair[1], 90);
                System.out.println("Backfilled " + saved + " rows for " + pair[0] + "/" + pair[1]);
            } catch (Exception e) {
                System.out.println("Backfill failed for " + pair[0] + "/" + pair[1] + ": " + e.getMessage());
            }
        }
        fetchAll();
    }

    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Kolkata")
    public void daily() {
        fetchAll();
    }

    private void fetchAll() {
        for (String[] pair : PAIRS) {
            try {
                rateService.getLatest(pair[0], pair[1]);
                System.out.println("Saved rate for " + pair[0] + "/" + pair[1]);
            } catch (Exception e) {
                System.out.println("Could not fetch " + pair[0] + "/" + pair[1] + ": " + e.getMessage());
            }
        }
    }
}