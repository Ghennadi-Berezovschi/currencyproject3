package com.currencyapi.service;

import com.currencyapi.dao.CurrencyDao;
import com.currencyapi.dao.CurrencyDaoImpl;
import com.currencyapi.dao.ExchangeRateDao;
import com.currencyapi.dao.ExchangeRateDaoImpl;
import com.currencyapi.dto.ExchangeRateRequestDto;
import com.currencyapi.dto.ExchangeRateResponseDto;
import com.currencyapi.dto.ExchangeResponseDto;
import com.currencyapi.exception.InvalidInputException;
import com.currencyapi.exception.NotFoundException;
import com.currencyapi.model.Currency;
import com.currencyapi.model.ExchangeRate;
import com.currencyapi.util.MappingUtils;
import com.currencyapi.util.ValidationUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ExchangeRateService {

    private final CurrencyDao currencyDao = new CurrencyDaoImpl();
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDaoImpl();

    public void save(ExchangeRateRequestDto dto) {

        ValidationUtils.validateExchangeRateInput(
                dto.getBaseCurrencyCode(),
                dto.getTargetCurrencyCode(),
                dto.getRate()
        );

        Currency baseCurrency = currencyDao.findByCode(dto.getBaseCurrencyCode())
                .orElseThrow(() -> new NotFoundException("Base currency not found: " + dto.getBaseCurrencyCode()));

        Currency targetCurrency = currencyDao.findByCode(dto.getTargetCurrencyCode())
                .orElseThrow(() -> new NotFoundException("Target currency not found: " + dto.getTargetCurrencyCode()));


        BigDecimal rate = ValidationUtils.validateAndParseRate(dto.getRate());
        ExchangeRate exchangeRate = new ExchangeRate(0, baseCurrency, targetCurrency, rate);


        exchangeRateDao.upsert(exchangeRate);
    }

    public List<ExchangeRateResponseDto> getAllRates() {
        List<ExchangeRate> rates = exchangeRateDao.findAll();
        return rates.stream()
                .map(MappingUtils::convertToDto)
                .collect(Collectors.toList());
    }

    public ExchangeRateResponseDto getRateByCodes(String baseCode, String targetCode) {
        ExchangeRate rate = exchangeRateDao.findByCodes(baseCode, targetCode);
        if (rate == null) {
            throw new NotFoundException("Exchange rate not found for " + baseCode + " to " + targetCode);
        }
        return MappingUtils.convertToDto(rate);
    }

    public ExchangeRateResponseDto updateRate(String baseCode, String targetCode, BigDecimal rate) {
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Rate must be a positive number");
        }

        ExchangeRate updated = exchangeRateDao.updateRate(baseCode, targetCode, rate)
                .orElseThrow(() -> new NotFoundException("Exchange rate not found for " + baseCode + " to " + targetCode));

        return MappingUtils.convertToDto(updated);
    }

    public ExchangeResponseDto exchange(String fromCode, String toCode, String amountStr) {
        ValidationUtils.validateExchangeParams(fromCode, toCode, amountStr);

        Currency base = currencyDao.findByCode(fromCode)
                .orElseThrow(() -> new NotFoundException("Currency not found: " + fromCode));

        Currency target = currencyDao.findByCode(toCode)
                .orElseThrow(() -> new NotFoundException("Currency not found: " + toCode));

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid amount format: " + amountStr);
        }

        BigDecimal rate = resolveRate(fromCode, toCode);
        BigDecimal converted = rate.multiply(amount);

        ExchangeResponseDto dto = new ExchangeResponseDto();
        dto.setBaseCurrency(base.getCode());
        dto.setTargetCurrency(target.getCode());
        dto.setRate(rate);
        dto.setAmount(amount);
        dto.setConvertedAmount(converted);

        return dto;
    }

    public BigDecimal resolveRate(String fromCode, String toCode) {
        ExchangeRate direct = exchangeRateDao.findByCodes(fromCode, toCode);
        if (direct != null) return direct.getRate();

        ExchangeRate reverse = exchangeRateDao.findByCodes(toCode, fromCode);
        if (reverse != null) {
            try {
                return BigDecimal.ONE.divide(reverse.getRate(), 10, BigDecimal.ROUND_HALF_UP);
            } catch (ArithmeticException e) {
                throw new InvalidInputException("Invalid reverse rate division for pair: " + toCode + "/" + fromCode);
            }
        }

        ExchangeRate usdToBase = exchangeRateDao.findByCodes("USD", fromCode);
        ExchangeRate usdToTarget = exchangeRateDao.findByCodes("USD", toCode);

        if (usdToBase != null && usdToTarget != null) {
            return usdToTarget.getRate().divide(usdToBase.getRate(), 10, BigDecimal.ROUND_HALF_UP);
        }

        throw new NotFoundException("Exchange rate not found for pair: " + fromCode + "/" + toCode);
    }


}

