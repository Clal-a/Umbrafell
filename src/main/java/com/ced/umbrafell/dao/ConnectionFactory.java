package com.ced.umbrafell.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Cesar e Danilo
 */
public class ConnectionFactory {

    private static final String URL = "jdbc:postgresql://localhost:5432/umbrafell";
    private static final String USUARIO = "postgres";
    private static final String SENHA = "postgresql";

    private ConnectionFactory() {
        // Impede que a classe seja instanciada
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new SQLException("Erro ao conectar ao banco de dados Umbrafell: " + e.getMessage(), e);
        }
    }
}
