package com.ced.umbrafell.dao;

import com.ced.umbrafell.model.Upgrade;
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
public class UpgradeDAO {

    public List<Upgrade> listarTodos() {
        String sql =
                "SELECT " +
                "id_upgrade, " +
                "nome, " +
                "descricao, " +
                "atributo, " +
                "nivel_maximo, " +
                "custo_nivel_1, " +
                "custo_nivel_2, " +
                "custo_nivel_3, " +
                "custo_nivel_4, " +
                "custo_nivel_5, " +
                "incremento " +
                "FROM upgrades " +
                "ORDER BY id_upgrade";

        List<Upgrade> upgrades = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                upgrades.add(montarUpgrade(rs));
            }

            return upgrades;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar upgrades: " + e.getMessage(), e);
        }
    }

    public Upgrade buscarPorId(int idUpgrade) {
        String sql =
                "SELECT " +
                "id_upgrade, " +
                "nome, " +
                "descricao, " +
                "atributo, " +
                "nivel_maximo, " +
                "custo_nivel_1, " +
                "custo_nivel_2, " +
                "custo_nivel_3, " +
                "custo_nivel_4, " +
                "custo_nivel_5, " +
                "incremento " +
                "FROM upgrades " +
                "WHERE id_upgrade = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idUpgrade);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return montarUpgrade(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar upgrade por ID: " + e.getMessage(), e);
        }
    }

    public int buscarNivelDoJogador(int idJogador, int idUpgrade) {
        String sql =
                "SELECT nivel " +
                "FROM jogador_upgrades " +
                "WHERE id_jogador = ? " +
                "AND id_upgrade = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idJogador);
            stmt.setInt(2, idUpgrade);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("nivel");
            }

            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar nível do upgrade do jogador: " + e.getMessage(), e);
        }
    }

    public int calcularCustoProximoNivel(int idJogador, int idUpgrade) {
        Upgrade upgrade = buscarPorId(idUpgrade);

        if (upgrade == null) {
            return 0;
        }

        int nivelAtual = buscarNivelDoJogador(idJogador, idUpgrade);

        if (nivelAtual >= upgrade.getNivelMaximo()) {
            return 0;
        }

        int proximoNivel = nivelAtual + 1;

        return upgrade.getCustoPorNivel(proximoNivel);
    }

    public boolean comprarOuMelhorar(int idJogador, int idUpgrade) {
        Connection connection = null;

        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            Upgrade upgrade = buscarPorIdComConexao(connection, idUpgrade);

            if (upgrade == null) {
                connection.rollback();
                return false;
            }

            int nivelAtual = buscarNivelDoJogadorComConexao(connection, idJogador, idUpgrade);

            if (nivelAtual >= upgrade.getNivelMaximo()) {
                connection.rollback();
                return false;
            }

            int proximoNivel = nivelAtual + 1;
            int custo = upgrade.getCustoPorNivel(proximoNivel);

            boolean pagou = gastarJoiasComConexao(connection, idJogador, custo);

            if (!pagou) {
                connection.rollback();
                return false;
            }

            if (nivelAtual == 0) {
                inserirUpgradeJogador(connection, idJogador, idUpgrade, proximoNivel);
            } else {
                atualizarUpgradeJogador(connection, idJogador, idUpgrade, proximoNivel);
            }

            aplicarEfeitoNoJogador(connection, idJogador, upgrade);

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Erro ao desfazer compra do upgrade: " + ex.getMessage(), ex);
                }
            }

            throw new RuntimeException("Erro ao comprar/melhorar upgrade: " + e.getMessage(), e);

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

    private Upgrade buscarPorIdComConexao(Connection connection, int idUpgrade) throws SQLException {
        String sql =
                "SELECT " +
                "id_upgrade, " +
                "nome, " +
                "descricao, " +
                "atributo, " +
                "nivel_maximo, " +
                "custo_nivel_1, " +
                "custo_nivel_2, " +
                "custo_nivel_3, " +
                "custo_nivel_4, " +
                "custo_nivel_5, " +
                "incremento " +
                "FROM upgrades " +
                "WHERE id_upgrade = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUpgrade);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return montarUpgrade(rs);
            }

            return null;
        }
    }

    private int buscarNivelDoJogadorComConexao(Connection connection, int idJogador, int idUpgrade) throws SQLException {
        String sql =
                "SELECT nivel " +
                "FROM jogador_upgrades " +
                "WHERE id_jogador = ? " +
                "AND id_upgrade = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idJogador);
            stmt.setInt(2, idUpgrade);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("nivel");
            }

            return 0;
        }
    }

    private boolean gastarJoiasComConexao(Connection connection, int idJogador, int custo) throws SQLException {
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

    private void inserirUpgradeJogador(Connection connection, int idJogador, int idUpgrade, int nivel) throws SQLException {
        String sql =
                "INSERT INTO jogador_upgrades (" +
                "id_jogador, " +
                "id_upgrade, " +
                "nivel" +
                ") VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idJogador);
            stmt.setInt(2, idUpgrade);
            stmt.setInt(3, nivel);

            stmt.executeUpdate();
        }
    }

    private void atualizarUpgradeJogador(Connection connection, int idJogador, int idUpgrade, int nivel) throws SQLException {
        String sql =
                "UPDATE jogador_upgrades SET " +
                "nivel = ? " +
                "WHERE id_jogador = ? " +
                "AND id_upgrade = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, nivel);
            stmt.setInt(2, idJogador);
            stmt.setInt(3, idUpgrade);

            stmt.executeUpdate();
        }
    }

    private void aplicarEfeitoNoJogador(Connection connection, int idJogador, Upgrade upgrade) throws SQLException {
        String atributo = upgrade.getAtributo();
        double incremento = upgrade.getIncremento();

        if ("VIDA_MAXIMA".equals(atributo)) {
            String sql =
                    "UPDATE jogadores SET " +
                    "vida_maxima = vida_maxima + ?, " +
                    "vida_atual = vida_atual + ?, " +
                    "atualizado_em = CURRENT_TIMESTAMP " +
                    "WHERE id_jogador = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, (int) incremento);
                stmt.setInt(2, (int) incremento);
                stmt.setInt(3, idJogador);
                stmt.executeUpdate();
            }

        } else if ("DANO".equals(atributo)) {
            String sql =
                    "UPDATE jogadores SET " +
                    "dano = dano + ?, " +
                    "atualizado_em = CURRENT_TIMESTAMP " +
                    "WHERE id_jogador = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, (int) incremento);
                stmt.setInt(2, idJogador);
                stmt.executeUpdate();
            }

        } else if ("VELOCIDADE".equals(atributo)) {
            String sql =
                    "UPDATE jogadores SET " +
                    "velocidade = velocidade + ?, " +
                    "atualizado_em = CURRENT_TIMESTAMP " +
                    "WHERE id_jogador = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setDouble(1, incremento);
                stmt.setInt(2, idJogador);
                stmt.executeUpdate();
            }

        } else if ("DEFESA".equals(atributo)) {
            String sql =
                    "UPDATE jogadores SET " +
                    "defesa = defesa + ?, " +
                    "atualizado_em = CURRENT_TIMESTAMP " +
                    "WHERE id_jogador = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, (int) incremento);
                stmt.setInt(2, idJogador);
                stmt.executeUpdate();
            }

        } else if ("ATAQUE_PRINCIPAL".equals(atributo)) {
            String sql =
                    "UPDATE jogadores SET " +
                    "ataque_principal_nivel = ataque_principal_nivel + 1, " +
                    "atualizado_em = CURRENT_TIMESTAMP " +
                    "WHERE id_jogador = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, idJogador);
                stmt.executeUpdate();
            }

        } else if ("ATAQUE_SECUNDARIO".equals(atributo)) {
            String sql =
                    "UPDATE jogadores SET " +
                    "ataque_secundario_nivel = ataque_secundario_nivel + 1, " +
                    "atualizado_em = CURRENT_TIMESTAMP " +
                    "WHERE id_jogador = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, idJogador);
                stmt.executeUpdate();
            }
        }
    }

    private Upgrade montarUpgrade(ResultSet rs) throws SQLException {
        Upgrade upgrade = new Upgrade();

        upgrade.setId(rs.getInt("id_upgrade"));
        upgrade.setNome(rs.getString("nome"));
        upgrade.setDescricao(rs.getString("descricao"));
        upgrade.setAtributo(rs.getString("atributo"));
        upgrade.setNivelMaximo(rs.getInt("nivel_maximo"));
        upgrade.setCustoNivel1(rs.getInt("custo_nivel_1"));
        upgrade.setCustoNivel2(rs.getInt("custo_nivel_2"));
        upgrade.setCustoNivel3(rs.getInt("custo_nivel_3"));
        upgrade.setCustoNivel4(rs.getInt("custo_nivel_4"));
        upgrade.setCustoNivel5(rs.getInt("custo_nivel_5"));
        upgrade.setIncremento(rs.getDouble("incremento"));

        return upgrade;
    }
}