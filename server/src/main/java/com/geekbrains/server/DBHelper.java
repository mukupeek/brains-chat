package com.geekbrains.server;

import javax.xml.transform.Result;
import java.sql.*;

public class DBHelper implements AutoCloseable {
    private static DBHelper instance;
    private static Connection connection;

    private DBHelper() {

    }

    public static DBHelper getInstance() {
        if (instance == null) {
            loadDriverAndOpenConnection();
            instance = new DBHelper();
        }
        return instance;
    }

    private static void loadDriverAndOpenConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Ошибка открытия соединения с базой данных!");
            e.printStackTrace();
        }
    }

    public String findByLoginAndPassword(String login, String password) {
        String query = String.format("SELECT * FROM participant WHERE LOWER(login)=LOWER(\"%s\") AND password = \"%s\"", login, password);
        System.out.println(query);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int updateNickname(String oldNickname, String newNickname) {
        String query = String.format("UPDATE participant SET nickname=\"%s\" WHERE nickname=\"%s\"", newNickname, oldNickname);

        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
