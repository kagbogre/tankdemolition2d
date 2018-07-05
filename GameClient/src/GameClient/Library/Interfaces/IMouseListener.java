package GameClient.Library.Interfaces;
import java.awt.Point;

/**
 *
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public interface IMouseListener {
    public void onClick(Point point);
    public void onHoverStart();
    public void onHoverEnd();
}
