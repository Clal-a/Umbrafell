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
    
    private static final int PRECO_POCAO_VIDA = 25;
    private static final int CURA_POCAO_VIDA = 40;

    private static final int PRECO_POCAO_RESISTENCIA = 35;
    private static final int BONUS_DEFESA_RESISTENCIA = 3;
    
    private static final int PRECO_POCAO_FORCA = 35;
    private static final int BONUS_DANO_FORCA = 3;

    private int defesaTemporariaComprada = 0; 
    
    private int danoTemporarioComprado = 0;

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

        lblTitulo.setText("Base de Umbrafell");

        lblJogador.setText("");

        lblJoias.setText("Joias Sombrias: " + player.getJoiasSombrias());
        lblJoias.setStyle(
                "-fx-text-fill: #f2d28b;"
                + "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(217,164,65,0.45), 8, 0.35, 0, 0);"
        );

        lblStatus.setText("");
    }

    private void carregarCardsUpgrades() {
        try {
            flowUpgrades.getChildren().clear();

            List<Upgrade> upgrades = upgradeDAO.listarTodos();
            
            for (Upgrade upgrade : upgrades) {
                
                if (deveOcultarUpgrade(upgrade)) {
                    continue;
                }
                
                VBox card = criarCardUpgrade(upgrade);
                flowUpgrades.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblMensagem.setText("Erro ao carregar upgrades.");
        }
    }
    
    private boolean deveOcultarUpgrade(Upgrade upgrade) {
        if (upgrade == null) {
            return true;
        }

        String nome = upgrade.getNome() != null ? upgrade.getNome().toLowerCase() : "";
        String atributo = upgrade.getAtributo() != null ? upgrade.getAtributo().toUpperCase() : "";

        return nome.contains("disparo sombrio")
                || atributo.contains("ATAQUE_SECUNDARIO");
    }

    private VBox criarCardUpgrade(Upgrade upgrade) {
        VBox card = new VBox(8);
        card.setPrefWidth(220);
        card.setMinHeight(190);
        card.getStyleClass().add("card-upgrade");

        Label lblNome = new Label(upgrade.getNome());
        lblNome.setWrapText(true);
        lblNome.setStyle("-fx-text-fill: #f2d28b; -fx-font-weight: bold; -fx-font-size: 15px;");

        Label lblAtributo = new Label(upgrade.getAtributo());
        lblAtributo.setStyle("-fx-text-fill: #d9a441; -fx-font-size: 13px;");

        int nivelAtual = upgradeDAO.buscarNivelDoJogador(player.getId(), upgrade.getId());
        int custo = upgradeDAO.calcularCustoProximoNivel(player.getId(), upgrade.getId());

        Label lblNivel = new Label("Nível atual: " + nivelAtual + " / " + upgrade.getNivelMaximo());
        lblNivel.setStyle("-fx-text-fill: #f2e6c9; -fx-font-size: 12px;");

        Label lblCusto = new Label(custo > 0 ? "Custo: " + custo + " joias" : "Nível máximo");
        lblCusto.setStyle("-fx-text-fill: #f2d28b; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label lblDescricao = new Label(upgrade.getDescricao());
        lblDescricao.setWrapText(true);
        lblDescricao.setStyle("-fx-text-fill: #c9b9d0; -fx-font-size: 12px;");

        Button btnComprar = new Button("Melhorar");
        btnComprar.getStyleClass().add("botao-destaque");
        btnComprar.setMaxWidth(Double.MAX_VALUE);
        btnComprar.setVisible(false);
        btnComprar.setManaged(false);

        btnComprar.setOnAction(event -> {
            upgradeSelecionado = upgrade;
            onComprarUpgrade();
        });

        card.setOnMouseClicked(event -> {
            upgradeSelecionado = upgrade;

            selecionarCard(flowUpgrades, card);
            esconderBotoesDosCards(flowUpgrades);

            btnComprar.setVisible(true);
            btnComprar.setManaged(true);

            lblMensagem.setText("Upgrade selecionado: " + upgrade.getNome());
        });

        card.getChildren().addAll(
                lblNome,
                lblAtributo,
                lblNivel,
                lblCusto,
                lblDescricao,
                btnComprar
        );

        return card;
    }

    private void carregarCardsTalismas() {
        try {
            flowTalismas.getChildren().clear();

            flowTalismas.getChildren().add(criarCardPocaoVida());
            flowTalismas.getChildren().add(criarCardPocaoResistencia());
            flowTalismas.getChildren().add(criarCardPocaoForca());

            List<Talisman> talismas = talismanDAO.listarTodos();

            for (Talisman talisman : talismas) {
                VBox card = criarCardTalisma(talisman);
                flowTalismas.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblMensagem.setText("Erro ao carregar loja.");
        }
    }

    private VBox criarCardTalisma(Talisman talisman) {
        final boolean possui =
                player != null
                && player.getId() > 0
                && talismanDAO.jogadorPossuiTalisma(player.getId(), talisman.getIdTalisma());

        Label lblNome = new Label(talisman.getNome());
        lblNome.getStyleClass().add("card-titulo");
        lblNome.setWrapText(true);

        Label lblCusto = new Label(
                possui
                        ? "Adquirido"
                        : "Custo: " + talisman.getValorEmJoiasSombrias() + " joias"
        );
        lblCusto.getStyleClass().add(possui ? "card-adquirido" : "card-custo");

        Label lblBonus1 = new Label(
                "+" + formatarNumero(talisman.getValorBuff1())
                + " " + formatarAtributo(talisman.getAtributoBuff1())
        );
        lblBonus1.getStyleClass().add("card-texto");

        Label lblBonus2 = new Label(
                "+" + formatarNumero(talisman.getValorBuff2())
                + " " + formatarAtributo(talisman.getAtributoBuff2())
        );
        lblBonus2.getStyleClass().add("card-texto");

        Label lblDebuff = new Label(
                "-" + formatarNumero(talisman.getValorDebuff())
                + " " + formatarAtributo(talisman.getAtributoDebuff())
        );
        lblDebuff.getStyleClass().add("card-debuff");

        Label lblDescricao = new Label(talisman.getDescricao());
        lblDescricao.setWrapText(true);
        lblDescricao.getStyleClass().add("card-descricao");

        Button btnComprar = new Button(possui ? "Adquirido" : "Comprar");
        btnComprar.getStyleClass().add("botao-destaque");
        btnComprar.setMaxWidth(Double.MAX_VALUE);
        btnComprar.setVisible(false);
        btnComprar.setManaged(false);
        btnComprar.setDisable(possui);

        btnComprar.setOnAction(event -> {
            event.consume();

            if (possui) {
                lblMensagem.setText("Você já possui este talismã.");
                return;
            }

            talismanSelecionado = talisman;
            onComprarTalisma();
        });

        VBox card = new VBox(
                8,
                lblNome,
                lblCusto,
                lblBonus1,
                lblBonus2,
                lblDebuff,
                lblDescricao,
                btnComprar
        );

        card.setPrefWidth(220);
        card.setMinHeight(210);
        card.setAlignment(Pos.TOP_CENTER);
        card.getStyleClass().add("card-base");
        card.getStyleClass().add("card-talisma");

        if (possui) {
            card.getStyleClass().add("card-bloqueado");
        }

        card.setOnMouseClicked(event -> {
            talismanSelecionado = talisman;

            selecionarCard(flowTalismas, card);
            esconderBotoesDosCards(flowTalismas);

            btnComprar.setVisible(true);
            btnComprar.setManaged(true);

            lblMensagem.setText(
                    possui
                            ? "Talismã já adquirido: " + talisman.getNome()
                            : "Talismã selecionado: " + talisman.getNome()
            );
        });

        return card;
    }
    
    private VBox criarCardPocaoVida() {
        return criarCardPocao(
                "Poção de Vida",
                "POÇÃO",
                "Custo: " + PRECO_POCAO_VIDA + " joias",
                "Recupera +" + CURA_POCAO_VIDA + " de vida imediatamente.",
                "Beber",
                () -> comprarPocaoVida()
        );
    }

    private VBox criarCardPocaoResistencia() {
        return criarCardPocao(
                "Poção de Resistência",
                "POÇÃO",
                "Custo: " + PRECO_POCAO_RESISTENCIA + " joias",
                "Aumenta sua defesa em +" + BONUS_DEFESA_RESISTENCIA + " apenas na próxima fase.",
                "Comprar",
                () -> comprarPocaoResistencia()
        );
    }
    
    private VBox criarCardPocaoForca() {
        return criarCardPocao(
                "Poção de Força",
                "POÇÃO",
                "Custo: " + PRECO_POCAO_FORCA + " joias",
                "Aumenta seu dano em +" + BONUS_DANO_FORCA + " apenas na próxima fase.",
                "Comprar",
                () -> comprarPocaoForca()
        );
    }

    private VBox criarCardPocao(
            String nome,
            String tipo,
            String custo,
            String descricao,
            String textoBotao,
            Runnable acaoCompra
    ) {
        Label lblNome = new Label(nome);
        lblNome.getStyleClass().add("card-titulo");
        lblNome.setWrapText(true);

        Label lblTipo = new Label(tipo);
        lblTipo.setStyle("-fx-text-fill: #d9a441; -fx-font-size: 13px;");

        Label lblCusto = new Label(custo);
        lblCusto.getStyleClass().add("card-custo");

        Label lblDescricao = new Label(descricao);
        lblDescricao.setWrapText(true);
        lblDescricao.getStyleClass().add("card-descricao");

        Button btnComprar = new Button(textoBotao);
        btnComprar.getStyleClass().add("botao-destaque");
        btnComprar.setMaxWidth(Double.MAX_VALUE);
        btnComprar.setVisible(false);
        btnComprar.setManaged(false);

        VBox card = new VBox(
                8,
                lblNome,
                lblTipo,
                lblCusto,
                lblDescricao,
                btnComprar
        );

        card.setPrefWidth(220);
        card.setMinHeight(185);
        card.setAlignment(Pos.TOP_CENTER);
        card.getStyleClass().add("card-base");
        card.getStyleClass().add("card-pocao");

        btnComprar.setOnAction(event -> {
            event.consume();
            acaoCompra.run();
        });

        card.setOnMouseClicked(event -> {
            selecionarCard(flowTalismas, card);
            esconderBotoesDosCards(flowTalismas);

            btnComprar.setVisible(true);
            btnComprar.setManaged(true);

            lblMensagem.setText(nome + " selecionada.");
        });

        return card;
    }
    
    private void comprarPocaoVida() {
        if (player == null || player.getId() <= 0) {
            lblMensagem.setText("Jogador inválido.");
            return;
        }

        if (player.getVidaAtual() >= player.getVidaMaxima()) {
            lblMensagem.setText("Sua vida já está cheia.");
            return;
        }

        try {
            boolean pagou = playerDAO.gastarJoiasSombrias(player.getId(), PRECO_POCAO_VIDA);

            if (!pagou) {
                AlertUtil.erro("Joias insuficientes", "Você não possui Joias Sombrias suficientes.");
                return;
            }

            recarregarPlayer();

            int novaVida = player.getVidaAtual() + CURA_POCAO_VIDA;

            if (novaVida > player.getVidaMaxima()) {
                novaVida = player.getVidaMaxima();
            }

            player.setVidaAtual(novaVida);
            playerDAO.atualizar(player);

            recarregarPlayer();
            carregarDadosTela();
            carregarCardsTalismas();

            AlertUtil.info("Poção usada", "Você recuperou vida.");
            lblMensagem.setText("Poção de Vida usada. Vida atual: " + player.getVidaAtual() + "/" + player.getVidaMaxima());

        } catch (Exception e) {
            e.printStackTrace();
            lblMensagem.setText("Erro ao comprar poção de vida.");
        }
    }

    private void comprarPocaoResistencia() {
        if (player == null || player.getId() <= 0) {
            lblMensagem.setText("Jogador inválido.");
            return;
        }

        try {
            boolean pagou = playerDAO.gastarJoiasSombrias(player.getId(), PRECO_POCAO_RESISTENCIA);

            if (!pagou) {
                AlertUtil.erro("Joias insuficientes", "Você não possui Joias Sombrias suficientes.");
                return;
            }

            defesaTemporariaComprada += BONUS_DEFESA_RESISTENCIA;

            recarregarPlayer();
            carregarDadosTela();
            carregarCardsTalismas();

            AlertUtil.info(
                    "Poção comprada",
                    "Defesa temporária +" + BONUS_DEFESA_RESISTENCIA + " será aplicada na próxima fase."
            );

            lblMensagem.setText(
                    "Poção de Resistência comprada. Defesa temporária acumulada: +"
                    + defesaTemporariaComprada
            );

        } catch (Exception e) {
            e.printStackTrace();
            lblMensagem.setText("Erro ao comprar poção de resistência.");
        }
    }
    
    private void comprarPocaoForca() {
        if (player == null || player.getId() <= 0) {
            lblMensagem.setText("Jogador inválido.");
            return;
        }

        try {
            boolean pagou = playerDAO.gastarJoiasSombrias(player.getId(), PRECO_POCAO_FORCA);

            if (!pagou) {
                AlertUtil.erro("Joias insuficientes", "Você não possui Joias Sombrias suficientes.");
                return;
            }

            danoTemporarioComprado += BONUS_DANO_FORCA;

            recarregarPlayer();
            carregarDadosTela();
            carregarCardsTalismas();

            AlertUtil.info(
                    "Poção comprada",
                    "Dano temporário +" + BONUS_DANO_FORCA + " será aplicado na próxima fase."
            );

            lblMensagem.setText(
                    "Poção de Força comprada. Dano temporário acumulado: +"
                    + danoTemporarioComprado
            );

        } catch (Exception e) {
            e.printStackTrace();
            lblMensagem.setText("Erro ao comprar poção de força.");
        }
    }

    private void selecionarCard(FlowPane flowPane, VBox cardSelecionado) {
        for (javafx.scene.Node node : flowPane.getChildren()) {
            node.getStyleClass().remove("card-base-selecionado");
            node.getStyleClass().remove("card-selecionado");
        }

        if (!cardSelecionado.getStyleClass().contains("card-selecionado")) {
            cardSelecionado.getStyleClass().add("card-selecionado");
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
                AlertUtil.erro(
                        "Compra não realizada",
                        "Joias insuficientes ou talismã já adquirido."
                );
                lblMensagem.setText("Não foi possível comprar o talismã.");
                return;
            }

            recarregarPlayer();
            carregarDadosTela();
            carregarCardsTalismas();

            AlertUtil.info("Talismã comprado", "Talismã adquirido com sucesso.");

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
    
    private void esconderBotoesDosCards(FlowPane flowPane) {
        for (javafx.scene.Node node : flowPane.getChildren()) {
            if (node instanceof VBox) {
                VBox card = (VBox) node;

                for (javafx.scene.Node filho : card.getChildren()) {
                    if (filho instanceof Button) {
                        Button botao = (Button) filho;
                        botao.setVisible(false);
                        botao.setManaged(false);
                    }
                }
            }
        }
    }
    
    public int getDefesaTemporariaComprada() {
        return defesaTemporariaComprada;
    }
    
    public int getDanoTemporarioComprado() {
        return danoTemporarioComprado;
    }
}