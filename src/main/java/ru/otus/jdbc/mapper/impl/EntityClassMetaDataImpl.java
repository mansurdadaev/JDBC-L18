package ru.otus.jdbc.mapper.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ru.otus.annotations.Id;
import ru.otus.exceptions.MetaDataExtractException;
import ru.otus.jdbc.mapper.EntityClassMetaData;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

  private Class<T> clazz;

  @Override
  public String getName() {
    return this.clazz.getSimpleName();
  }

  @Override
  public Constructor<T> getConstructor() {
    try {
      return this.clazz.getConstructor();
    } catch (Exception ex) {
      throw new MetaDataExtractException("Can't get constructor");
    }
  }

  @Override
  public Field getIdField() {

    Optional<Field> optionalField = Arrays.stream(this.clazz.getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(Id.class))
        .findFirst();

    if (optionalField.isPresent()) {
      return optionalField.get();
    }

    throw new MetaDataExtractException("Field annotated by Id not found");
  }

  @Override
  public List<Field> getAllFields() {
    List<Field> fieldList = Arrays.stream(this.clazz.getDeclaredFields())
        .collect(Collectors.toList());

    if (fieldList.isEmpty()) {
      throw new MetaDataExtractException("Any fields not found");
    }

    return fieldList;
  }

  @Override
  public List<Field> getFieldsWithoutId() {

    List<Field> fieldList = Arrays.stream(this.clazz.getDeclaredFields())
        .filter(field -> !field.isAnnotationPresent(Id.class))
        .collect(Collectors.toList());

    if (fieldList.isEmpty()) {
      throw new MetaDataExtractException("Any fields not found");
    }

    return fieldList;
  }

  public EntityClassMetaDataImpl(Class<T> clazz) {
    this.clazz = clazz;
  }

 }
