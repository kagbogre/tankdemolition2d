package GameClient.Library;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

/**
 * Sprite.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class Sprite {
    private final Image image;
    private String name;
    
    public int rotation = 0;
    
    private int offsetX = 0;
    private int offsetY = 0;
    
    public Sprite(Image image, int rotation) {
        this.image = image;
        this.rotation = rotation;
    }
    
    public int getWidth() { return image.getWidth(null); }
    public int getHeight() { return image.getHeight(null); }
    public Image getImage() { return image; }
    public void setRotation(int rotation) { this.rotation = rotation; }
    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
    
    /**
     * Dibuja el Sprite en un contexto gráfico y posición determinados.
     * Aplica también la rotación deseada.
     * @param g
     * @param x
     * @param y 
     * @param oldX 
     * @param oldY 
     */
    public void draw(Graphics2D g, int x, int y, int oldX, int oldY) {
        AffineTransform transform = new AffineTransform();
        transform.rotate(
            Math.toRadians(rotation), 
            oldX + image.getWidth(null)/2, 
            oldY + image.getHeight(null)/2
        );
        
        transform.translate(x+offsetX, y+offsetY);
        g.drawImage(image, transform, null);
    }
    
    public void setOffset(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}
