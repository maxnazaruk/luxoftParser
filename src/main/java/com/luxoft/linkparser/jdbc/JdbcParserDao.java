package com.luxoft.linkparser.jdbc;

import com.luxoft.linkparser.entity.Link;
import com.luxoft.linkparser.jdbc.mapper.LinkMapper;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcParserDao {
    private static final LinkMapper LINK_MAPPER = new LinkMapper();

    private static final String FIND_ALL = "SELECT id, url, depth, number FROM Links ORDER BY id ASC;";
    private static final String INSERT_INTO = "INSERT INTO Links (url, depth, number) VALUES (?, ?, ?)";
    private static final String CLEAR_TABLE = "DELETE FROM Links;";
    private static final String RESET_SEQUENCE = "ALTER SEQUENCE links_id_seq RESTART WITH 1;";
    private static final String CREATE_TABLE = "CREATE TABLE links (id SERIAL, url varchar(255), depth int, number int);";
    private static final String DROP_TABLE = "DROP TABLE Links;";

    private Connection getConnection() throws SQLException, PSQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/links",
                "user", "pswd");
    }

    public List<Link> findAll() throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<Link> links = new ArrayList<>();
            while (resultSet.next()) {
                Link link = LINK_MAPPER.mapResultSet(resultSet);
                links.add(link);
            }
            return links;
        }
    }

    public void add(Link link) {
        if(link != null) {
            if (!checkTableExists()) {
                createLinksTable();
            }
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO)) {
                preparedStatement.setString(1, link.getUrl());
                preparedStatement.setInt(2, link.getDepth());
                preparedStatement.setInt(3, link.getNumber());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearTable() throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CLEAR_TABLE);
             PreparedStatement resetStatement = connection.prepareStatement(RESET_SEQUENCE)) {
            preparedStatement.executeUpdate();
            resetStatement.executeUpdate();
        }
    }

    // Extend this method to public for test purpose
    public void createLinksTable() {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TABLE)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Extend this method to public for test purpose
    public  boolean checkTableExists() {
        try (Connection connection = getConnection();
             ) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, "links", null);
            if(resultSet.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Extend this method to public for test purpose
    public void dropTable() {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DROP_TABLE)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
