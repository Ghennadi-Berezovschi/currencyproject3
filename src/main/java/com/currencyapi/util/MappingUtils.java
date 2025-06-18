package com.currencyapi.util;

import com.currencyapi.dto.CurrencyRequestDto;
import com.currencyapi.dto.CurrencyResponseDto;
import com.currencyapi.dto.ExchangeRateResponseDto;
import com.currencyapi.model.Currency;
import com.currencyapi.model.ExchangeRate;
import org.modelmapper.ModelMapper;



public class MappingUtils {

    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    static {
        MODEL_MAPPER.typeMap(CurrencyRequestDto.class, Currency.class)
                .addMapping(CurrencyRequestDto::getName, Currency::setName);
    }

    public static Currency convertToEntity(CurrencyRequestDto dto) {
        return MODEL_MAPPER.map(dto, Currency.class);
    }

    public static CurrencyResponseDto convertToDto(Currency entity) {
        return MODEL_MAPPER.map(entity, CurrencyResponseDto.class);
    }

    public static ExchangeRateResponseDto convertToDto(ExchangeRate rate) {
        ExchangeRateResponseDto dto = new ExchangeRateResponseDto();
        dto.setBaseCode(rate.getBaseCurrency().getCode());
        dto.setTargetCode(rate.getTargetCurrency().getCode());


        dto.setRate(Double.parseDouble(rate.getRate().toPlainString()));

        return dto;
    }
}
