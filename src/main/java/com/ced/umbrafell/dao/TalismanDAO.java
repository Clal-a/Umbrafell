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
                "t.id_item, " +
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

    public Talisman buscarPorId(int idTalisma) {
        String sql =
                "SELECT " +
                "t.id_talisma, " +
                "t.id_item, " +
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

    public Talisman buscarPorItemId(int idItem) {
        String sql =
                "SELECT " +
                "t.id_talisma, " +
                "t.id_item, " +
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
                "WHERE t.id_item = ?";

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
                "t.id_item, " +
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
        Talisman talisman = new Talisman();

        talisman.setIdTalisma(rs.getInt("id_talisma"));
        talisman.setIdItem(rs.getInt("id_item"));

        talisman.setNome(rs.getString("nome"));
        talisman.setTipo(rs.getString("tipo"));
        talisman.setDescricao(rs.getString("descricao"));
        talisman.setValorJoiasSombrias(rs.getInt("valor_joias_sombrias"));

        talisman.setAtributoBuff1(rs.getString("atributo_buff_1"));
        talisman.setValorBuff1(rs.getDouble("valor_buff_1"));

        talisman.setAtributoBuff2(rs.getString("atributo_buff_2"));
        talisman.setValorBuff2(rs.getDouble("valor_buff_2"));

        talisman.setAtributoDebuff(rs.getString("atributo_debuff"));
        talisman.setValorDebuff(rs.getDouble("valor_debuff"));

        return talisman;
    }
}