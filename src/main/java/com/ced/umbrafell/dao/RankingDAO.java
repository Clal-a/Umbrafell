package com.ced.umbrafell.dao;

import com.ced.umbrafell.model.RankingEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RankingDAO {

    public List<RankingEntry> listarTop10() {
        String sql =
                "SELECT " +
                "posicao, " +
                "jogador, " +
                "pontuacao, " +
                "fase_alcancada, " +
                "joias_sombrias_obtidas, " +
                "inimigos_derrotados, " +
                "resultado " +
                "FROM vw_ranking " +
                "LIMIT 10";

        List<RankingEntry> ranking = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ranking.add(montarRankingEntry(rs));
            }

            return ranking;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ranking: " + e.getMessage(), e);
        }
    }

    public List<RankingEntry> listarTodos() {
        String sql =
                "SELECT " +
                "posicao, " +
                "jogador, " +
                "pontuacao, " +
                "fase_alcancada, " +
                "joias_sombrias_obtidas, " +
                "inimigos_derrotados, " +
                "resultado " +
                "FROM vw_ranking";

        List<RankingEntry> ranking = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ranking.add(montarRankingEntry(rs));
            }

            return ranking;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ranking completo: " + e.getMessage(), e);
        }
    }

    private RankingEntry montarRankingEntry(ResultSet rs) throws SQLException {
        RankingEntry entry = new RankingEntry();

        entry.setPosicao(rs.getInt("posicao"));
        entry.setJogador(rs.getString("jogador"));
        entry.setPontuacao(rs.getInt("pontuacao"));
        entry.setFaseAlcancada(rs.getInt("fase_alcancada"));
        entry.setJoiasSombriasObtidas(rs.getInt("joias_sombrias_obtidas"));
        entry.setInimigosDerrotados(rs.getInt("inimigos_derrotados"));
        entry.setResultado(rs.getString("resultado"));

        return entry;
    }
}