package com.ced.umbrafell.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Cesar e Danilo
 */
public class TesteConexao {

    public static void main(String[] args) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            System.out.println("Conexão realizada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco:");
            System.out.println(e.getMessage());
        }
    }
}