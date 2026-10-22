package com.afiya.forexbee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BestDayService {

    private final RateRecordRepository repository;
    private final PredictionService predictionService;

    public BestDayService(RateRecordRepository repository, PredictionService predictionService) {
        this.repository = repository;
        this.predictionService = predictionService;
    }

    public BestDayResponse bestDay(String from, String to, LocalDate target,
                                   int window, String goal) {
        String f = from.toUpperCase();
        String t = to.toUpperCase();
        int w = Math.max(1, Math.min(window, 10));
        boolean wantHigh = !"low".equalsIgnoreCase(goal);

        PredictionResponse p = predictionService.predict(f, t, 30);
        LocalDate limit = p.lastDate().plusDays(30);
        if (target.isAfter(limit)) {
            throw new IllegalArgumentException(
                    "Target date is too far ahead. Try a date on or before " + limit);
        }

        LocalDate start = target.minusDays(w);
        LocalDate end = target.plusDays(w);

        List<DayRate> days = new ArrayList<>();
        for (RateRecord r : repository.findByFromCurrencyAndToCurrencyOrderByRateDateAsc(f, t)) {
            LocalDate d = r.getRateDate();
            if (!d.isBefore(start) && !d.isAfter(end)) {
                days.add(new DayRate(d, r.getRate().doubleValue(), "actual"));
            }
        }
        for (ForecastPoint fp : p.forecast()) {
            LocalDate d = fp.date();
            if (!d.isBefore(start) && !d.isAfter(end)) {
                days.add(new DayRate(d, fp.rate(), "forecast"));
            }
        }
        if (days.isEmpty()) {
            throw new IllegalStateException("No rate data around " + target);
        }
        days.sort(Comparator.comparing(DayRate::date));

        DayRate best = days.get(0);
        Double targetRate = null;
        for (DayRate d : days) {
            if (wantHigh ? d.rate() > best.rate() : d.rate() < best.rate()) {
                best = d;
            }
            if (d.date().equals(target)) {
                targetRate = d.rate();
            }
        }

        Double advantage = null;
        if (targetRate != null && targetRate != 0) {
            double diff = wantHigh ? best.rate() - targetRate : targetRate - best.rate();
            advantage = Math.round(diff / targetRate * 10000.0) / 100.0;
        }

        return new BestDayResponse(f, t, wantHigh ? "high" : "low", target,
                best.date(), best.rate(), best.type(), targetRate, advantage, days,
                "Estimate based on recent trend. Forecast days are not guaranteed. Not financial advice.");
    }
}