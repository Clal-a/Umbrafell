package com.ced.umbrafell.controller;

import com.ced.umbrafell.dao.PlayerDAO;
import com.ced.umbrafell.dao.TalismanDAO;
import com.ced.umbrafell.dao.UpgradeDAO;

import com.ced.umbrafell.model.Player;
import com.ced.umbrafell.model.Talisman;
import com.ced.umbrafell.model.Upgrade;

import com.ced.umbrafell.util.AlertUtil;

import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Cesar e Danilo
 */
public class BaseController {

    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblJogador;

    @FXML
    private Label lblJoias;

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblMensagem;

    @FXML
    private FlowPane flowUpgrades;

    @FXML
    private FlowPane flowTalismas;

    @FXML
    private Label lblDetalheTitulo;

    @FXML
    private Label lblDetalheTexto;

    @FXML
    private Label lblDetalheTalismaTitulo;

    @FXML
    private Label lblDetalheTalismaTexto;

    @FXML
    private Button btnComprarUpgrade;

    @FXML
    private Button btnComprarTalisma;

    private Player player;
    private int proximaFase;

    private Upgrade upgradeSelecionado;
    private Talisman talismanSelecionado;

    private final PlayerDAO playerDAO = new PlayerDAO();
    private final UpgradeDAO upgradeDAO = new UpgradeDAO();
    private final TalismanDAO talismanDAO = new TalismanDAO();

    public void setDados(Player player, int proximaFase) {
        this.player = player;
        this.proximaFase = proximaFase;

        carregarDadosTela();
        carregarCardsUpgrades();
        carregarCardsTalismas();

        lblMensagem.setText("Escolha sua preparação para a próxima fase.");

        lblDetalheTitulo.setText("Nenhum upgrade selecionado");
        lblDetalheTexto.setText("Selecione um card para ver os detalhes.");

        lblDetalheTalismaTitulo.setText("Nenhum talismã selecionado");
        lblDetalheTalismaTexto.setText("Selecione um card para ver os detalhes.");
    }

    private void carregarDadosTela() {
        if (player == null) {
            return;
        }

        lblTitulo.setText("Base de Umbrafell — Próxima fase: " + proximaFase);
        lblJogador.setText("Jogador: " + player.getNome());
        lblJoias.setText("Jóias Sombrias: " + player.getJoiasSombrias());

        lblStatus.setText(
                "Vida: " + player.getVidaAtual() + "/" + player.getVidaMaxima()
                + " | Dano: " + player.getDano()
                + " | Defesa: " + player.getDefesa()
                + " | Velocidade: " + player.getVelocidade()
                + " | Ataque principal: Nv. " + player.getAtaquePrincipalNivel()
                + " | Ataque secundário: Nv. " + player.getAtaqueSecundarioNivel()
        );
    }

