package GameServer;

import GameServer.Library.Protocol;
import GameServer.Library.ServerCommands;
import GameServer.Library.Exceptions.UnknownCommandException;
import GameServer.Library.Interfaces.IServerCommand;
import GameServer.Models.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class ClientMessageHandler extends Thread {

    private final GameServer gameServer;
    private final Socket client;
    private final ServerCommands commands;

    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    private boolean clientConnected;
    
    ClientMessageHandler(GameServer gameServer, Socket client) throws IOException {
        clientConnected = true;
        
        System.out.println("Se ha conectado " + client.getInetAddress().getHostAddress());
        
        this.gameServer = gameServer;
        this.client = client;
        this.outputStream = new DataOutputStream(client.getOutputStream());
        this.inputStream = new DataInputStream(client.getInputStream());
        
        commands = new ServerCommands();
        commands.addCommand(Protocol.Messages.PLAYER_MOVE, new PlayerMoveCommand());
        commands.addCommand(Protocol.Messages.PROJECTILE_CREATE, new ProjectileCreateCommand());
        commands.addCommand(Protocol.Messages.PICKUP_COLLECTED, new PickupCollectedCommand());
        commands.addCommand(Protocol.Messages.LIVES_UPDATE, new LivesUpdateCommand());
        commands.addCommand(Protocol.Messages.SCORE_UPDATE, new ScoreUpdateCommand());
    }
    
    @Override
    public void run() {
        try {
            Player player = gameServer.createPlayer(client);
            gameServer.sendInitialState(player, client);
            
            while(clientConnected) {
                try {
                    int messageType = inputStream.readInt();
                    
                    commands.execute(messageType);
                } catch(IOException | UnknownCommandException ex) {
                    clientConnected = false;
                    gameServer.broadcastLeave(player.getId());
                    System.out.println("Se ha desconectado " + player.getId());
                }
            }
        } catch (IOException ex) {
            clientConnected = false;
            System.out.println("Se ha desconectado (error) " + client.getInetAddress().getHostAddress());        
        }
    }
    
    private class PlayerMoveCommand implements IServerCommand {

        @Override
        public void execute() throws IOException {
            
            String playerId = inputStream.readUTF();
            
            double x = inputStream.readDouble();
            double y = inputStream.readDouble();
            int rotation = inputStream.readInt();
            
            gameServer.broadcastMove(playerId, x, y, rotation);
        }   
    }
    
    private class ProjectileCreateCommand implements IServerCommand {

        @Override
        public void execute() throws IOException {
            String playerId = inputStream.readUTF();
            
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            int rotation = inputStream.readInt();
            
            gameServer.broadcastProjectile(playerId, x, y, rotation);
        }   
    }
    
    private class PickupCollectedCommand implements IServerCommand {
        @Override
        public void execute() throws IOException {
            String playerId = inputStream.readUTF();
            
            gameServer.updatePickupCollected(playerId);
        }
    }
    
    private class LivesUpdateCommand implements IServerCommand {
        @Override
        public void execute() throws IOException {
            String playerId = inputStream.readUTF();
            int lives = inputStream.readInt();
            
            gameServer.updateLives(playerId, lives);
        }        
    }
    
    private class ScoreUpdateCommand implements IServerCommand {

        @Override
        public void execute() throws IOException {
            int team = inputStream.readInt();
            
            gameServer.updateScore(team);
        }
        
    }
}
