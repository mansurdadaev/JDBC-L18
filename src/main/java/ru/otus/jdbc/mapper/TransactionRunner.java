package ru.otus.jdbc.mapper;

public interface TransactionRunner {

    <T> T doInTransaction(TransactionAction<T> action);
}
