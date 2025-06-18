package com.currencyapi.service;

import com.currencyapi.dao.CurrencyDao;
import com.currencyapi.dao.CurrencyDaoImpl;
import com.currencyapi.dto.CurrencyRequestDto;
import com.currencyapi.dto.CurrencyResponseDto;
import com.currencyapi.exception.NotFoundException;
import com.currencyapi.model.Currency;
import com.currencyapi.util.MappingUtils;
import com.currencyapi.util.ValidationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class CurrencyService {

    private final CurrencyDao currencyDao = new CurrencyDaoImpl();
    public void addCurrency(CurrencyRequestDto dto) {
        ValidationUtils.validateCurrency(dto.getCode(), dto.getName(), dto.getSign());
        Currency currency = MappingUtils.convertToEntity(dto);
        currencyDao.save(currency);
    }

    public CurrencyResponseDto getCurrencyByCode(String code) {
        ValidationUtils.validateCurrencyCode(code);

        Currency currency = currencyDao.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Currency not found: " + code));

        return MappingUtils.convertToDto(currency);
    }

    public List<CurrencyResponseDto> getAllCurrencies() {
        return currencyDao.findAll().stream()
                .map(MappingUtils::convertToDto)
                .collect(Collectors.toList());
    }


    public void deleteCurrencyById(int id) {
        boolean deleted = currencyDao.delete(id);
        if (!deleted) {
            throw new NotFoundException("Currency with ID " + id + " not found");
        }
    }
}

