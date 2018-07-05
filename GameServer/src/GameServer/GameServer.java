package GameServer;

import GameServer.Library.Protocol;
import GameServer.Models.Player;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import javax.swing.Timer;

/**
 *
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class GameServer {
    private ArrayList<Player> players;
    private int blueScore = 0;
    private int redScore = 0;

    public GameServer() {
        players = new ArrayList<>();
        
        ActionListener collectibleTask = (ActionEvent evt) -> {
            try {
                broadcastPickup(
                    50 + (int)(Math.random() * ((750 - 50) + 1)),
                    50 + (int)(Math.random() * ((550 - 50) + 1))
                );
            } catch (IOException ex) {}
        };
        
        Timer collectibleTimer = new Timer(30000, collectibleTask);
        collectibleTimer.setRepeats(true);
        collectibleTimer.start();
    }

    public void listen() {

        try {
            UDPMessageHandler udpWorker = new UDPMessageHandler(this);
            udpWorker.start();
            ServerSocket socket = new ServerSocket(2424, 100);

            System.out.println("Escuchando en el puerto " + 2424);

            while (true) {
                Socket client = socket.accept();

                ClientMessageHandler worker = new ClientMessageHandler(this, client);

                worker.start();
            }
        } catch (IOException ex) {
            System.out.println("No se pudo crear el ServerSocket");
        }
    }

    /**
     * Crea una nueva entidad jugador.
     * @param playerSocket
     * @return
     * @throws IOException 
     */
    public Player createPlayer(Socket playerSocket) throws IOException {
        String id = UUID.randomUUID().toString();

        int team = getTeam();
        //900x640
        int randomX = 25 + (int) (Math.random() * ((850 - 25) + 1));

        int randomY = team==Protocol.Teams.BLUE ? 
                25 + (int) (Math.random() * ((220 - 25) + 1)) : 
                    390 + (int) (Math.random() * ((580 - 390) + 1));

        int rotation = team==Protocol.Teams.BLUE ? 180 : 0;
        // calcular equipo
        Player player = new Player(id, randomX, randomY, rotation, 4, 0, team, playerSocket);

        broadcastJoin(player);
        
        players.add(player);

        return player;
    }
    
    private int getTeam() {
        int blue = 0;
        int red = 0;
        
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            
            if(player.getTeam() == Protocol.Teams.RED) {
                red++;
            }
            
            if(player.getTeam() == Protocol.Teams.BLUE) {
                blue++;
            }
        }
        
        if(blue > red) {
            return Protocol.Teams.RED;
        } else if(blue < red) {
            return Protocol.Teams.BLUE;
        } else {
            return (new Random()).nextInt(1)==0 ? Protocol.Teams.RED : Protocol.Teams.BLUE;
        }
    }

    public static void main(String[] args) {
        GameServer server = new GameServer();

        server.listen();
    }
    
    /**
     * Añade un jugador a todos los clientes.
     * @param newPlayer
     * @throws IOException 
     */
    public void broadcastJoin(Player newPlayer) throws IOException {
        for (int i = 0; i < players.size(); i++) {
            
            Player player = players.get(i);
            
            Socket playerSocket = player.getSocket();

            DataOutputStream os = new DataOutputStream(playerSocket.getOutputStream());

            os.writeInt(Protocol.Messages.PLAYER_JOIN);
            os.writeUTF(newPlayer.getId());
            os.writeInt((int)newPlayer.getX());
            os.writeInt((int)newPlayer.getY());
            os.writeInt(newPlayer.getRotation());
            os.writeInt(newPlayer.getLives());
            os.writeInt(newPlayer.getPickupsCollected());
            os.writeInt(newPlayer.getTeam());
            os.flush();
        }
    }

    /**
     * Actualiza la posición de un jugador en todos los clientes.
     * @param playerId
     * @param x
     * @param y
     * @param rotation
     * @throws IOException 
     */
    public void broadcastMove(String playerId, double x, double y, int rotation) throws IOException {
        for (int i = 0; i < players.size(); i++) {

            Player player = players.get(i);

            if (!playerId.equals(player.getId())) {
                Socket playerSocket = player.getSocket();

                DataOutputStream os = new DataOutputStream(playerSocket.getOutputStream());

                os.writeInt(Protocol.Messages.PLAYER_MOVE);
                os.writeUTF(playerId);
                os.writeDouble(x);
                os.writeDouble(y);
                os.writeInt(rotation);
                os.flush();
            } else {
                player.setX(x);
                player.setY(y);
                player.setRotation(rotation);
            }
        }
    }
    
    /**
     * 
     * @param playerId
     * @param x
     * @param y
     * @param rotation
     * @throws IOException 
     */
    public void broadcastProjectile(String playerId, int x, int y, int rotation) throws IOException {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            if (!playerId.equals(player.getId())) {
                Socket playerSocket = player.getSocket();

                DataOutputStream os = new DataOutputStream(playerSocket.getOutputStream());

                os.writeInt(Protocol.Messages.PROJECTILE_CREATE);
                os.writeUTF(playerId);
                os.writeInt(x);
                os.writeInt(y);
                os.writeInt(rotation);
                os.flush();
            }
        }
    }
    
    /**
     * 
     * @param playerId
     * @throws IOException 
     */
    public void broadcastLeave(String playerId) throws IOException {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            if (playerId.equals(player.getId())) {
                players.remove(player);
            }          
        }
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            Socket playerSocket = player.getSocket();

            DataOutputStream os = new DataOutputStream(playerSocket.getOutputStream());

            os.writeInt(Protocol.Messages.PLAYER_LEAVE);
            os.writeUTF(playerId);
            os.flush();
        }  
    }
    
    /**
     * 
     * @param x
     * @param y
     * @throws IOException 
     */
    public void broadcastPickup(int x, int y) throws IOException {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            Socket playerSocket = player.getSocket();

            DataOutputStream os = new DataOutputStream(playerSocket.getOutputStream());

            os.writeInt(Protocol.Messages.PICKUP_CREATE);
            os.writeInt(x);
            os.writeInt(y);
            os.flush();
        }          
    }
    
    public void updatePickupCollected(String playerId) {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            if (playerId.equals(player.getId())) {
                player.setPickupsCollected(player.getPickupsCollected() + 1);
            }          
        }        
    }
    
    public void updateLives(String playerId, int lives) {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            if (playerId.equals(player.getId())) {
                player.setLives(lives);
            }          
        }              
    }
    
    public void updateScore(int team) {
        if(team==Protocol.Teams.BLUE) {
            blueScore++;
        } else {
            redScore++;
        }
    }

    /**
     * Manda el estado inicial al jugador que se acaba de conectar.
     * @param newPlayer
     * @param playerSocket
     * @throws IOException
     */
    public void sendInitialState(Player newPlayer, Socket playerSocket) throws IOException {
        DataOutputStream os = new DataOutputStream(playerSocket.getOutputStream());

        int playerCount = players.size();
        
        os.writeInt(Protocol.Messages.INITIAL_STATE);
        os.writeInt(playerCount);
        
        for (int i = 0; i < playerCount; i++) {
            Player player = players.get(i);
            
            os.writeUTF(player.getId());
            os.writeInt((int)player.getX());
            os.writeInt((int)player.getY());
            os.writeInt(player.getRotation());
            os.writeInt(player.getLives());
            os.writeInt(player.getPickupsCollected());
            os.writeInt(player.getTeam());
        }
        
        // la id del jugador
        os.writeUTF(newPlayer.getId());
        os.writeInt(blueScore);
        os.writeInt(redScore);
        os.flush();        
    }
}
