package ru.otus.jdbc.mapper.impl;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import ru.otus.annotations.Id;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import ru.otus.exceptions.MetaDataExtractException;
import ru.otus.jdbc.mapper.DataTemplate;
import ru.otus.jdbc.mapper.DbExecutor;
import ru.otus.jdbc.mapper.EntityConverter;
import ru.otus.jdbc.mapper.EntitySQLMetaData;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityConverter<T> converter;

    public DataTemplateJdbc(
        DbExecutor dbExecutor,
        EntitySQLMetaData entitySQLMetaData,
        EntityConverter<T> converter) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.converter = converter;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(
            connection,
            entitySQLMetaData.getSelectByIdSql(),
            Collections.singletonList(id),
            converter::toEntity
        );
    }

    @Override
    public List<T> findAll(Connection connection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long insert(Connection connection, T client) {

        String insertSql = entitySQLMetaData.getInsertSql();

        return dbExecutor.executeStatement(
            connection,
            insertSql,
            getFieldValues(client)
        );
    }

    @Override
    public void update(Connection connection, T client) {

        String updateSql = entitySQLMetaData.getUpdateSql();

        dbExecutor.updateStatement(
            connection,
            updateSql,
            getIdFieldValue(client),
            getFieldValues(client));

    }

    private List<Object> getFieldValues(T client) {
        Field[] declaredFields = client.getClass().getDeclaredFields();

        List<Object> values = new ArrayList<>();
        try {
            for (Field field: declaredFields) {
                if (field.isAnnotationPresent(Id.class)) {
                    continue;
                }
                field.setAccessible(true);
                Object o = field.get(client);
                values.add(o);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return values;
    }

    private Long getIdFieldValue(T client) {

        Field[] declaredFields = client.getClass().getDeclaredFields();

        try {
            Long aLong = null;

            for (Field field: declaredFields) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    aLong = (Long) field.get(client);
                }
            }
            return aLong;
        } catch (Exception ex) {
            throw new MetaDataExtractException("Can't extract id-field value");
        }
    }
}
