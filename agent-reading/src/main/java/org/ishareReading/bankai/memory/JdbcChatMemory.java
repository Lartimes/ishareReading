package org.ishareReading.bankai.memory;

import lombok.SneakyThrows;
import org.ishareReading.bankai.util.TokenCountUtil;
import org.ishareReading.bankai.utils.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class JdbcChatMemory implements ChatMemory, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(JdbcChatMemory.class);

    private static final String DEFAULT_TABLE_NAME = "messages";
    private final Connection connection;
    private final String tableName;

    protected JdbcChatMemory(String username, String password, String jdbcUrl) {
        this(username, password, jdbcUrl, DEFAULT_TABLE_NAME);
    }

    @SneakyThrows
    protected JdbcChatMemory(String username, String password, String jdbcUrl, String tableName) {
        this.tableName = tableName;
        this.connection = DriverManager.getConnection(jdbcUrl, username, password);
        checkAndCreateTable();
    }

    private void checkAndCreateTable() throws SQLException {
        String checkTableQuery = hasTableSql(tableName);
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(checkTableQuery)) {
            if (rs.next()) {
                logger.info("Table {} exists.", tableName);
            } else {
                logger.info("Table {} does not exist. Creating table...", tableName);
                createTable();
            }
        }
    }

    protected abstract String hasTableSql(String tableName);

    private void createTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSql(tableName));
        } catch (Exception e) {
            throw new RuntimeException("Error creating table " + tableName + " ", e);
        }
    }

    protected abstract String createTableSql(String tableName);

    protected JdbcChatMemory(Connection connection) {
        this(connection, DEFAULT_TABLE_NAME);
    }

    @SneakyThrows
    protected JdbcChatMemory(Connection connection, String tableName) {
        this.connection = connection;
        this.tableName = tableName;
        checkAndCreateTable();
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }


    @SneakyThrows
    @Override
    public void add(String sessionId, List<Message> messages) {
        logger.info("================custom 加入的消息:{}" , messages);
        for (Message message : messages) {
            String sql = "INSERT INTO " + tableName + " (id , session_id, role , content , token_count , create_at , actived) VALUES (?, ?, ? , ? , ?  , ? , ? )";
            try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
                stmt.setLong(1, IdUtil.getId());
                stmt.setLong(2, Long.parseLong(sessionId));
                String type = message.getMessageType().name();
                type.intern();
                stmt.setString(3, type);
                String text = message.getText();
                stmt.setString(4, text);
                int now = TokenCountUtil.countTokens(text);
                stmt.setInt(5, now);
                stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                stmt.setBoolean(7, isActived(type, now));
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public List<Message> get(String sessionId, int lastN) {
        return this.selectMessageByIdAndByActived(sessionId, lastN);
    }

    /**
     * 如果超出上下文token了，在进行重试处理actived字段
     */
    public List<Message> selectMessageByIdAndByActived(String sessionId, int lastN) {
        List<Message> totalMessage = new ArrayList<>();
//        StringBuilder sqlBuilder = new StringBuilder("SELECT content,role FROM ").append(tableName)
//                .append(" WHERE session_id = ? ORDER BY create_at DESC ");
        String sql = generatePaginatedQuerySql(tableName, lastN);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, Long.parseLong(sessionId));
            if (lastN > 0) {
                stmt.setInt(2, lastN);
            }
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                var content = resultSet.getString("content");
                String type = resultSet.getString("role");
                var message = switch (type) {
                    case "USER" -> new UserMessage(content);
                    case "ASSISTANT" -> new AssistantMessage(content);
//                    case MessageType.SYSTEM.name() -> new UserMessage(content);
                    case "SYSTEM" -> new SystemMessage(content);
                    default -> null;
                };

                totalMessage.add(message);
            }
            logger.info("=======================custom 加入上下文的记忆: {}", totalMessage.size());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return totalMessage;
    }

    //create table messages
//(
//    id          bigint      not null
//        constraint pk_messages
//            primary key,
//    session_id  bigint      not null,
//    role        varchar(20) not null,
//    content     text        not null,
//    token_count integer,
//    model       varchar(50),
//    create_at   timestamp,
//    update_at   timestamp,
//    delete_at   timestamp,
//    actived     boolean
//);
    protected String generatePaginatedQuerySql(String tableName, int lastN) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT content,role FROM ").append(tableName)
                .append(" WHERE session_id = ? ORDER BY create_at DESC ");

        if (lastN > 0) {
            sqlBuilder.append(" LIMIT ?");
        }

        return sqlBuilder.toString();
    }

    @Override
    public void clear(String sessionId) {
//        逻辑删除
        String sql = "update " + tableName + "set delete_at = ? WHERE session_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setString(2, sessionId);
            stmt.executeUpdate();
        } catch (Exception e) {
            logger.error("Error clearing messages from {} chat memory", jdbcType(), e);
            throw new RuntimeException("Error executing delete ", e);
        }

    }

    protected abstract String jdbcType();

    private boolean isActived(String type, int count) {
        if (type.equals(MessageType.ASSISTANT.name())) {
            return count > 100;
        } else if (type.equals(MessageType.SYSTEM.name())) {
            return count > 100;
        } else if (type.equals(MessageType.USER.name())) {
            return count > 20;
        } else if (type.equals(MessageType.TOOL.name())) {
            return count > 40;
        }
        return false;
    }

}
