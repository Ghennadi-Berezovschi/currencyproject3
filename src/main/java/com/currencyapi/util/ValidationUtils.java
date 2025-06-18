package com.currencyapi.util;

import com.currencyapi.exception.InvalidInputException;

import java.math.BigDecimal;

public class ValidationUtils {

    private static final String CURRENCY_CODE_REGEX = "^[A-Z]{3}$";
    private static final String PAIR_PATH_REGEX = "^/[A-Z]{6}$";

    public static void validateCurrencyCode(String code) {
        if (code == null || code.isBlank()) {
            throw new InvalidInputException("Currency code is missing");
        }

        if (!code.matches(CURRENCY_CODE_REGEX)) {
            throw new InvalidInputException("Currency code must contain exactly 3 uppercase letters (e.g. USD, EUR)");
        }
    }

    public static void validateCurrency(String code, String name, String sign) {
        if (code == null || name == null || sign == null ||
                code.isBlank() || name.isBlank() || sign.isBlank()) {
            throw new InvalidInputException("Missing required fields");
        }

        validateCurrencyCode(code);
    }

    public static void validateCurrencyPairPath(String path) {
        if (path == null || !path.matches(PAIR_PATH_REGEX)) {
            throw new InvalidInputException("Invalid currency pair format (expected /USDEUR)");
        }
    }

    public static boolean isValidCurrencyPairPath(String path) {
        return path != null && path.matches(PAIR_PATH_REGEX);
    }

    public static void validateCurrencyPairPathOrThrow(String path) {
        if (!isValidCurrencyPairPath(path)) {
            throw new InvalidInputException("Invalid currency code format (expected /USDEUR)");
        }
    }

    public static void validateExchangeRateInput(String baseCode, String targetCode, String rateStr) {
        if (baseCode == null || targetCode == null || rateStr == null ||
                baseCode.isBlank() || targetCode.isBlank() || rateStr.isBlank()) {
            throw new InvalidInputException("Missing required fields");
        }
    }

    public static void validateExchangeParams(String from, String to, String amountStr) {
        if (from == null || to == null || amountStr == null ||
                from.isBlank() || to.isBlank() || amountStr.isBlank()) {
            throw new InvalidInputException("Missing parameters: from, to, amount");
        }
    }

    public static BigDecimal validateAndParseRate(String rateStr) {
        if (rateStr == null || rateStr.isBlank()) {
            throw new InvalidInputException("Missing 'rate' parameter");
        }

        try {
            return new BigDecimal(rateStr);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid rate format: " + rateStr);
        }
    }

    public static int parseId(String idStr) {
        if (idStr == null || idStr.isBlank()) {
            throw new InvalidInputException("Missing ID");
        }

        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid ID format: " + idStr);
        }
    }
}
