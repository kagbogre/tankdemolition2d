/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer;

import GameServer.Library.Exceptions.UnknownCommandException;
import GameServer.Library.Interfaces.IUDPCommand;
import GameServer.Library.UDPCommands;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author kagbogre
 */
public class UDPMessageHandler extends Thread {
    
    private DatagramSocket socket;
    private byte[] buffer = new byte[256];
    private String address;
    
    private final UDPCommands commands;
    
    private final GameServer gameServer;
    
    public UDPMessageHandler(GameServer gameServer) throws SocketException {
        this.socket = new DatagramSocket();
        this.gameServer = gameServer;
        
        commands = new UDPCommands();
        commands.addCommand(0, new PlayerMoveCommand());
    }
    
    @Override
    public void run() {
       while(true) {
           DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
           
           byte[] data = packet.getData();
           
           try {
                socket.receive(packet);
                InetAddress addr = packet.getAddress();
                int port = packet.getPort();

                packet = new DatagramPacket(buffer, buffer.length, addr, port);

                DataInputStream inputStream = new DataInputStream(
                    new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength())
                );
                
                int messageType = inputStream.readInt();
                
                commands.execute(messageType, inputStream, packet);
           } catch (IOException | UnknownCommandException ignored) {}
       }
    }
    
    private class PlayerMoveCommand implements IUDPCommand {

        @Override
        public void execute(DataInputStream inputStream, DatagramPacket packet) throws IOException {
            
        }
    }
}
