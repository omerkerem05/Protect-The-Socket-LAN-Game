package grup.proje.Controller;

import grup.proje.Assets;
import grup.proje.Main;
import grup.proje.NetworkManager;
import grup.proje.ScreenManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class LobbyController {

    private ScreenManager screenManager;
    private NetworkManager networkManager;

    public LobbyController(ScreenManager screenManager, NetworkManager networkManager) {
        this.screenManager = screenManager;
        this.networkManager = networkManager;
        networkManager.setGameStartListener(() -> {
            Platform.runLater(() -> {
                screenManager.showGame();
            });
        });
    }

    public void StartGame(){
        networkManager.startGame();
    }

    public void SendMessage(String msg){
        Assets.sendMessageSound.play(0.2);
        networkManager.sendChatMessage(Main.nicknameAtTextBox, msg);
    }

    public void goBackToLobbyBrowser(){
        Platform.runLater(() -> {
            showError("Host lobiden çıktı!");
            screenManager.showLobbyBrowser();
        });
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

