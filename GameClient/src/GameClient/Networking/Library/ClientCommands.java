package GameClient.Networking.Library;

import GameClient.Networking.Library.Interfaces.IClientCommand;
import GameClient.Networking.Library.Exceptions.UnknownCommandException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Clase que almacena todos los comandos y los ejecuta.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class ClientCommands {
    final HashMap<Integer, IClientCommand> commands;

    public ClientCommands() {
        commands = new HashMap<>();
    }

    /**
     * Añade un comando.
     *
     * @param messageType
     * @param command
     */
    public void addCommand(int messageType, IClientCommand command) {
        commands.put(messageType, command);
    }

    /**
     * Ejecuta un comando.
     * @param messageType
     * @throws GameClient.Networking.Library.Exceptions.UnknownCommandException
     * @throws IOException 
     */
    public void execute(int messageType) throws UnknownCommandException, IOException {
        IClientCommand command = commands.get(messageType);

        if (command != null) {
            command.execute();
        } else {
            throw new UnknownCommandException("Cliente: El comando con header " + messageType + " no existe");
        }
    }
}
