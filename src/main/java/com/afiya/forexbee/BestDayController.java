package com.afiya.forexbee;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BestDayController {

    private final BestDayService bestDayService;

    public BestDayController(BestDayService bestDayService) {
        this.bestDayService = bestDayService;
    }

    @GetMapping("/best-day")
    public BestDayResponse bestDay(@RequestParam String from,
                                   @RequestParam String to,
                                   @RequestParam String date,
                                   @RequestParam(defaultValue = "5") int window,
                                   @RequestParam(defaultValue = "high") String goal) {
        return bestDayService.bestDay(from, to, LocalDate.parse(date), window, goal);
    }
}