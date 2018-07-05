package GameClient.Networking;

import GameClient.Entities.Tank;
import GameClient.Networking.Library.ClientCommands;
import GameClient.Networking.Library.Protocol;
import GameClient.Networking.Library.Exceptions.UnknownCommandException;
import GameClient.Networking.Library.Interfaces.IClientCommand;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class ServerMessageHandler extends Thread {

    private final ClientCommands commands;
    private final SocketClient client;
    private final DataInputStream inputStream;

    ServerMessageHandler(SocketClient socketClient, DataInputStream inputStream, DataOutputStream outputStream) {
        client = socketClient;
        
        this.inputStream = inputStream;

        commands = new ClientCommands();
        commands.addCommand(
            Protocol.Messages.INITIAL_STATE, new InitialStateCommand()
        );
        
        commands.addCommand(
            Protocol.Messages.PLAYER_JOIN, new PlayerJoinCommand()
        );
        
        commands.addCommand(
            Protocol.Messages.PLAYER_MOVE, new PlayerMoveCommand()
        );
        
        commands.addCommand(
            Protocol.Messages.PLAYER_LEAVE, new PlayerLeaveCommand()
        );
        
        commands.addCommand(
            Protocol.Messages.PROJECTILE_CREATE, new ProjectileCreateCommand()
        );
        
        commands.addCommand(
            Protocol.Messages.PICKUP_CREATE, new PickupCreateCommand()
        );
    }
    
    @Override
    public void run() {
        while (client.isConnected()) {
            try {
                int messageType = inputStream.readInt();
                
                commands.execute(messageType);
            } catch (IOException ex) {
                client.setConnected(false);
            } catch (UnknownCommandException ignored) {}
        }        
    }
    
    class InitialStateCommand implements IClientCommand {
        @Override
        public void execute() throws IOException {
            ArrayList<Tank> players = new ArrayList<>();
            
            int playerCount = inputStream.readInt();
            
            for (int i = 0; i < playerCount; i++) {
                String playerId = inputStream.readUTF();
                int x = inputStream.readInt();
                int y = inputStream.readInt();
                int rotation = inputStream.readInt();
                int lives = inputStream.readInt();
                int pickupsCollected = inputStream.readInt();
                int team = inputStream.readInt();
                
                Tank player = new Tank(
                    client.getGameClient(), 
                    playerId, 
                    playerId, 
                    x, y, 
                    rotation, 
                    lives, 
                    pickupsCollected, 
                    team
                );
                
                players.add(player);
            }
            
            String myId = inputStream.readUTF();
            
            int blueScore = inputStream.readInt();
            int redScore = inputStream.readInt();
            
            client.fireReceiveInitialState(myId, players, blueScore, redScore);
        }        
    }
    
    class PlayerJoinCommand implements IClientCommand {
        @Override
        public void execute() throws IOException {
            String playerId = inputStream.readUTF();
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            int rotation = inputStream.readInt();
            int lives = inputStream.readInt();
            int pickupsCollected = inputStream.readInt();
            int team = inputStream.readInt();
           
            client.firePlayerJoin(
                new Tank(
                    client.getGameClient(), 
                    playerId,
                    playerId, 
                    x, y, 
                    rotation, 
                    lives, 
                    pickupsCollected, 
                    team
                )
            );
        }
    }
    
    class PlayerLeaveCommand implements IClientCommand {
        @Override
        public void execute() throws IOException {
            String playerId = inputStream.readUTF();
           
            client.firePlayerLeave(playerId);
        }
    }
    
    class PlayerMoveCommand implements IClientCommand {
        @Override
        public void execute() throws IOException {
            String playerId = inputStream.readUTF();
            double x = inputStream.readDouble();
            double y = inputStream.readDouble();
            int rotation = inputStream.readInt();
            
            client.firePlayerMove(playerId, x, y, rotation);
        }
    }
    
    class ProjectileCreateCommand implements IClientCommand {
        @Override
        public void execute() throws IOException {
            String playerId = inputStream.readUTF();
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            int rotation = inputStream.readInt();
            
            client.fireProjectileCreate(playerId, x, y, rotation);
        }
    }
    
    class PickupCreateCommand implements IClientCommand {
        @Override
        public void execute() throws IOException {
            int x = inputStream.readInt();
            int y = inputStream.readInt();
            
            client.firePickupCreate(x, y);
        }        
    }
}