    private void carregarCardsUpgrades() {
        try {
            flowUpgrades.getChildren().clear();

            List<Upgrade> upgrades = upgradeDAO.listarTodos();

            for (Upgrade upgrade : upgrades) {
                VBox card = criarCardUpgrade(upgrade);
                flowUpgrades.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblMensagem.setText("Erro ao carregar upgrades.");
        }
    }

    private VBox criarCardUpgrade(Upgrade upgrade) {
        int nivelAtual = upgradeDAO.buscarNivelDoJogador(player.getId(), upgrade.getId());
        int custo = upgradeDAO.calcularCustoProximoNivel(player.getId(), upgrade.getId());

        Label lblNome = new Label(upgrade.getNome());
        lblNome.getStyleClass().add("card-titulo");

        Label lblAtributo = new Label(formatarAtributo(upgrade.getAtributo()));
        lblAtributo.getStyleClass().add("card-subtitulo");

        Label lblNivel = new Label("Nível atual: " + nivelAtual + " / " + upgrade.getNivelMaximo());
        lblNivel.getStyleClass().add("card-texto");

        Label lblCusto = new Label(custo > 0 ? "Custo: " + custo + " joias" : "Nível máximo");
        lblCusto.getStyleClass().add("card-custo");

        Label lblDescricao = new Label(upgrade.getDescricao());
        lblDescricao.setWrapText(true);
        lblDescricao.getStyleClass().add("card-descricao");

        VBox card = new VBox(8, lblNome, lblAtributo, lblNivel, lblCusto, lblDescricao);
        card.setAlignment(Pos.TOP_CENTER);
        card.getStyleClass().add("card-base");

        card.setOnMouseClicked(event -> {
            upgradeSelecionado = upgrade;
            selecionarCard(flowUpgrades, card);

            lblDetalheTitulo.setText(upgrade.getNome());

            lblDetalheTexto.setText(
                    "Atributo: " + formatarAtributo(upgrade.getAtributo())
                    + "\nNível atual: " + nivelAtual + " / " + upgrade.getNivelMaximo()
                    + "\nPróximo custo: " + (custo > 0 ? custo + " joias" : "nível máximo")
                    + "\n\n" + upgrade.getDescricao()
            );

            lblMensagem.setText("Upgrade selecionado: " + upgrade.getNome());
        });

        return card;
    }

    private void carregarCardsTalismas() {
        try {
            flowTalismas.getChildren().clear();

            List<Talisman> talismas = talismanDAO.listarTodos();

            for (Talisman talisman : talismas) {
                VBox card = criarCardTalisma(talisman);
                flowTalismas.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblMensagem.setText("Erro ao carregar talismãs.");
        }
    }

    private VBox criarCardTalisma(Talisman talisman) {
        boolean possui = false;

        if (player != null && player.getId() > 0) {
            possui = talismanDAO.jogadorPossuiTalisma(player.getId(), talisman.getIdTalisma());
        }

        Label lblNome = new Label(talisman.getNome());
        lblNome.getStyleClass().add("card-titulo");

        Label lblCusto = new Label(possui ? "Adquirido" : "Custo: " + talisman.getValorEmJoiasSombrias() + " joias");
        lblCusto.getStyleClass().add(possui ? "card-adquirido" : "card-custo");

        Label lblBonus1 = new Label("+" + formatarNumero(talisman.getValorBuff1()) + " " + formatarAtributo(talisman.getAtributoBuff1()));
        lblBonus1.getStyleClass().add("card-texto");

        Label lblBonus2 = new Label("+" + formatarNumero(talisman.getValorBuff2()) + " " + formatarAtributo(talisman.getAtributoBuff2()));
        lblBonus2.getStyleClass().add("card-texto");

        Label lblDebuff = new Label("-" + formatarNumero(talisman.getValorDebuff()) + " " + formatarAtributo(talisman.getAtributoDebuff()));
        lblDebuff.getStyleClass().add("card-debuff");

        Label lblDescricao = new Label(talisman.getDescricao());
        lblDescricao.setWrapText(true);
        lblDescricao.getStyleClass().add("card-descricao");

        VBox card = new VBox(8, lblNome, lblCusto, lblBonus1, lblBonus2, lblDebuff, lblDescricao);
        card.setAlignment(Pos.TOP_CENTER);
        card.getStyleClass().add("card-base");

        if (possui) {
            card.getStyleClass().add("card-bloqueado");
        }

        card.setOnMouseClicked(event -> {
            talismanSelecionado = talisman;
            selecionarCard(flowTalismas, card);

            lblDetalheTalismaTitulo.setText(talisman.getNome());

            lblDetalheTalismaTexto.setText(
                    "Custo: " + talisman.getValorEmJoiasSombrias() + " joias"
                    + "\nBônus: +" + formatarNumero(talisman.getValorBuff1()) + " " + formatarAtributo(talisman.getAtributoBuff1())
                    + " | +" + formatarNumero(talisman.getValorBuff2()) + " " + formatarAtributo(talisman.getAtributoBuff2())
                    + "\nPenalidade: -" + formatarNumero(talisman.getValorDebuff()) + " " + formatarAtributo(talisman.getAtributoDebuff())
                    + "\n\n" + talisman.getDescricao()
            );

            lblMensagem.setText("Talismã selecionado: " + talisman.getNome());
        });

        return card;
    }

    private void selecionarCard(FlowPane flowPane, VBox cardSelecionado) {
        for (javafx.scene.Node node : flowPane.getChildren()) {
            node.getStyleClass().remove("card-base-selecionado");
        }

        if (!cardSelecionado.getStyleClass().contains("card-base-selecionado")) {
            cardSelecionado.getStyleClass().add("card-base-selecionado");
        }
    }

    @FXML
    private void onComprarUpgrade() {
        if (upgradeSelecionado == null) {
            lblMensagem.setText("Selecione um upgrade.");
            return;
        }

        try {
            boolean comprou = upgradeDAO.comprarOuMelhorar(player.getId(), upgradeSelecionado.getId());

            if (!comprou) {
                AlertUtil.erro("Joias insuficientes", "Você não possui Joias Sombrias suficientes.");
                return;
            }

            recarregarPlayer();
            carregarDadosTela();
            carregarCardsUpgrades();

            AlertUtil.info("Upgrade comprado", "Upgrade aplicado com sucesso.");
            lblDetalheTexto.setText("Você melhorou: " + upgradeSelecionado.getNome());

            lblMensagem.setText("Upgrade comprado/melhorado: " + upgradeSelecionado.getNome());

            upgradeSelecionado = null;

        } catch (Exception e) {
            e.printStackTrace();
            lblMensagem.setText("Erro ao comprar upgrade.");
        }
    }

    @FXML
    private void onComprarTalisma() {
        if (talismanSelecionado == null) {
            lblMensagem.setText("Selecione um talismã.");
            return;
        }

        try {
            boolean comprou = talismanDAO.comprarParaJogador(player.getId(), talismanSelecionado);

            if (!comprou) {
                lblMensagem.setText("Não foi possível comprar: joias insuficientes ou talismã já adquirido.");
                return;
            }

            recarregarPlayer();
            carregarDadosTela();
            carregarCardsTalismas();

            lblDetalheTalismaTitulo.setText("Talismã adquirido!");
            lblDetalheTalismaTexto.setText("Você comprou: " + talismanSelecionado.getNome());

            lblMensagem.setText("Talismã comprado: " + talismanSelecionado.getNome());

            talismanSelecionado = null;

        } catch (Exception e) {
            e.printStackTrace();
            lblMensagem.setText("Erro ao comprar talismã.");
        }
    }

    @FXML
    private void onContinuar() {
        Stage stage = (Stage) lblTitulo.getScene().getWindow();
        stage.close();
    }

    private void recarregarPlayer() {
        Player atualizado = playerDAO.buscarPorId(player.getId());

        if (atualizado != null) {
            player = atualizado;
        }
    }

    private String formatarAtributo(String atributo) {
        if (atributo == null) {
            return "-";
        }

        return atributo
                .replace("_", " ")
                .replace("VIDA MAXIMA", "VIDA MÁXIMA")
                .replace("ATAQUE SECUNDARIO", "ATAQUE SECUNDÁRIO");
    }

    private String formatarNumero(double valor) {
        if (valor == (int) valor) {
            return String.valueOf((int) valor);
        }

        return String.format("%.2f", valor);
    }
}