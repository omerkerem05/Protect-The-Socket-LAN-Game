package grup.proje.UI;

import grup.proje.Controller.MainMenuController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.beans.binding.BooleanBinding;

public class MainMenuUI {

    private StackPane root;
    private MainMenuController controller;

    public Button createLobbyBtn;
    public Button joinLobbyBtn;
    public TextField nicknameField;

    public void setController(MainMenuController controller) {
        this.controller = controller;
    }

    public StackPane createMenu(double width, double height) {
        root = new StackPane();

        // 1. Arka Plan
        Image bgImage = new Image(getClass().getResource("/images/menu_bg.png").toExternalForm());
        ImageView background = new ImageView(bgImage);
        background.setFitWidth(width);
        background.setFitHeight(height);

        // 2. Üst Kısım: Nickname Bölümü
        VBox topSection = new VBox(10);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(80, 0, 0, 0)); // Üstten 100px boşluk

        Label nickLabel = new Label("KULLANICI ADI");
        nickLabel.setFont(Font.font("Arial", FontWeight.BLACK, 18));
        nickLabel.setTextFill(Color.WHITE);

        nicknameField = new TextField();
        nicknameField.setPromptText("Nickname giriniz...");
        nicknameField.setMaxWidth(300);
        nicknameField.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.9); " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 12; " +
                        "-fx-font-size: 16; " +
                        "-fx-border-color: #f39c12; " +
                        "-fx-border-width: 3;"
        );

        topSection.getChildren().addAll(nickLabel, nicknameField);

        // 3. Alt Kısım: Butonlar Bölümü
        VBox bottomSection = new VBox(20);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(0, 0, 30, 0)); // Alttan 100px boşluk

        createLobbyBtn = createStyledButton("LAN LOBİSİ OLUŞTUR", "#2ecc71", "#27ae60");
        joinLobbyBtn = createStyledButton("LAN LOBİSİNE KATIL", "#3498db", "#2980b9");
        createLobbyBtn.setOnAction(e -> controller.CreateLobby(nicknameField.getText()));
        joinLobbyBtn.setOnAction(e -> controller.JoinLobby(nicknameField.getText()));

        bottomSection.getChildren().addAll(createLobbyBtn, joinLobbyBtn);

        // 4. Pasif/Aktif Kuralı (Aynen korundu)
        BooleanBinding isNicknameInvalid = nicknameField.textProperty().isEmpty()
                .or(nicknameField.textProperty().length().lessThan(3));

        createLobbyBtn.disableProperty().bind(isNicknameInvalid);
        joinLobbyBtn.disableProperty().bind(isNicknameInvalid);

        BorderPane layoutWrapper = new BorderPane();
        layoutWrapper.setTop(topSection);
        layoutWrapper.setBottom(bottomSection);

        // Katmanları ekle: Önce Resim, sonra düzen
        root.getChildren().addAll(background, layoutWrapper);
        return root;
    }

    private Button createStyledButton(String text, String color, String hoverColor) {
        Button btn = new Button(text);
        btn.setPrefWidth(300);
        btn.setPrefHeight(50);
        btn.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

        String style = "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 4);";
        String hoverStyle = "-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-scale-x: 1.05; -fx-scale-y: 1.05;";
        String disabledStyle = "-fx-background-color: #7f8c8d; -fx-text-fill: #bdc3c7; -fx-background-radius: 8;";

        btn.setStyle(style);

        btn.setOnMouseEntered(e -> { if (!btn.isDisable()) btn.setStyle(hoverStyle); });
        btn.setOnMouseExited(e -> { if (!btn.isDisable()) btn.setStyle(style); });

        btn.disabledProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) btn.setStyle(disabledStyle);
            else btn.setStyle(style);
        });

        return btn;
    }
}