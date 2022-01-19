package ru.otus.jdbc.mapper.impl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntitySQLMetaData;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {

  private final EntityClassMetaData<T> metaDataExtractor;

  public EntitySQLMetaDataImpl(EntityClassMetaData<T> metaDataExtractor) {
    this.metaDataExtractor = metaDataExtractor;
  }

  @Override
  public String getSelectAllSql() {
    String tableName = metaDataExtractor.getName();
    return String.format("SELECT * FROM %s", tableName);
  }

  @Override
  public String getSelectByIdSql() {
    String tableName = metaDataExtractor.getName();
    String idFieldName = metaDataExtractor.getIdField().getName();
    return String.format("SELECT * FROM %s WHERE %s = ?", tableName, idFieldName);
  }

  @Override
  public String getInsertSql() {

    String tableName = metaDataExtractor.getName();

    List<Field> fieldsWithoutId = metaDataExtractor.getFieldsWithoutId();

    String columnNames = fieldsWithoutId.stream()
        .map(Field::getName)
        .collect(Collectors.joining(", "));

    String valueParams = Stream
        .generate(() -> "?")
        .limit(fieldsWithoutId.size())
        .collect(Collectors.joining(", "));

    return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnNames, valueParams);
  }

  @Override
  public String getUpdateSql() {

    String tableName = metaDataExtractor.getName();

    List<Field> fieldsWithoutId = metaDataExtractor.getFieldsWithoutId();

    String idFieldName = metaDataExtractor.getIdField().getName();

    String settableFields = fieldsWithoutId.stream()
        .map(it -> String.format("%s = ?", it.getName()))
        .collect(Collectors.joining(", "));

    return String.format("UPDATE %s SET %s WHERE %s = ?", tableName, settableFields, idFieldName);
  }

}
