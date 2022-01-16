package ru.otus.jdbc.mapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DataTemplate<T> {

    Optional<T> findById(Connection connection, long id);

    List<T> findAll(Connection connection);

    long insert(Connection connection, T object);

    void update(Connection connection, T object);
}
