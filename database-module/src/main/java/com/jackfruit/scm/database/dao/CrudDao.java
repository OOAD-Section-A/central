package com.jackfruit.scm.database.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T, ID> {

    void save(T entity);

    void update(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();
}
