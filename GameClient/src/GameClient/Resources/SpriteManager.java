package GameClient.Resources;

import GameClient.Library.Sprite;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 * Obtiene Sprites de una forma centralizada.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class SpriteManager {
    private static HashMap<String, Sprite> sprites;
    /**
     * Obtiene el sprite y lo cachea.
     * Es necesario introducir un Unique Reference para no cachear
     * la misma rotación para todas las entidades.
     *
     * @param ref Referencia de archivo
     * @param uref Referencia única
     * @param rotation Rotación del sprite
     * @return
     */
    public static Sprite getSprite(String ref, String uref, int rotation) {
        if (sprites == null) {
            sprites = new HashMap<>();
        } else {
            if (sprites.get(ref+ ":" +uref) != null) {
                return sprites.get(ref+ ":" +uref);
            }
        }

        BufferedImage sourceImage;

        try {
            URL url = SpriteManager.class.getResource("Sprites/" + ref);

            if (url == null) {
                System.out.println("Sprite " + ref + " could not be loaded!");
                return null;
            }

            sourceImage = ImageIO.read(url);

            GraphicsConfiguration config = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

            Image image = config.createCompatibleImage(
                sourceImage.getWidth(),
                sourceImage.getHeight(),
                Transparency.BITMASK
            );

            image.getGraphics().drawImage(sourceImage, 0, 0, null);

            Sprite sprite = new Sprite(image, rotation);
            sprite.setName(ref);
            sprites.put(ref+ ":" +uref, sprite);

            return sprite;

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
