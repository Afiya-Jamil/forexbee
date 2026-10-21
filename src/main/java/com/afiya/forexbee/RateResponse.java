package com.afiya.forexbee;

import java.math.BigDecimal;

public record RateResponse(String from, String to, BigDecimal rate) {
}