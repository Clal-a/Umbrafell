package com.ced.umbrafell.controller;

import com.ced.umbrafell.model.InventarioItem;

import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @authors Cesar & Danilo
 */
public class InventarioController implements Initializable {

    @FXML
    private Label lblNome;

    @FXML
    private Label lblVida;

    @FXML
    private Label lblJoias;

    @FXML
    private Label lblFase;

    @FXML
    private ListView<InventarioItem> listaItens;

    @FXML
    private VBox painelDetalhe;

    @FXML
    private Label lblDetalheNome;

    @FXML
    private Label lblDetalheDesc;

    @FXML
    private Label lblDetalheTipo;

    @FXML
    private Label lblTotal;

    @FXML
    private Button btnUsar;

    @FXML
    private Button btnEquipar;

    private final List<InventarioItem> itens = new ArrayList<>();

    private InventarioItem itemSelecionado;
    private String acaoSelecionada;
    private String talismanEquipadoNome;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaItens.setFixedCellSize(98);

        configurarCellFactory();
        configurarSelecao();
        atualizarBotoesAcao();
    }

    public void setDados(
            String nome,
            int vida,
            int vidaMaxima,
            int joiasSombrias,
            int faseAtual,
            List<InventarioItem> itens
    ) {
        setDados(
                nome,
                vida,
                vidaMaxima,
                joiasSombrias,
                faseAtual,
                itens,
                null
        );
    }

    public void setDados(
            String nome,
            int vida,
            int vidaMaxima,
            int joiasSombrias,
            int faseAtual,
            List<InventarioItem> itensRecebidos,
            String talismanEquipadoNome
    ) {
        this.talismanEquipadoNome = talismanEquipadoNome;
        this.itemSelecionado = null;
        this.acaoSelecionada = null;

        this.itens.clear();

        if (itensRecebidos != null) {
            this.itens.addAll(itensRecebidos);
        }

        lblNome.setText("Jogador: " + nome);
        lblVida.setText("Vida: " + vida + " / " + vidaMaxima);
        lblJoias.setText("Joias Sombrias: " + joiasSombrias);
        lblFase.setText("Fase atual: " + faseAtual);

        carregarListaItens();

        painelDetalhe.setVisible(false);
        painelDetalhe.setManaged(false);

        atualizarBotoesAcao();
    }

    public InventarioItem getItemSelecionado() {
        return itemSelecionado;
    }

    public String getAcaoSelecionada() {
        return acaoSelecionada;
    }

    private void carregarListaItens() {
        listaItens.getItems().clear();
        listaItens.getItems().addAll(itens);

        lblTotal.setText("Total: " + itens.size() + " item(s)");
    }

    private void configurarCellFactory() {
        listaItens.setCellFactory(lista -> new ListCell<InventarioItem>() {

            @Override
            protected void updateItem(InventarioItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(null);
                setGraphic(criarCardInventario(item));
            }
        });
    }

    private HBox criarCardInventario(InventarioItem item) {
        Node icone = criarIconeItem(item);

        Label lblNomeItem = new Label(item.getNome());
        lblNomeItem.getStyleClass().add("item-nome");
        lblNomeItem.setWrapText(true);

        Label lblDescricao = new Label(textoSeguro(item.getDescricao()));
        lblDescricao.getStyleClass().add("item-descricao");
        lblDescricao.setWrapText(true);
        lblDescricao.setMaxWidth(300);

        Label lblTipo = new Label(item.getTipo());
        lblTipo.getStyleClass().add("item-tipo");

        VBox info = new VBox(3, lblNomeItem, lblDescricao, lblTipo);
        info.getStyleClass().add("item-info");
        HBox.setHgrow(info, Priority.ALWAYS);

        VBox ladoDireito = new VBox(6);
        ladoDireito.setAlignment(Pos.CENTER_RIGHT);

        if (ehTalismaEquipado(item)) {
            Label lblEquipado = new Label("EQUIPADO");
            lblEquipado.getStyleClass().add("selo-equipado");
            ladoDireito.getChildren().add(lblEquipado);
        }

        Label lblQuantidade = new Label("x" + item.getQuantidade());
        lblQuantidade.getStyleClass().add("item-quantidade");
        ladoDireito.getChildren().add(lblQuantidade);

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox card = new HBox(12, icone, info, espaco, ladoDireito);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("item-card");

        if ("Poção".equalsIgnoreCase(item.getTipo())) {
            card.getStyleClass().add("item-card-pocao");
        } else if ("Talismã".equalsIgnoreCase(item.getTipo())) {
            card.getStyleClass().add("item-card-talisma");
        }

        if (ehTalismaEquipado(item)) {
            card.getStyleClass().add("item-card-equipado");
        }

        Tooltip.install(card, new Tooltip(item.getNome() + "\n" + textoSeguro(item.getDescricao())));

        return card;
    }

    private Node criarIconeItem(InventarioItem item) {
        Image imagem = carregarImagemItem(item);

        if (imagem != null) {
            ImageView imageView = new ImageView(imagem);
            imageView.setFitWidth(58);
            imageView.setFitHeight(58);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(false);
            imageView.getStyleClass().add("item-imagem");
            return imageView;
        }

        Label fallback = new Label(item.getIcone() != null ? item.getIcone() : "?");
        fallback.getStyleClass().add("item-icone-fallback");
        return fallback;
    }

    private Image carregarImagemItem(InventarioItem item) {
        if (item == null || item.getNome() == null) {
            return null;
        }

        String nome = normalizarTexto(item.getNome());

        String caminho = null;

        if (nome.contains("pocao_de_vida") || nome.contains("vida")) {
            caminho = "/com/ced/umbrafell/icons/pocao_vida.png";
        } else if (nome.contains("pocao_de_forca") || nome.contains("forca")) {
            caminho = "/com/ced/umbrafell/icons/pocao_forca.png";
        } else if (nome.contains("resistencia") || nome.contains("defesa")) {
            caminho = "/com/ced/umbrafell/icons/pocao_defesa.png";
        } else if (nome.contains("carmesim")) {
            caminho = "/com/ced/umbrafell/icons/talisma_carmesim.png";
        } else if (nome.contains("nevoa")) {
            caminho = "/com/ced/umbrafell/icons/talisma_nevoa.png";
        } else if (nome.contains("profano")) {
            caminho = "/com/ced/umbrafell/icons/talisma_profano.png";
        }

        if (caminho == null || getClass().getResource(caminho) == null) {
            return null;
        }

        return new Image(getClass().getResource(caminho).toExternalForm());
    }

    private void configurarSelecao() {
        listaItens.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, selecionado) -> {
                    itemSelecionado = selecionado;

                    if (selecionado == null) {
                        painelDetalhe.setVisible(false);
                        painelDetalhe.setManaged(false);
                        atualizarBotoesAcao();
                        return;
                    }

                    mostrarDetalhe(selecionado);
                    atualizarBotoesAcao();
                });
    }

    private void mostrarDetalhe(InventarioItem item) {
        String prefixo = ehTalismaEquipado(item) ? "[EQUIPADO] " : "";

        lblDetalheNome.setText(prefixo + item.getNome());
        lblDetalheDesc.setText(textoSeguro(item.getDescricao()));
        lblDetalheTipo.setText(
                "Tipo: " + item.getTipo()
                + " | Quantidade: x" + item.getQuantidade()
        );

        painelDetalhe.setVisible(true);
        painelDetalhe.setManaged(true);
    }

    private void atualizarBotoesAcao() {
        boolean selecionouPocao =
                itemSelecionado != null
                && "Poção".equalsIgnoreCase(itemSelecionado.getTipo());

        boolean selecionouTalisma =
                itemSelecionado != null
                && "Talismã".equalsIgnoreCase(itemSelecionado.getTipo());

        boolean talismaJaEquipado = ehTalismaEquipado(itemSelecionado);

        if (btnUsar != null) {
            btnUsar.setDisable(!selecionouPocao);
        }

        if (btnEquipar != null) {
            btnEquipar.setDisable(!selecionouTalisma || talismaJaEquipado);

            if (talismaJaEquipado) {
                btnEquipar.setText("Equipado");
            } else {
                btnEquipar.setText("Equipar talismã");
            }
        }
    }

    private boolean ehTalismaEquipado(InventarioItem item) {
        return item != null
                && "Talismã".equalsIgnoreCase(item.getTipo())
                && talismanEquipadoNome != null
                && talismanEquipadoNome.equals(item.getNome());
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }

        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        normalizado = normalizado.replaceAll("\\p{M}", "");
        normalizado = normalizado.toLowerCase();
        normalizado = normalizado.replace(" ", "_");

        return normalizado;
    }

    private String textoSeguro(String texto) {
        return texto != null ? texto : "";
    }

    @FXML
    private void onUsar() {
        if (itemSelecionado == null) {
            return;
        }

        acaoSelecionada = "USAR";
        fecharJanela();
    }

    @FXML
    private void onEquipar() {
        if (itemSelecionado == null) {
            return;
        }

        acaoSelecionada = "EQUIPAR";
        fecharJanela();
    }

    @FXML
    private void onFechar() {
        acaoSelecionada = null;
        itemSelecionado = null;
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) lblNome.getScene().getWindow();
        stage.close();
    }
}