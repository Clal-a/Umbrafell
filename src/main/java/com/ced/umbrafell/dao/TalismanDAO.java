package com.ced.umbrafell.dao;

import com.ced.umbrafell.model.Talisman;
import com.ced.umbrafell.model.InventarioItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cesar e Danilo
 */
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
        Talisman talisman = new Talisman(
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

        talisman.setIdTalisma(rs.getInt("id_talisma"));

        return talisman;
    }
    
    public boolean jogadorPossuiTalisma(int idJogador, int idTalisma) {
        String sql =
                "SELECT 1 " +
                "FROM jogador_talismas " +
                "WHERE id_jogador = ? " +
                "AND id_talisma = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idJogador);
            stmt.setInt(2, idTalisma);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar talismã do jogador: " + e.getMessage(), e);
        }
    }

    public boolean comprarParaJogador(int idJogador, Talisman talisman) {
        Connection connection = null;

        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            int idTalisma = talisman.getIdTalisma();
            int custo = talisman.getValorEmJoiasSombrias();

            if (jogadorPossuiTalismaComConexao(connection, idJogador, idTalisma)) {
                connection.rollback();
                return false;
            }

            boolean pagou = gastarJoiasComConexao(connection, idJogador, custo);

            if (!pagou) {
                connection.rollback();
                return false;
            }

            inserirTalismaJogador(connection, idJogador, idTalisma);

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Erro ao desfazer compra do talismã: " + ex.getMessage(), ex);
                }
            }

            throw new RuntimeException("Erro ao comprar talismã: " + e.getMessage(), e);

        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }

    private boolean jogadorPossuiTalismaComConexao(
            Connection connection,
            int idJogador,
            int idTalisma
    ) throws SQLException {
        String sql =
                "SELECT 1 " +
                "FROM jogador_talismas " +
                "WHERE id_jogador = ? " +
                "AND id_talisma = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idJogador);
            stmt.setInt(2, idTalisma);

            ResultSet rs = stmt.executeQuery();

            return rs.next();
        }
    }

    private boolean gastarJoiasComConexao(
            Connection connection,
            int idJogador,
            int custo
    ) throws SQLException {
        String sql =
                "UPDATE jogadores SET " +
                "joias_sombrias = joias_sombrias - ?, " +
                "atualizado_em = CURRENT_TIMESTAMP " +
                "WHERE id_jogador = ? " +
                "AND joias_sombrias >= ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, custo);
            stmt.setInt(2, idJogador);
            stmt.setInt(3, custo);

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;
        }
    }

    private void inserirTalismaJogador(
            Connection connection,
            int idJogador,
            int idTalisma
    ) throws SQLException {
        String sql =
                "INSERT INTO jogador_talismas (" +
                "id_jogador, " +
                "id_talisma" +
                ") VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idJogador);
            stmt.setInt(2, idTalisma);

            stmt.executeUpdate();
        }
    }
    
    public List<InventarioItem> listarInventarioDoJogador(int idJogador) {
        String sql =
                "SELECT " +
                "i.nome, " +
                "i.tipo, " +
                "i.descricao " +
                "FROM jogador_talismas jt " +
                "JOIN talismas t ON t.id_talisma = jt.id_talisma " +
                "JOIN itens i ON i.id_item = t.id_item " +
                "WHERE jt.id_jogador = ? " +
                "ORDER BY i.nome";

        List<InventarioItem> itens = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idJogador);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(
                        new InventarioItem(
                                "✦",
                                rs.getString("nome"),
                                rs.getString("descricao"),
                                rs.getString("tipo"),
                                1
                        )
                );
            }

            return itens;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar inventário do jogador: " + e.getMessage(), e);
        }
    }
}
