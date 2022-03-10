package net.millida.storage.mysql;

import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class MysqlConnection {

    private MysqlDataSource dataSource = new MysqlDataSource();

    @Getter
    private Connection connection;

    /**
     * Инициализация и создание MySQL-Соединения
     *
     * @param host - Хост.
     * @param port - Порт.
     * @param username - Имя пользователя.
     * @param password - Пароль.
     * @param database - База данных, схема.
     * @param tables - Таблицы с их аргументами, которые нужно создать
     *               при условии, что их не существует.
     */
    private MysqlConnection(String host, int port, String username, String password, String database,
                            Map<String, String> tables) {

        dataSource.setServerName(host);
        dataSource.setPort(port);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setDatabaseName(database);

        try {
            dataSource.setAutoReconnect(true);

            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        MysqlExecutor.getExecutor(this).execute(true, String.format("CREATE DATABASE IF NOT EXISTS `%s`", database));

        for (String table : tables.keySet()) {
            String value = tables.get(table);
            String query = String.format("CREATE TABLE IF NOT EXISTS `%s` (%s)", table, value);

            MysqlExecutor.getExecutor(this).execute(true, query);
        }
    }

    /**
     * Получение класса с запросами
     * в MySQL
     */
    public MysqlExecutor getExecutor() {
        return MysqlExecutor.getExecutor(this);
    }

    /**
     * Обновление и восстановление соединения
     * при его закрытии
     */
    public void refreshConnection() {
        try {
            if (connection != null && !connection.isClosed() && connection.isValid(1000)) {
                return;
            }
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Все нахуй обосралось с MySql печаль тоска");
        }
    }


    /**
     * Вызов нового билдера соединения с MySQL
     */
    public static SQLBuilder newBuilder() {
        return new SQLBuilder();
    }

    public static class SQLBuilder {


        private String host = "localhost",
                password = "",
                username = "root",
                database = "mysql";

        private int port = 3306;

        private Map<String, String> tables = new HashMap<>();


        @Getter
        private static final Map<String, MysqlConnection> databases = new HashMap<>();


        /**
         * Инициализация базы данных, схемы
         *
         * @param database - База данных, схема
         */
        public SQLBuilder setDatabase(String database) {
            this.database = database;

            return this;
        }

        /**
         * Инициализация пароля
         *
         * @param password - Пароль
         */
        public SQLBuilder setPassword(String password) {
            this.password = password;

            return this;
        }

        /**
         * Инициализация порта
         *
         * @param port - Порт
         */
        public SQLBuilder setPort(int port) {
            this.port = port;

            return this;
        }

        /**
         * Инициализация хоста
         *
         * @param host - Хост
         */
        public SQLBuilder setHost(String host) {
            this.host = host;

            return this;
        }

        /**
         * Инициализация имени пользователя
         *
         * @param username - Имя пользователя
         */
        public SQLBuilder setUsername(String username) {
            this.username = username;

            return this;
        }

        /**
         * Создание таблицы
         *
         * @param table - Имя таблицы
         * @param value - Аргументы табллицы
         */
        public SQLBuilder createTable(String table, String value) {
            this.tables.put(table, value);

            return this;
        }

        public MysqlConnection build() {
            MysqlConnection connection = databases.getOrDefault(database, null);

            if (connection == null) {
                connection = new MysqlConnection(this.host, this.port, this.username,
                        this.password, this.database, this.tables);
                databases.put(database, connection);

            }
            return connection;
        }
    }

}
