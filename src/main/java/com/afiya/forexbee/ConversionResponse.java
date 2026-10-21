package com.afiya.forexbee;

import java.math.BigDecimal;

public record ConversionResponse(BigDecimal amount, String from, String to,
                                 BigDecimal rate, BigDecimal result) {
}