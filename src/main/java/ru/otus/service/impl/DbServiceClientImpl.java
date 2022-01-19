package ru.otus.service.impl;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.jdbc.mapper.DataTemplate;
import ru.otus.jdbc.mapper.TransactionRunner;
import ru.otus.model.Client;
import ru.otus.service.DBServiceClient;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(
        DbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionRunner transactionRunner;

    public DbServiceClientImpl(TransactionRunner transactionRunner, DataTemplate<Client> clientDataTemplate) {
        this.transactionRunner = transactionRunner;
        this.clientDataTemplate = clientDataTemplate;
    }

    @Override
    public Client saveClient(Client client) {


        return transactionRunner.doInTransaction(connection -> {
            if (client.getId() == null) {
                var clientId = clientDataTemplate.insert(connection, client);
                var createdClient = new Client(clientId, client.getName());
                log.info("created client: {}", createdClient);
                return createdClient;
            }
            clientDataTemplate.update(connection, client);
            log.info("updated client: {}", client);
            return client;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        return transactionRunner.doInTransaction(connection -> {
            var clientOptional = clientDataTemplate.findById(connection, id);
            log.info("client: {}", clientOptional);
            return clientOptional;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionRunner.doInTransaction(connection -> {
            var clientList = clientDataTemplate.findAll(connection);
            log.info("clientList:{}", clientList);
            return clientList;
       });
    }
}
