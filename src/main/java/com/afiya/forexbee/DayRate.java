package com.afiya.forexbee;

import java.time.LocalDate;

public record DayRate(LocalDate date, double rate, String type) {
}