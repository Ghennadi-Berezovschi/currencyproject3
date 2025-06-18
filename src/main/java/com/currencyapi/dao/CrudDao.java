package com.currencyapi.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    T save(T item);     //addCurrency
    T update(T item);
    boolean delete(ID id);
}

