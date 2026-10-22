package com.afiya.forexbee;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class RateController {

    private final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @GetMapping("/rate")
    public RateResponse rate(@RequestParam String from, @RequestParam String to) {
        return rateService.getLatest(from, to);
    }
        @GetMapping("/convert")
    public ConversionResponse convert(@RequestParam BigDecimal amount,
                                      @RequestParam String from,
                                      @RequestParam String to) {
        return rateService.convert(amount, from, to);
    }
        @GetMapping("/currencies")
    public List<CurrencyInfo> currencies() {
        return rateService.getCurrencies();
    }
}