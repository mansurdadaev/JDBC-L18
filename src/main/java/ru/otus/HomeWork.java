package ru.otus;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.datasource.DriverManagerDataSource;
import ru.otus.jdbc.mapper.impl.DataTemplateJdbc;
import ru.otus.jdbc.mapper.impl.DbExecutorImpl;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.impl.EntityClassMetaDataImpl;
import ru.otus.jdbc.mapper.EntityConverter;
import ru.otus.jdbc.mapper.impl.EntityConverterImpl;
import ru.otus.jdbc.mapper.EntitySQLMetaData;
import ru.otus.jdbc.mapper.impl.EntitySQLMetaDataImpl;
import ru.otus.jdbc.mapper.impl.TransactionRunnerJdbc;
import ru.otus.model.Client;
import ru.otus.model.Manager;
import ru.otus.service.impl.DbServiceClientImpl;
import ru.otus.service.impl.DbServiceManagerImpl;

public class HomeWork {

  private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
  private static final String USER = "usr";
  private static final String PASSWORD = "pwd";

  private static final Logger log = LoggerFactory.getLogger(HomeWork.class);

  public static void main(String[] args) {
// Общая часть
    var dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
    flywayMigrations(dataSource);
    var transactionRunner = new TransactionRunnerJdbc(dataSource);
    var dbExecutor = new DbExecutorImpl();

// Работа с клиентом
    EntityClassMetaData<Client> clientClassMetaDataExtractor = new EntityClassMetaDataImpl<>(Client.class);
    EntitySQLMetaData clientSQLFactory = new EntitySQLMetaDataImpl<>(clientClassMetaDataExtractor);
    EntityConverter<Client> clientConverter = new EntityConverterImpl<>(clientClassMetaDataExtractor);
    var dataTemplateClient = new DataTemplateJdbc<Client>(dbExecutor, clientSQLFactory, clientConverter); //реализация DataTemplate, универсальная

// Код дальше должен остаться
    var dbServiceClient = new DbServiceClientImpl(transactionRunner, dataTemplateClient);
    Client dbServiceFirst = dbServiceClient.saveClient(new Client("dbServiceFirst"));

    var clientSecond = dbServiceClient.saveClient(new Client("dbServiceSecond"));

    dbServiceClient.saveClient(new Client(1L, "First"));

    var clientSecondSelected = dbServiceClient.getClient(clientSecond.getId())
        .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
    log.info("clientSecondSelected:{}", clientSecondSelected);

// Сделайте тоже самое с классом Manager (для него надо сделать свою таблицу)

    EntityClassMetaData<Manager> managerClassMetaDataExtractor = new EntityClassMetaDataImpl<>(Manager.class);
    EntitySQLMetaData managerSQLFactory = new EntitySQLMetaDataImpl<>(managerClassMetaDataExtractor);
    EntityConverter<Manager> managerConverter = new EntityConverterImpl<>(managerClassMetaDataExtractor);
    var dataTemplateManager = new DataTemplateJdbc<Manager>(dbExecutor, managerSQLFactory, managerConverter);

    var dbServiceManager = new DbServiceManagerImpl(transactionRunner, dataTemplateManager);
        dbServiceManager.saveManager(new Manager("ManagerFirst"));

        var managerSecond = dbServiceManager.saveManager(new Manager("ManagerSecond"));
        var managerSecondSelected = dbServiceManager.getManager(managerSecond.getNo())
                .orElseThrow(() -> new RuntimeException("Manager not found, id:" + managerSecond.getNo()));
        log.info("managerSecondSelected:{}", managerSecondSelected);
  }

  private static void flywayMigrations(DataSource dataSource) {

    log.info("db migration started...");

    var flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:/db/migration")
        .load();

    flyway.migrate();
    log.info("db migration finished.");
    log.info("***");
  }
}
