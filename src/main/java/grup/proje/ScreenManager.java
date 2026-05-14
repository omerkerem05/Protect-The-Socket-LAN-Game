package grup.proje;

import grup.proje.UI.GameUI;
import grup.proje.UI.LobbyBrowserUI;
import grup.proje.UI.LobbyUI;
import grup.proje.UI.MainMenuUI;
import javafx.scene.layout.StackPane;

public class ScreenManager {
    public StackPane root;
    private MainMenuUI mainMenuUI;
    public LobbyBrowserUI lobbyBrowserUI;
    private LobbyUI lobbyUI;
    private GameUI gameUI;

    public ScreenManager(StackPane root) {
        this.root = root;
    }

    public void setMainMenu(MainMenuUI ui) {
        this.mainMenuUI = ui;
    }

    public void setLobbyBrowser(LobbyBrowserUI ui) {
        this.lobbyBrowserUI = ui;
    }

    public void setLobby(LobbyUI ui) {
        this.lobbyUI = ui;
    }

    public void setGame(GameUI ui) {
        this.gameUI = ui;
    }

    public void showMainMenu() {
        root.getChildren().setAll(mainMenuUI.createMenu(700, 700));
    }

    public void showLobbyBrowser() {
        root.getChildren().setAll(lobbyBrowserUI.createBrowser(700, 700));
    }

    public void showLobby() {
        root.getChildren().setAll(lobbyUI.createLobby(700, 700));
    }

    public void showGame() {
        root.getChildren().setAll(gameUI.createGameScreen(700, 700));
    }
}