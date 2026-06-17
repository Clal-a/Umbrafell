package com.ced.umbrafell.dao;

import com.ced.umbrafell.model.Talisman;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TalismanDAO {

    public List<Talisman> listarTodos() {
        String sql =
                "SELECT " +
                "t.id_talisma, " +
                "i.id_item, " +
                "i.nome, " +
                "i.tipo, " +
                "i.descricao, " +
                "i.valor_joias_sombrias, " +
                "t.atributo_buff_1, " +
                "t.valor_buff_1, " +
                "t.atributo_buff_2, " +
                "t.valor_buff_2, " +
                "t.atributo_debuff, " +
                "t.valor_debuff " +
                "FROM talismas t " +
                "JOIN itens i ON i.id_item = t.id_item " +
                "ORDER BY i.valor_joias_sombrias ASC";

        List<Talisman> talismas = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                talismas.add(montarTalisman(rs));
            }

            return talismas;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar talismãs: " + e.getMessage(), e);
        }
    }

    public Talisman buscarPorIdTalisma(int idTalisma) {
        String sql =
                "SELECT " +
                "t.id_talisma, " +
                "i.id_item, " +
                "i.nome, " +
                "i.tipo, " +
                "i.descricao, " +
                "i.valor_joias_sombrias, " +
                "t.atributo_buff_1, " +
                "t.valor_buff_1, " +
                "t.atributo_buff_2, " +
                "t.valor_buff_2, " +
                "t.atributo_debuff, " +
                "t.valor_debuff " +
                "FROM talismas t " +
                "JOIN itens i ON i.id_item = t.id_item " +
                "WHERE t.id_talisma = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idTalisma);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return montarTalisman(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar talismã por ID: " + e.getMessage(), e);
        }
    }

    public Talisman buscarPorIdItem(int idItem) {
        String sql =
                "SELECT " +
                "t.id_talisma, " +
                "i.id_item, " +
                "i.nome, " +
                "i.tipo, " +
                "i.descricao, " +
                "i.valor_joias_sombrias, " +
                "t.atributo_buff_1, " +
                "t.valor_buff_1, " +
                "t.atributo_buff_2, " +
                "t.valor_buff_2, " +
                "t.atributo_debuff, " +
                "t.valor_debuff " +
                "FROM talismas t " +
                "JOIN itens i ON i.id_item = t.id_item " +
                "WHERE i.id_item = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idItem);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return montarTalisman(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar talismã por item: " + e.getMessage(), e);
        }
    }

    public Talisman buscarPorNome(String nome) {
        String sql =
                "SELECT " +
                "t.id_talisma, " +
                "i.id_item, " +
                "i.nome, " +
                "i.tipo, " +
                "i.descricao, " +
                "i.valor_joias_sombrias, " +
                "t.atributo_buff_1, " +
                "t.valor_buff_1, " +
                "t.atributo_buff_2, " +
                "t.valor_buff_2, " +
                "t.atributo_debuff, " +
                "t.valor_debuff " +
                "FROM talismas t " +
                "JOIN itens i ON i.id_item = t.id_item " +
                "WHERE i.nome = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, nome);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return montarTalisman(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar talismã por nome: " + e.getMessage(), e);
        }
    }

    private Talisman montarTalisman(ResultSet rs) throws SQLException {
        return new Talisman(
                rs.getString("atributo_buff_1"),
                rs.getDouble("valor_buff_1"),
                rs.getString("atributo_buff_2"),
                rs.getDouble("valor_buff_2"),
                rs.getString("atributo_debuff"),
                rs.getDouble("valor_debuff"),
                rs.getInt("id_item"),
                rs.getString("nome"),
                rs.getString("tipo"),
                rs.getString("descricao"),
                rs.getInt("valor_joias_sombrias")
        );
    }
}
