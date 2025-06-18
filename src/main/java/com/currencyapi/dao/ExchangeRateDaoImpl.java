package com.currencyapi.dao;

import com.currencyapi.exception.DatabaseOperationException;
import com.currencyapi.model.Currency;
import com.currencyapi.model.ExchangeRate;
import com.currencyapi.repository.DatabaseConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDaoImpl implements ExchangeRateDao {

    @Override
    public void upsert(ExchangeRate rate) {
        String sql = """
            INSERT INTO exchangerates(basecurrencyid, targetcurrencyid, rate)
            VALUES (?, ?, ?)
            ON CONFLICT(basecurrencyid, targetcurrencyid)
            DO UPDATE SET rate = EXCLUDED.rate
        """;
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rate.getBaseCurrency().getId());
            stmt.setInt(2, rate.getTargetCurrency().getId());
            stmt.setBigDecimal(3, rate.getRate());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error in upsert exchange rate", e);
        }
    }

    @Override
    public List<ExchangeRate> findAll() {
        List<ExchangeRate> result = new ArrayList<>();
        String sql = """
            SELECT
                e.id,
                base.id AS base_id,
                base.code AS base_code,
                base.name AS base_name,
                base.sign AS base_sign,
                target.id AS target_id,
                target.code AS target_code,
                target.name AS target_name,
                target.sign AS target_sign,
                e.rate
            FROM exchangerates e
            JOIN currencies base ON e.basecurrencyid = base.id
            JOIN currencies target ON e.targetcurrencyid = target.id
        """;

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Currency base = new Currency(
                        rs.getInt("base_id"),
                        rs.getString("base_code"),
                        rs.getString("base_name"),
                        rs.getString("base_sign")
                );
                Currency target = new Currency(
                        rs.getInt("target_id"),
                        rs.getString("target_code"),
                        rs.getString("target_name"),
                        rs.getString("target_sign")
                );
                ExchangeRate rate = new ExchangeRate(
                        rs.getInt("id"),
                        base,
                        target,
                        rs.getBigDecimal("rate")
                );
                result.add(rate);
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error in reading exchange rates", e);
        }

        return result;
    }

    @Override
    public ExchangeRate findByCodes(String baseCode, String targetCode) {
        CurrencyDao currencyDao = new CurrencyDaoImpl();

        try {
            Currency base = currencyDao.findByCode(baseCode)
                    .orElseThrow(() -> new DatabaseOperationException("Base currency not found: " + baseCode));

            Currency target = currencyDao.findByCode(targetCode)
                    .orElseThrow(() -> new DatabaseOperationException("Target currency not found: " + targetCode));

            return findByBaseAndTarget(base, target);

        } catch (Exception e) {
            throw new DatabaseOperationException("Error when searching for the exchange rate by currency codes", e);
        }
    }

    @Override
    public ExchangeRate findByCurrencyIds(int baseId, int targetId) {
        CurrencyDao currencyDao = new CurrencyDaoImpl();
        Currency base = currencyDao.findById(baseId).orElse(null);
        Currency target = currencyDao.findById(targetId).orElse(null);

        if (base == null || target == null) return null;

        return findByBaseAndTarget(base, target);
    }

    private ExchangeRate findByBaseAndTarget(Currency base, Currency target) {
        String sql = """
            SELECT e.id, e.rate FROM exchangerates e
            WHERE e.basecurrencyid = ? AND e.targetcurrencyid = ?
        """;

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, base.getId());
            stmt.setInt(2, target.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new ExchangeRate(
                        rs.getInt("id"),
                        base,
                        target,
                        rs.getBigDecimal("rate")
                );
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error in the search exchange rate", e);
        }

        return null;
    }

    @Override
    public Optional<ExchangeRate> updateRate(String baseCode, String targetCode, BigDecimal newRate) {
        String sql = """
            UPDATE exchangerates er
            SET rate = ?
            FROM currencies base, currencies target
            WHERE er.basecurrencyid = base.id
              AND er.targetcurrencyid = target.id
              AND base.code = ?
              AND target.code = ?
            RETURNING
                er.id AS exchange_rate_id,
                base.id AS base_id, base.name AS base_name, base.code AS base_code, base.sign AS base_sign,
                target.id AS target_id, target.name AS target_name, target.code AS target_code, target.sign AS target_sign,
                er.rate AS updated_rate
        """;

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, newRate);
            stmt.setString(2, baseCode);
            stmt.setString(3, targetCode);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Currency base = new Currency(
                        rs.getInt("base_id"),
                        rs.getString("base_code"),
                        rs.getString("base_name"),
                        rs.getString("base_sign")
                );
                Currency target = new Currency(
                        rs.getInt("target_id"),
                        rs.getString("target_code"),
                        rs.getString("target_name"),
                        rs.getString("target_sign")
                );
                ExchangeRate rate = new ExchangeRate(
                        rs.getInt("exchange_rate_id"),
                        base,
                        target,
                        rs.getBigDecimal("updated_rate")
                );
                return Optional.of(rate);
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating the exchange rate", e);
        }

        return Optional.empty();
    }
}
