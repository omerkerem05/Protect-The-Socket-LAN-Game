package grup.proje;

import grup.proje.Controller.GameController;
import grup.proje.Controller.LobbyBrowserController;
import grup.proje.Controller.LobbyController;
import grup.proje.Controller.MainMenuController;
import grup.proje.UI.GameUI;
import grup.proje.UI.LobbyBrowserUI;
import grup.proje.UI.LobbyUI;
import grup.proje.UI.MainMenuUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {
    private StackPane root;

    public static String nicknameAtTextBox;
    public static boolean isHost;

    @Override
    public void start(Stage stage) {
        // 1️⃣ Ana root
        root = new StackPane();
        Assets.load();

        // 2️⃣ Managerlar
        ScreenManager screenManager = new ScreenManager(root);
        NetworkManager networkManager = new NetworkManager();

        // 3️⃣ UI'lar
        MainMenuUI mainMenuUI = new MainMenuUI();
        LobbyBrowserUI lobbyBrowserUI = new LobbyBrowserUI();
        LobbyUI lobbyUI = new LobbyUI();
        GameUI gameUI = new GameUI();

        // 4️⃣ Controllerlar
        MainMenuController menuController = new MainMenuController(screenManager, networkManager);
        LobbyController lobbyController = new LobbyController(screenManager, networkManager);
        LobbyBrowserController lobbyBrowserController = new LobbyBrowserController(screenManager, networkManager);
        GameController gameController = new GameController(screenManager, networkManager);

        // 5️⃣ UI -> Controller bağlantıları
        mainMenuUI.setController(menuController);
        lobbyBrowserUI.setController(lobbyBrowserController);
        lobbyUI.setController(lobbyController);
        gameUI.setController(gameController);

        //Ekstra benim bağlantılar
        lobbyBrowserController.setUI(lobbyBrowserUI);
        menuController.setBrowserUI(lobbyBrowserUI);
        networkManager.setLobbyUI(lobbyUI);
        networkManager.setGameController(gameController);

        // 6️⃣ ScreenManager'a UI'ları ver
        screenManager.setMainMenu(mainMenuUI);
        screenManager.setLobbyBrowser(lobbyBrowserUI);
        screenManager.setLobby(lobbyUI);
        screenManager.setGame(gameUI);

        // 7️⃣ İlk ekran
        screenManager.showMainMenu();

        Scene scene = new Scene(root, 700, 700);

        gameController.setupControls(scene);
        gameController.setUI(gameUI);

        stage.setTitle("Protect The Socket");
        stage.setScene(scene);
        stage.setResizable(false);

        stage.setOnCloseRequest(e -> {
            System.out.println("Sayfa kapatılıyor...");
            networkManager.stopBroadcast();
            networkManager.stopDiscovery();
            networkManager.stopServer();
            networkManager.disconnectClient();
            System.out.println("Sayfa kapatıldı!");
        });

        stage.show();
    }

    public static void main(String[] args){
        launch();
    }
}
