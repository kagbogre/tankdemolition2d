package GameClient.Networking.Library.Interfaces;

import java.io.IOException;

/**
 * Interfaz para los comandos de cliente.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public interface IClientCommand {
    public void execute() throws IOException;
}
