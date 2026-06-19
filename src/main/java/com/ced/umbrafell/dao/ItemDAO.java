package com.ced.umbrafell.dao;

import com.ced.umbrafell.model.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public List<Item> listarTodos() {
        String sql =
                "SELECT " +
                "id_item, " +
                "nome, " +
                "tipo, " +
                "descricao, " +
                "valor_joias_sombrias " +
                "FROM itens " +
                "ORDER BY valor_joias_sombrias ASC";

        List<Item> itens = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(montarItem(rs));
            }

            return itens;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens: " + e.getMessage(), e);
        }
    }

    public List<Item> listarPorTipo(String tipo) {
        String sql =
                "SELECT " +
                "id_item, " +
                "nome, " +
                "tipo, " +
                "descricao, " +
                "valor_joias_sombrias " +
                "FROM itens " +
                "WHERE tipo = ? " +
                "ORDER BY valor_joias_sombrias ASC";

        List<Item> itens = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, tipo);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(montarItem(rs));
            }

            return itens;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens por tipo: " + e.getMessage(), e);
        }
    }

    public Item buscarPorId(int idItem) {
        String sql =
                "SELECT " +
                "id_item, " +
                "nome, " +
                "tipo, " +
                "descricao, " +
                "valor_joias_sombrias " +
                "FROM itens " +
                "WHERE id_item = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idItem);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return montarItem(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item por ID: " + e.getMessage(), e);
        }
    }

    public Item buscarPorNome(String nome) {
        String sql =
                "SELECT " +
                "id_item, " +
                "nome, " +
                "tipo, " +
                "descricao, " +
                "valor_joias_sombrias " +
                "FROM itens " +
                "WHERE nome = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, nome);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return montarItem(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item por nome: " + e.getMessage(), e);
        }
    }

    public Item cadastrar(Item item) {
        String sql =
                "INSERT INTO itens (" +
                "nome, " +
                "tipo, " +
                "descricao, " +
                "valor_joias_sombrias" +
                ") VALUES (?, ?, ?, ?) " +
                "RETURNING id_item";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, item.getNome());
            stmt.setString(2, item.getTipo());
            stmt.setString(3, item.getDescricao());
            stmt.setInt(4, item.getValorEmJoiasSombrias());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                item.setId(rs.getInt("id_item"));
            }

            return item;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar item: " + e.getMessage(), e);
        }
    }

    public void atualizar(Item item) {
        String sql =
                "UPDATE itens SET " +
                "nome = ?, " +
                "tipo = ?, " +
                "descricao = ?, " +
                "valor_joias_sombrias = ? " +
                "WHERE id_item = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, item.getNome());
            stmt.setString(2, item.getTipo());
            stmt.setString(3, item.getDescricao());
            stmt.setInt(4, item.getValorEmJoiasSombrias());
            stmt.setInt(5, item.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item: " + e.getMessage(), e);
        }
    }

    public void excluir(int idItem) {
        String sql =
                "DELETE FROM itens " +
                "WHERE id_item = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idItem);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir item: " + e.getMessage(), e);
        }
    }

    private Item montarItem(ResultSet rs) throws SQLException {
        return new Item(
                rs.getInt("id_item"),
                rs.getString("nome"),
                rs.getString("tipo"),
                rs.getString("descricao"),
                rs.getInt("valor_joias_sombrias")
        );
    }
}
