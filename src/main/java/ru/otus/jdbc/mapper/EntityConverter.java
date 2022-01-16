package ru.otus.jdbc.mapper;

import java.sql.ResultSet;

public interface EntityConverter<T> {

  T toEntity(ResultSet resultSet);
}
