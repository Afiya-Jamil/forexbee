package com.afiya.forexbee;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PredictionService {

    private final RateRecordRepository repository;

    public PredictionService(RateRecordRepository repository) {
        this.repository = repository;
    }

    public PredictionResponse predict(String from, String to, int days) {
        String f = from.toUpperCase();
        String t = to.toUpperCase();
        int ahead = Math.max(1, Math.min(days, 30));

        List<RateRecord> all =
                repository.findByFromCurrencyAndToCurrencyOrderByRateDateAsc(f, t);
        if (all.size() < 10) {
            throw new IllegalStateException("Not enough saved history for " + f + "/" + t);
        }

        int n = Math.min(30, all.size());
        List<RateRecord> recent = all.subList(all.size() - n, all.size());

        // Linear regression: x = day number (0..n-1), y = rate
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
        for (int i = 0; i < n; i++) {
            double y = recent.get(i).getRate().doubleValue();
            sumX += i;
            sumY += y;
            sumXY += i * y;
            sumXX += (double) i * i;
        }
        double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        RateRecord last = recent.get(n - 1);
        double latest = last.getRate().doubleValue();

        double longAvg = sumY / n;
        int shortN = Math.min(7, n);
        double shortSum = 0;
        for (int i = n - shortN; i < n; i++) {
            shortSum += recent.get(i).getRate().doubleValue();
        }
        double shortAvg = shortSum / shortN;

        // Direction: change per day as a percent of the latest rate
        double pctPerDay = slope / latest * 100;
        String direction = "FLAT";
        if (pctPerDay > 0.02) {
            direction = "UP";
        } else if (pctPerDay < -0.02) {
            direction = "DOWN";
        }

        List<ForecastPoint> forecast = new ArrayList<>();
        for (int k = 1; k <= ahead; k++) {
            double y = intercept + slope * (n - 1 + k);
            forecast.add(new ForecastPoint(last.getRateDate().plusDays(k), round(y)));
        }

        return new PredictionResponse(f, t, last.getRateDate(), latest, direction,
                round(slope), round(shortAvg), round(longAvg), forecast,
                "Simple trend estimate from recent history. Not financial advice.");
    }

    private double round(double v) {
        return Math.round(v * 10000.0) / 10000.0;
    }
}