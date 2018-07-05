package GameServer.Library.Interfaces;

import java.io.IOException;

/**
 * Interfaz de un comando de servidor.
 * @author Administrator
 */
public interface IServerCommand {
    public void execute() throws IOException;
}
