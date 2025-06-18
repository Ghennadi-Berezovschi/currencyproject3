package com.currencyapi.dao;

import com.currencyapi.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateDao {
    void upsert(ExchangeRate rate);
    List<ExchangeRate> findAll();
    ExchangeRate findByCodes(String baseCode, String targetCode);
    ExchangeRate findByCurrencyIds(int baseId, int targetId);
    Optional<ExchangeRate> updateRate(String baseCode, String targetCode, BigDecimal newRate);
}
