package com.afiya.forexbee;

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
}