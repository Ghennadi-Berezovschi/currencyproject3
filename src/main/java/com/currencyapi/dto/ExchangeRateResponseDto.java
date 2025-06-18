package com.currencyapi.dto;

public class ExchangeRateResponseDto {
    private String baseCode;
    private String targetCode;
    private double rate;

    public ExchangeRateResponseDto() {}

    public ExchangeRateResponseDto(String baseCode, String targetCode, double rate) {
        this.baseCode = baseCode;
        this.targetCode = targetCode;
        this.rate = rate;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }


}
