package grup.proje.UI;

import grup.proje.Controller.LobbyBrowserController;
import grup.proje.Lobby.LobbyInfo;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LobbyBrowserUI {

    private StackPane root;
    private LobbyBrowserController controller;

    public ListView<LobbyInfo> lobbyListView;
    public Button backBtn;
    public Button joinBtn;
    public Button refreshBtn;

    public void setController(LobbyBrowserController controller) {
        this.controller = controller;
    }

    public StackPane createBrowser(double width, double height) {
        root = new StackPane();
        root.setPrefSize(width, height);
        root.setStyle("-fx-background-color: #1e272e;"); // Arka plan koyu

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);

        // --- BAŞLIK ---
        Label title = new Label("AKTİF LOBİLER");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-effect: dropshadow(gaussian, #3498db, 10, 0, 0, 0);");

        // --- LİSTE (ListView) ---
        lobbyListView = new ListView<>();
        lobbyListView.setPrefHeight(500);

        // Listenin stilini oyun temasına uyduralım
        lobbyListView.setStyle(
                "-fx-background-color: #2f3640;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: #353b48;" +
                        "-fx-border-width: 2;" +
                        "-fx-control-inner-background: #2f3640;" +
                        "-fx-padding: 5;"
        );

        lobbyListView.setOnMouseClicked(e -> {
            controller.stopDiscovery();
        });

        // --- ALT BUTONLAR ---
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);

        joinBtn = createStyledButton("KATIL", "#2ecc71");
        backBtn = createStyledButton("GERİ DÖN", "#e74c3c");
        refreshBtn = createStyledButton("YENİLE", "#3498db");
        joinBtn.setOnAction(e -> controller.Join(getSelectedLobby()));
        backBtn.setOnAction(e -> controller.Back());
        refreshBtn.setOnAction(e -> controller.Refresh());

        controls.getChildren().addAll(backBtn, refreshBtn, joinBtn);
        layout.getChildren().addAll(title, lobbyListView, controls);

        root.getChildren().add(layout);

        // Liste satırlarını özelleştirme (Custom Cell Factory)
        setupCustomListCells();

        return root;
    }

    public LobbyInfo getSelectedLobby() {
        return lobbyListView.getSelectionModel().getSelectedItem();
    }

    public void updateLobbyList(ObservableList<LobbyInfo> lobbies) {
        lobbyListView.setItems(lobbies);
        System.out.println(lobbies);
    }

    private void setupCustomListCells() {
        lobbyListView.setCellFactory(lv -> new ListCell<LobbyInfo>() {
            @Override
            protected void updateItem(LobbyInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Satır tasarımı
                    HBox cellLayout = new HBox(20);
                    cellLayout.setAlignment(Pos.CENTER_LEFT);
                    cellLayout.setPadding(new Insets(10, 20, 10, 20));

                    Label nameLabel = new Label(item.hostName + "'ın Lobisi");
                    nameLabel.setMinWidth(250);
                    nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

                    Label countLabel = new Label(item.playerCount + "/3");
                    countLabel.setMinWidth(100);

                    Label statusLabel = new Label(item.status); // "Lobide", "Savaşta", "Dolu"
                    statusLabel.setMinWidth(150);
                    statusLabel.setAlignment(Pos.CENTER);

                    cellLayout.getChildren().addAll(nameLabel, countLabel, statusLabel);
                    setGraphic(cellLayout);

                    if (isSelected()) {
                        setStyle(
                                "-fx-background-color: #3d566e;" +
                                        "-fx-background-radius: 10;" +
                                        "-fx-border-color: #2ecc71;" +
                                        "-fx-border-width: 2;" +
                                        "-fx-border-radius: 10;"
                        );
                    } else {
                        setStyle(
                                "-fx-background-color: #353b48;" +
                                        "-fx-background-radius: 10;"
                        );
                    }
                }
            }
        });
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefSize(150, 40);
        btn.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand;"
        );
        return btn;
    }
}