package GameClient.Networking;

import GameClient.GameClient;
import GameClient.Entities.Tank;
import GameClient.Networking.Library.Protocol;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Cliente de comunicación con el servidor.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class SocketClient {
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private GameClient gameClient;
    
    private Socket server;
    
    public GameClient getGameClient() {
        return gameClient;
    }
    
    private boolean forceDisconnect;
    private boolean connected;
    
    public boolean isForceDisconnect() {
        return forceDisconnect;
    }
    
    public SocketClient(GameClient gameClient) {
        this.gameClient = gameClient;
        connected = false;
        forceDisconnect = false;
    }
    
    public void setConnected(boolean connected) {
        this.connected = connected;
        
        if(!connected) {
            gameClient.onDisconnect();
        }
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Fuerza la desconexión.
     * @throws IOException 
     */
    public void forceDisconnect() throws IOException {
        forceDisconnect = true;
        inputStream.close();
        outputStream.close();
        server.close();

        setConnected(false);        
    }
    
    /**
     * Conecta al servidor.
     * @param url URL de conexión en formato IP:Puerto
     * @throws IOException 
     */
    public void connect(String url) throws IOException {
        String[] parts = url.split(":");

        server = new Socket(parts[0], Integer.parseInt(parts[1]));

        inputStream = new DataInputStream(server.getInputStream());
        outputStream = new DataOutputStream(server.getOutputStream());

        setConnected(true);
        
        ServerMessageHandler client = new ServerMessageHandler(
            this, inputStream, outputStream
        );        
        
        client.start();
    }
    
    /**
     * Notifica al servidor el movimiento del jugador.
     * @param player
     * @throws IOException 
     */
    public void updatePlayerMove(Tank player) throws IOException {
        outputStream.writeInt(Protocol.Messages.PLAYER_MOVE);
        outputStream.writeUTF(player.getId());
        outputStream.writeDouble(player.getX());
        outputStream.writeDouble(player.getY());
        outputStream.writeInt(player.getRotation());
        outputStream.flush();
    }
    
    /**
     * Notifica al servidor la creación de un proyectil.
     * @param playerId
     * @param x
     * @param y
     * @param rotation
     * @throws IOException 
     */
    public void updateProjectileCreate(String playerId, int x, int y, int rotation) throws IOException {
        outputStream.writeInt(Protocol.Messages.PROJECTILE_CREATE);
        outputStream.writeUTF(playerId);
        outputStream.writeInt(x);
        outputStream.writeInt(y);
        outputStream.writeInt(rotation);
        outputStream.flush();
    }
    
    /**
     * Aumenta la puntuación de un equipo y lo envía al servidor.
     * @param team Equipo rojo o azul
     * @throws IOException 
     */
    public void updateScore(int team) throws IOException {
        outputStream.writeInt(Protocol.Messages.SCORE_UPDATE);
        outputStream.writeInt(team);
        outputStream.flush();
    }
    
    /**
     * Notifica al servidor de que se ha recogido un objeto.
     * @param playerId ID del jugador
     * @throws IOException 
     */
    public void updatePickupCollected(String playerId) throws IOException {
        outputStream.writeInt(Protocol.Messages.PICKUP_COLLECTED);
        outputStream.writeUTF(playerId);
        outputStream.flush();        
    }
    
    /**
     * Notifica al servidor las vidas de un jugador.
     * @param playerId ID del jugador
     * @param lives Vidas del jugador
     * @throws IOException 
     */
    public void updateLives(String playerId, int lives) throws IOException {
        outputStream.writeInt(Protocol.Messages.LIVES_UPDATE);
        outputStream.writeUTF(playerId);
        outputStream.writeInt(lives);
        outputStream.flush();            
    }
     
    /**
     * Notifica al cliente el movimiento de un jugador.
     * @param playerId
     * @param x
     * @param y
     * @param rotation 
     */
    public void firePlayerMove(String playerId, double x, double y, int rotation) {
        gameClient.onPlayerMove(playerId, x, y, rotation);
    }
    
    /**
     * Notifica al cliente la creación de un proyectil.
     * @param playerId
     * @param x
     * @param y
     * @param rotation 
     */
    public void fireProjectileCreate(String playerId, int x, int y, int rotation) {
        gameClient.onProjectileCreate(playerId, x, y, rotation);
    }
    
    /**
     * Notifica al cliente la llegada de un jugador.
     * @param player 
     */
    public void firePlayerJoin(Tank player) {
        gameClient.onPlayerJoin(player);
    }
    
    /**
     * Notifica al cliente la recogida de un objeto.
     * @param x
     * @param y 
     */
    public void firePickupCreate(int x, int y) {
        gameClient.onPickupCreate(x, y);
    }
    
    /**
     * Notifica al cliente que un jugador se ha ido.
     * @param playerId 
     */
    public void firePlayerLeave(String playerId) {
        gameClient.onPlayerLeave(playerId);
    }
    
    /**
     * Notifica al cliente el estado inicial.
     * @param myId ID creada por el servidor
     * @param players Jugadores actuales
     * @param blueScore Puntuación actual del equipo azul
     * @param redScore Puntuación actual del equipo rojo
     */
    public void fireReceiveInitialState(String myId, ArrayList<Tank> players, int blueScore, int redScore) {
        gameClient.onReceiveInitialState(myId, players, blueScore, redScore);
    }
}
