package GameServer.Library;

import GameServer.Library.Exceptions.UnknownCommandException;
import GameServer.Library.Interfaces.IServerCommand;
import java.io.IOException;
import java.util.HashMap;

/**
 * Clase que almacena todos los comandos y los ejecuta.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class ServerCommands {
    private final HashMap<Integer, IServerCommand> commands;

    public ServerCommands() {
        commands = new HashMap<>();
    }

    /**
     * Añade un comando.
     *
     * @param messageType
     * @param command
     */
    public void addCommand(int messageType, IServerCommand command) {
        commands.put(messageType, command);
    }

    /**
     * Ejecuta un comando.
     * @param messageType
     * @throws GameServer.Library.Exceptions.UnknownCommandException
     * @throws IOException 
     */
    public void execute(int messageType) throws UnknownCommandException, IOException {
        IServerCommand command = commands.get(messageType);

        if (command != null) {
            command.execute();
        } else {
            throw new UnknownCommandException("Servidor: El comando con header " + messageType + " no existe");
        }
    }
}
