package com.currencyapi.dao;

import com.currencyapi.model.Currency;
import java.util.Optional;

public interface CurrencyDao extends CrudDao<Currency, Integer> {
    Optional<Currency> findByCode(String code);

}
