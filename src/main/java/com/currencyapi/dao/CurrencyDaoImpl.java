package com.currencyapi.dao;

import com.currencyapi.exception.DatabaseOperationException;
import com.currencyapi.exception.EntityExistsException;
import com.currencyapi.model.Currency;
import com.currencyapi.repository.DatabaseConnectionManager;

import java.sql.*;
import java.util.*;

public class CurrencyDaoImpl implements CurrencyDao {

    @Override
    public Optional<Currency> findById(Integer id) {
        String sql = "SELECT id, code, name, sign FROM currencies WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Currency currency = new Currency(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("sign")
                );
                return Optional.of(currency);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while reading currency by ID", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT id, code, name, sign FROM currencies";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Currency currency = new Currency(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("sign")
                );
                currencies.add(currency);
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error when getting the list of currencies from the database", e
            );
        }

        return currencies;
    }


    @Override
    public Currency save(Currency currency) {
        String sql = "INSERT INTO currencies (code, name, sign) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currency.getCode());
            stmt.setString(2, currency.getName());
            stmt.setString(3, currency.getSign());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currency.setId(rs.getInt("id"));
            }

            return currency;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // код валюты уже есть
                throw new EntityExistsException("Currency with code '" + currency.getCode() + "' already exists", e);
            }
            throw new DatabaseOperationException("Error while saving currency", e);
        }
    }

    @Override
    public Currency update(Currency currency) {
        String sql = "UPDATE currencies SET code = ?, name = ?, sign = ? WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currency.getCode());
            stmt.setString(2, currency.getName());
            stmt.setString(3, currency.getSign());
            stmt.setInt(4, currency.getId());

            stmt.executeUpdate();
            return currency;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while updating currency", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM currencies WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error when deleting currency ID = " + id, e
            );
        }
        return false;
    }


    @Override
    public Optional<Currency> findByCode(String code) {
        String sql = "SELECT id, code, name, sign FROM currencies WHERE UPPER(code) = UPPER(?)";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Currency currency = new Currency(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("sign")
                );
                return Optional.of(currency);
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to read currency by code: " + code, e);
        }
    }
}

