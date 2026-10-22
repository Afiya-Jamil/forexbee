package com.afiya.forexbee;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping("/predict")
    public PredictionResponse predict(@RequestParam String from,
                                      @RequestParam String to,
                                      @RequestParam(defaultValue = "7") int days) {
        return predictionService.predict(from, to, days);
    }
}