package com.afiya.forexbee;

import java.time.LocalDate;
import java.util.List;

public record BestDayResponse(String from, String to, String goal, LocalDate targetDate,
                              LocalDate bestDate, double bestRate, String bestType,
                              Double targetRate, Double advantagePercent,
                              List<DayRate> days, String note) {
}