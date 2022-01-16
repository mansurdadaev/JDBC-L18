package ru.otus.jdbc.mapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface DbExecutor {

    void updateStatement(Connection connection, String sql, Long id, List<Object> params);

    long executeStatement(Connection connection, String sql, List<Object> params);

    <T> Optional<T> executeSelect(Connection connection, String sql, List<Object> params, Function<ResultSet, T> rsHandler) ;
}
