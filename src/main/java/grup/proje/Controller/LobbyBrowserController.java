package grup.proje.Controller;

import grup.proje.Lobby.LobbyInfo;
import grup.proje.NetworkManager;
import grup.proje.ScreenManager;
import grup.proje.UI.LobbyBrowserUI;
import javafx.scene.control.Alert;

import java.util.Objects;

public class LobbyBrowserController {

    private ScreenManager screenManager;
    private NetworkManager networkManager;

    private LobbyBrowserUI lobbyBrowserUI;

    public LobbyBrowserController(ScreenManager screenManager, NetworkManager networkManager) {
        this.screenManager = screenManager;
        this.networkManager = networkManager;
    }

    public void setUI(LobbyBrowserUI lobbyBrowserUI) {
        this.lobbyBrowserUI = lobbyBrowserUI;
    }

    public void Join(LobbyInfo lobby){
        if(lobby == null) return;
        if (Objects.equals(lobby.status, "Dolu") || Objects.equals(lobby.status, "Oyunda")){
            showError("Bu lobiye katılamazsınız!");
            networkManager.startDiscovery();
            return;
        }
        try{
            networkManager.connectToServer(lobby.ipAddress);
            screenManager.showLobby();
            System.out.println("Lobiye bağlanıldı! (browser controller)");
        }catch(Exception e){
            System.out.println("browser join hata: "+e);
            showError("Lobi bulunamadı!");
            networkManager.startDiscovery();
        }
    }

    public void Back(){
        screenManager.showMainMenu();
        networkManager.stopDiscovery();
    }

    public void Refresh(){
        networkManager.startDiscovery();
    }

    public void stopDiscovery(){
        networkManager.stopDiscovery();
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

