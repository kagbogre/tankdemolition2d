package GameServer.Library.Exceptions;

/**
 * Excepción cuando no existe el comando.
 * @author Administrator
 */
public class UnknownCommandException extends Exception {
    public UnknownCommandException(String message) {
        super(message);
    }
}
