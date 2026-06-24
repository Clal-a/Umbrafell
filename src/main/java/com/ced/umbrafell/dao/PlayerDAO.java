package com.ced.umbrafell.dao;

import com.ced.umbrafell.model.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Cesar e Danilo
 */
public class PlayerDAO {

    public Player cadastrar(Player player) {
        String sql =
                "INSERT INTO jogadores (" +
                "nome, " +
                "vida_maxima, " +
                "vida_atual, " +
                "dano, " +
                "defesa, " +
                "velocidade, " +
                "ataque_principal_nivel, " +
                "ataque_secundario_nivel, " +
                "joias_sombrias, " +
                "fase_atual" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "RETURNING id_jogador";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, player.getNome());
            stmt.setInt(2, player.getVidaMaxima());
            stmt.setInt(3, player.getVidaAtual());
            stmt.setInt(4, player.getDano());
            stmt.setInt(5, player.getDefesa());
            stmt.setDouble(6, player.getVelocidade());
            stmt.setInt(7, player.getAtaquePrincipalNivel());
            stmt.setInt(8, player.getAtaqueSecundarioNivel());
            stmt.setInt(9, player.getJoiasSombrias());
            stmt.setInt(10, player.getFaseAtual());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                player.setId(rs.getInt("id_jogador"));
            }

            return player;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar jogador: " + e.getMessage(), e);
        }
    }

    public Player buscarPorId(int idJogador) {
        String sql =
                "SELECT " +
                "id_jogador, " +
                "nome, " +
                "vida_maxima, " +
                "vida_atual, " +
                "dano, " +
                "defesa, " +
                "velocidade, " +
                "ataque_principal_nivel, " +
                "ataque_secundario_nivel, " +
                "joias_sombrias, " +
                "fase_atual " +
                "FROM jogadores " +
                "WHERE id_jogador = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idJogador);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return montarPlayer(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar jogador por ID: " + e.getMessage(), e);
        }
    }

    public Player buscarPorNome(String nome) {
        String sql =
                "SELECT " +
                "id_jogador, " +
                "nome, " +
                "vida_maxima, " +
                "vida_atual, " +
                "dano, " +
                "defesa, " +
                "velocidade, " +
                "ataque_principal_nivel, " +
                "ataque_secundario_nivel, " +
                "joias_sombrias, " +
                "fase_atual " +
                "FROM jogadores " +
                "WHERE nome = ? " +
                "ORDER BY id_jogador DESC " +
                "LIMIT 1";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, nome);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return montarPlayer(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar jogador por nome: " + e.getMessage(), e);
        }
    }

    public void atualizar(Player player) {
        String sql =
                "UPDATE jogadores SET " +
                "nome = ?, " +
                "vida_maxima = ?, " +
                "vida_atual = ?, " +
                "dano = ?, " +
                "defesa = ?, " +
                "velocidade = ?, " +
                "ataque_principal_nivel = ?, " +
                "ataque_secundario_nivel = ?, " +
                "joias_sombrias = ?, " +
                "fase_atual = ?, " +
                "atualizado_em = CURRENT_TIMESTAMP " +
                "WHERE id_jogador = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, player.getNome());
            stmt.setInt(2, player.getVidaMaxima());
            stmt.setInt(3, player.getVidaAtual());
            stmt.setInt(4, player.getDano());
            stmt.setInt(5, player.getDefesa());
            stmt.setDouble(6, player.getVelocidade());
            stmt.setInt(7, player.getAtaquePrincipalNivel());
            stmt.setInt(8, player.getAtaqueSecundarioNivel());
            stmt.setInt(9, player.getJoiasSombrias());
            stmt.setInt(10, player.getFaseAtual());
            stmt.setInt(11, player.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar jogador: " + e.getMessage(), e);
        }
    }

    public void adicionarJoiasSombrias(int idJogador, int quantidade) {
        String sql =
                "UPDATE jogadores SET " +
                "joias_sombrias = joias_sombrias + ?, " +
                "atualizado_em = CURRENT_TIMESTAMP " +
                "WHERE id_jogador = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            stmt.setInt(2, idJogador);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar Joias Sombrias: " + e.getMessage(), e);
        }
    }

    public boolean gastarJoiasSombrias(int idJogador, int quantidade) {
        String sql =
                "UPDATE jogadores SET " +
                "joias_sombrias = joias_sombrias - ?, " +
                "atualizado_em = CURRENT_TIMESTAMP " +
                "WHERE id_jogador = ? " +
                "AND joias_sombrias >= ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            stmt.setInt(2, idJogador);
            stmt.setInt(3, quantidade);

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gastar Joias Sombrias: " + e.getMessage(), e);
        }
    }

    public void atualizarFaseAtual(int idJogador, int faseAtual) {
        String sql =
                "UPDATE jogadores SET " +
                "fase_atual = ?, " +
                "atualizado_em = CURRENT_TIMESTAMP " +
                "WHERE id_jogador = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, faseAtual);
            stmt.setInt(2, idJogador);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar fase atual do jogador: " + e.getMessage(), e);
        }
    }

    private Player montarPlayer(ResultSet rs) throws SQLException {
        Player player = new Player();

        player.setId(rs.getInt("id_jogador"));
        player.setNome(rs.getString("nome"));
        player.setVidaMaxima(rs.getInt("vida_maxima"));
        player.setVidaAtual(rs.getInt("vida_atual"));
        player.setDano(rs.getInt("dano"));
        player.setDefesa(rs.getInt("defesa"));
        player.setVelocidade(rs.getDouble("velocidade"));
        player.setAtaquePrincipalNivel(rs.getInt("ataque_principal_nivel"));
        player.setAtaqueSecundarioNivel(rs.getInt("ataque_secundario_nivel"));
        player.setJoiasSombrias(rs.getInt("joias_sombrias"));
        player.setFaseAtual(rs.getInt("fase_atual"));

        return player;
    }
}