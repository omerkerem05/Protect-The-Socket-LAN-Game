package grup.proje.UI;

import grup.proje.Assets;
import grup.proje.Controller.LobbyController;
import grup.proje.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LobbyUI {

    private StackPane root;
    private LobbyController controller;

    private StackPane leftSlot;
    private StackPane centerSlot;
    private StackPane rightSlot;

    private Label leftNameLabel = new Label();
    private Label centerNameLabel = new Label();
    private Label rightNameLabel = new Label();

    TextArea chatMessages;
    TextField chatInput;

    public void setController(LobbyController controller) {
        this.controller = controller;
    }

    public StackPane createLobby(double width, double height) {
        root = new StackPane();

        BackgroundSize bgSize = new BackgroundSize(700, 470, false, false, false, false);
        BackgroundImage bImg = new BackgroundImage(
                Assets.lobbyBg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(
                        Side.LEFT, 0.5, true,
                        Side.TOP, 0, true
                ),
                bgSize
        );
        root.setBackground(new Background(bImg));

        VBox mainLayout = new VBox(); // BorderPane yerine VBox daha düzenli duracaktır
        mainLayout.setPrefSize(width, height);

        // --- ÜST ALAN (400px): Oyuncular ---
        HBox playerArea = new HBox(30);
        playerArea.setPrefHeight(350); // Buton için yer açmak adına biraz kıstık
        playerArea.setAlignment(Pos.TOP_CENTER);

        leftSlot = createSlot(Assets.avatar2, 164, 343, leftNameLabel); // Sol (2. oyuncu)
        centerSlot = createSlot(Assets.avatar1, 173, 361, centerNameLabel); // Orta (Host)
        rightSlot = createSlot(Assets.avatar3, 164, 343, rightNameLabel); // Sağ (3. oyuncu)

        leftSlot.setVisible(false);
        rightSlot.setVisible(false);

        playerArea.getChildren().addAll(leftSlot, centerSlot, rightSlot);

        HBox btnContainer = new HBox();
        btnContainer.setAlignment(Pos.CENTER);
        btnContainer.setPrefHeight(100);

        if (Main.isHost){
            centerNameLabel.setText(Main.nicknameAtTextBox);
            Button startButton = new Button("OYUNU BAŞLAT");
            styleStartButton(startButton, true);
            btnContainer.getChildren().add(startButton);
            startButton.setOnAction(e -> controller.StartGame());
        }


        // --- ALT ALAN (250-300px): Chat ---
        VBox chatArea = new VBox(10);
        chatArea.setPrefHeight(250);
        chatArea.setPadding(new Insets(15));
        chatArea.setStyle("-fx-background-color: #060B0f; -fx-background-radius: 20 20 0 0;");

        chatMessages = new TextArea();
        chatMessages.setEditable(false);
        chatMessages.setPrefHeight(200);
        chatMessages.setWrapText(true);
        // Chat geçmişi stili
        chatMessages.setStyle(
                "-fx-control-inner-background: #11161A; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-family: 'Consolas'; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: transparent;"
        );

        HBox chatInputArea = new HBox(10);
        chatInputArea.setAlignment(Pos.CENTER);

        chatInput = new TextField();
        chatInput.setPromptText("Mesaj yaz...");
        chatInput.setPrefWidth(width - 120);
        chatInput.setStyle("-fx-background-radius: 15; -fx-padding: 8;");

        Button sendBtn = new Button("Gönder");
        sendBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 15; -fx-cursor: hand;");
        sendBtn.setOnAction(e -> controller.SendMessage(chatInput.getText()));

        chatInputArea.getChildren().addAll(chatInput, sendBtn);
        chatArea.getChildren().addAll(chatMessages, chatInputArea);

        // Hepsini dikeyde birleştir
        mainLayout.getChildren().addAll(playerArea, btnContainer, chatArea);

        root.getChildren().add(mainLayout);

        return root;
    }

    public void appendChatMessage(String playerName, String msg) {
        if (msg.isEmpty()) return; // boş mesaj gönderme

        // Saat formatı sabit HH:mm:ss
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = LocalTime.now().format(formatter);

        // 🔥 Format: [12:30:45] Kerem: mesaj
        String formatted = "[" + time + "] " + playerName + ": " + msg;

        // Chatbox'a ekle
        chatMessages.appendText(formatted + "\n");

        // input temizle
        chatInput.clear();
    }

    private StackPane createSlot(Image img, int width, int height, Label nameLabel) {
        ImageView view = new ImageView(img);
        StackPane.setAlignment(view, Pos.TOP_CENTER);
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.2), 15, 0, 0, 0);");

        // Oyuncu ismi
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        // Label'ı resmin üst kısmına hizala
        StackPane.setAlignment(nameLabel, Pos.TOP_CENTER);
        StackPane slot = new StackPane(view, nameLabel);
        slot.setPrefSize(width, height); // opsiyonel, düzen için

        nameLabel.setTranslateY(50);

        return slot;
    }

    private void FillNameLabel(Label nameLabel, String nickname){
        nameLabel.setText(nickname);
    }

    private void styleStartButton(Button btn, boolean isHost) {

        btn.setStyle(
                // ANA GRADIENT (resimdeki mavi ton)
                "-fx-background-color: linear-gradient(to bottom, #164157, #0c97ae);" + //#00eaff, #0077ff

                        // yazı
                        "-fx-text-fill: #afe1e5;" + // #e0f7ff
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +

                        // şekil
                        "-fx-background-radius: 50;" +
                        "-fx-padding: 6 25;" +

                        // dış çerçeve (ince parlak çizgi)
                        "-fx-border-color: #145f7a;" + // rgba(0,255,255,0.5)
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 50;" +

                        // glow + shadow
                        "-fx-effect: dropshadow(gaussian, rgba(20, 159, 180, 0.6), 20, 0.5, 0, 0);" +

                        "-fx-cursor: hand;"
        );

        // hover efekti (resimdeki gibi daha parlak hale geliyor)
        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
            btn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #1f5c73, #12b3c7);" +
                            "-fx-text-fill: #e6fcff;" +
                            "-fx-font-size: 20px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 50;" +
                            "-fx-padding: 6 25;" +

                            // border biraz daha açık ton
                            "-fx-border-color: #1aa0c4;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 50;" +

                            // glow daha soft ve uyumlu
                            "-fx-effect: dropshadow(gaussian, rgba(18, 179, 199, 0.9), 30, 0.6, 0, 0);" +

                            "-fx-cursor: hand;"
            );
        });

        btn.setOnMouseExited(e -> styleStartButton(btn, isHost));
    }

    public void setPlayers(List<String> playerList) {
        // Platform.runLater içinde çağrılacak
        if (playerList.size() == 1) {
            leftSlot.setVisible(false);
            rightSlot.setVisible(false);
            centerNameLabel.setText(playerList.get(0));
        }
        else if (playerList.size() == 2) {
            leftSlot.setVisible(true);
            rightSlot.setVisible(false);
            centerNameLabel.setText(playerList.get(0));
            leftNameLabel.setText(playerList.get(1));
        } else if (playerList.size() == 3) {
            leftSlot.setVisible(true);
            rightSlot.setVisible(true);
            centerNameLabel.setText(playerList.get(0));
            leftNameLabel.setText(playerList.get(1));
            rightNameLabel.setText(playerList.get(2));
        }
    }


    public void goBackToLobbyBrowser(){
        controller.goBackToLobbyBrowser();
    }
}