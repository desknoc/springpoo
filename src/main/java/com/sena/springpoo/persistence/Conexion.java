package com.sena.springpoo.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    public static Connection getConnection() {
        Connection conn = null;

        try {

            String url = "jdbc:mysql://localhost:3306/sena";
            String user = "root";
            String password = "";

            conn = DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

}
