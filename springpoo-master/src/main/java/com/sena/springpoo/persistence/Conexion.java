package com.sena.springpoo.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final Logger logger = LoggerFactory.getLogger(Conexion.class);

    public static Connection getConnection() {
        Connection conn = null;

        try {

            String url = "jdbc:mysql://localhost:3306/sena";
            String user = "root";
            String password = "";

            conn = DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            logger.error("[Conexion.getConnection] No se pudo conectar a la base de datos: {}", e.getMessage(), e);
        }

        return conn;
    }

}
