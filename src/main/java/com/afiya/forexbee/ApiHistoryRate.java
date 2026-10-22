package com.afiya.forexbee;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ApiHistoryRate(LocalDate date, BigDecimal rate) {
}