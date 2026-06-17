package com.ced.umbrafell.dao;

import com.ced.umbrafell.model.Run;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RunDAO {

    public Run salvar(Run run) {
        String sql =
                "INSERT INTO runs (" +
                "id_jogador, " +
                "pontuacao, " +
                "fase_alcancada, " +
                "joias_sombrias_obtidas, " +
                "inimigos_derrotados, " +
                "resultado, " +
                "venceu_boss" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "RETURNING id_run";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, run.getPlayerId());
            stmt.setInt(2, run.getPontuacao());
            stmt.setInt(3, run.getFaseAlcancada());
            stmt.setInt(4, run.getJoiasSombriasObtidas());
            stmt.setInt(5, run.getInimigosDerrotados());
            stmt.setString(6, run.getResultado());
            stmt.setBoolean(7, run.isVenceuBoss());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                run.setId(rs.getInt("id_run"));
            }

            return run;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar run: " + e.getMessage(), e);
        }
    }

    public Run buscarPorId(int idRun) {
        String sql =
                "SELECT " +
                "id_run, " +
                "id_jogador, " +
                "pontuacao, " +
                "fase_alcancada, " +
                "joias_sombrias_obtidas, " +
                "inimigos_derrotados, " +
                "resultado, " +
                "venceu_boss " +
                "FROM runs " +
                "WHERE id_run = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idRun);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return montarRun(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar run por ID: " + e.getMessage(), e);
        }
    }

    public List<Run> listarPorJogador(int idJogador) {
        String sql =
                "SELECT " +
                "id_run, " +
                "id_jogador, " +
                "pontuacao, " +
                "fase_alcancada, " +
                "joias_sombrias_obtidas, " +
                "inimigos_derrotados, " +
                "resultado, " +
                "venceu_boss " +
                "FROM runs " +
                "WHERE id_jogador = ? " +
                "ORDER BY pontuacao DESC, id_run DESC";

        List<Run> runs = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idJogador);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                runs.add(montarRun(rs));
            }

            return runs;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar runs do jogador: " + e.getMessage(), e);
        }
    }

    public List<Run> listarTodas() {
        String sql =
                "SELECT " +
                "id_run, " +
                "id_jogador, " +
                "pontuacao, " +
                "fase_alcancada, " +
                "joias_sombrias_obtidas, " +
                "inimigos_derrotados, " +
                "resultado, " +
                "venceu_boss " +
                "FROM runs " +
                "ORDER BY pontuacao DESC, id_run DESC";

        List<Run> runs = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                runs.add(montarRun(rs));
            }

            return runs;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar runs: " + e.getMessage(), e);
        }
    }

    private Run montarRun(ResultSet rs) throws SQLException {
        Run run = new Run();

        run.setId(rs.getInt("id_run"));
        run.setPlayerId(rs.getInt("id_jogador"));
        run.setPontuacao(rs.getInt("pontuacao"));
        run.setFaseAlcancada(rs.getInt("fase_alcancada"));
        run.setJoiasSombriasObtidas(rs.getInt("joias_sombrias_obtidas"));
        run.setInimigosDerrotados(rs.getInt("inimigos_derrotados"));
        run.setResultado(rs.getString("resultado"));
        run.setVenceuBoss(rs.getBoolean("venceu_boss"));

        return run;
    }
}