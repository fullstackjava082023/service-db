package com.example.server_db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface myrepository extends JpaRepository<ExchangeRate, Long> {

    ExchangeRate getByDateAndFromRateAndToRate(String date, String fromRate, String toRate);

    @Query("SELECT e.value FROM ExchangeRate e WHERE e.date = :date AND e.fromRate = :fromRate AND e.toRate = :toRate")
    String getValueByDateAndFromRateAndToRate(@Param("date") String date,
                                              @Param("fromRate") String fromRate,
                                              @Param("toRate") String toRate);
}
