package com.afiya.forexbee;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRecordRepository extends JpaRepository<RateRecord, Long> {

    boolean existsByFromCurrencyAndToCurrencyAndRateDate(
            String fromCurrency, String toCurrency, LocalDate rateDate);

    List<RateRecord> findByFromCurrencyAndToCurrencyOrderByRateDateAsc(
            String fromCurrency, String toCurrency);
}