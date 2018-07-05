package GameClient.Networking.Library.Exceptions;

/**
 * Excepción cuando no existe el comando.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class UnknownCommandException extends Exception {
    public UnknownCommandException(String message) {
        super(message);
    }
}
