package ru.otus.jdbc.mapper.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.List;
import ru.otus.exceptions.EntityConverterException;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntityConverter;

public class EntityConverterImpl<T> implements EntityConverter<T> {

  private final EntityClassMetaData<T> metaDataExtractor;

  public EntityConverterImpl(EntityClassMetaData<T> metaDataExtractor) {
    this.metaDataExtractor = metaDataExtractor;
  }

  @Override
  public T toEntity(ResultSet resultSet) {
    try {
      List<Field> fields = metaDataExtractor.getAllFields();

      if (!resultSet.next()) {
        return null;
      }

      Constructor<T> constructor = metaDataExtractor.getConstructor();
      T instance = constructor.newInstance();

      for (Field field : fields) {
        var value = resultSet.getObject(field.getName());
        field.setAccessible(true);
        field.set(instance, value);
      }
      return instance;
    } catch (Exception ex) {
      throw new EntityConverterException("Error during converting");
    }
  }
}
