package com.afiya.forexbee;

import java.time.LocalDate;
import java.util.List;

public record PredictionResponse(String from, String to, LocalDate lastDate,
                                 double latestRate, String direction,
                                 double slopePerDay, double shortAverage,
                                 double longAverage, List<ForecastPoint> forecast,
                                 String note) {
}