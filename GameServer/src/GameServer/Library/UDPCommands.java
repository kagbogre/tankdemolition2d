package GameServer.Library;

import GameServer.Library.Exceptions.UnknownCommandException;
import GameServer.Library.Interfaces.IUDPCommand;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;

/**
 * Clase que almacena todos los comandos y los ejecuta.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class UDPCommands {
    private final HashMap<Integer, IUDPCommand> commands;

    public UDPCommands() {
        commands = new HashMap<>();
    }

    /**
     * Añade un comando.
     *
     * @param messageType
     * @param command
     */
    public void addCommand(int messageType, IUDPCommand command) {
        commands.put(messageType, command);
    }

    /**
     * Ejecuta un comando.
     * @param messageType
     * @throws GameServer.Library.Exceptions.UnknownCommandException
     * @throws IOException 
     */
    public void execute(int messageType, DataInputStream inputStream, DatagramPacket packet) throws UnknownCommandException, IOException {
        IUDPCommand command = commands.get(messageType);

        if (command != null) {
            command.execute(inputStream, packet);
        } else {
            throw new UnknownCommandException("Servidor: El comando UDP con header " + messageType + " no existe");
        }
    }
}
