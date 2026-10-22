package com.afiya.forexbee;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RateRecordRepository extends JpaRepository<RateRecord, Long> {

    interface PairView {
        String getFromCurrency();
        String getToCurrency();
    }

    boolean existsByFromCurrencyAndToCurrencyAndRateDate(
            String fromCurrency, String toCurrency, LocalDate rateDate);

    List<RateRecord> findByFromCurrencyAndToCurrencyOrderByRateDateAsc(
            String fromCurrency, String toCurrency);

    @Query("select distinct r.fromCurrency as fromCurrency, r.toCurrency as toCurrency from RateRecord r")
    List<PairView> findDistinctPairs();
}