package org.ishareReading.bankai.service.memory;

import java.sql.Connection;

public class PostgresChatMemory extends JdbcChatMemory {

    private static final String JDBC_TYPE = "postgresql";

    public PostgresChatMemory(String username, String password, String url) {
        super(username, password, url);
    }

    public PostgresChatMemory(String username, String password, String jdbcUrl, String tableName) {
        super(username, password, jdbcUrl, tableName);
    }

    public PostgresChatMemory(Connection connection) {
        super(connection);
    }

    public PostgresChatMemory(Connection connection, String tableName) {
        super(connection, tableName);
    }

    @Override
    protected String hasTableSql(String tableName) {
        return String.format(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE' AND table_name = '%s'",
                tableName);
    }

    @Override
    protected String createTableSql(String tableName) {
        return String.format("""
                        create table %s
                        (
                            id          bigint      not null primary key,
                            session_id  bigint      not null,
                            role        varchar(20) not null,
                            content     text        not null,
                            token_count integer,
                            model       varchar(50),
                            create_at   timestamp,
                            update_at   timestamp,
                            delete_at   timestamp,
                            actived     boolean
                        );
                        """,
                tableName);
    }

    @Override
    protected String jdbcType() {
        return JDBC_TYPE;
    }

}
