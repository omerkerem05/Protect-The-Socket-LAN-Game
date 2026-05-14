package grup.proje.Controller;

import grup.proje.Assets;
import grup.proje.Main;
import grup.proje.NetworkManager;
import grup.proje.ScreenManager;
import grup.proje.UI.LobbyBrowserUI;

public class MainMenuController {
    private ScreenManager screenManager;
    private NetworkManager networkManager;

    private LobbyBrowserUI lobbyBrowserUI;

    public MainMenuController(ScreenManager screenManager, NetworkManager networkManager) {
        this.screenManager = screenManager;
        this.networkManager = networkManager;
    }

    public void setBrowserUI(LobbyBrowserUI lobbyBrowserUI) {
        this.lobbyBrowserUI = lobbyBrowserUI;
    }

    public void CreateLobby(String nickname){
        Assets.createLobbySound.play();
        Main.isHost = true;
        Main.nicknameAtTextBox = nickname;
        try {
            // LOBBY BROADCAST
            networkManager.startBroadcast(nickname, 3);

            // SERVER BAŞLAT
            networkManager.startServer();

            networkManager.connectToServer("localhost");

            // LOBBY UI AÇ
            screenManager.showLobby();
        }
        catch (Exception ex) {
            networkManager.stopServer();
            System.err.println("HATA 1");
        }
    }

    public void JoinLobby(String nickname){
        Main.isHost = false;
        Main.nicknameAtTextBox = nickname;
        screenManager.showLobbyBrowser();
        networkManager.startDiscovery();
        lobbyBrowserUI.updateLobbyList(networkManager.getActiveLobbies());
    }
}
