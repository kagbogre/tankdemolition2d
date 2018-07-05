package GameClient.Library;

import GameClient.Resources.SpriteManager;
import GameClient.GameClient;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Entidad - los elementos más importantes del juego.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public abstract class Entity {
    protected double x;
    protected double y;
    private Sprite sprite;
    protected double dx;
    protected double dy;
    private double moveSpeed = 225;
    protected double fireInterval = 700;
    protected int rotation = 0;
    
    protected double oldX;
    protected double oldY;

    protected final GameClient gameClient;
    private final Rectangle collider = new Rectangle();
    private final Rectangle otherCollider = new Rectangle();
    
    // <editor-fold defaultstate="collapsed" desc="Getters y setters"> 
    public double getX() { return x; }
    public double getY() { return y; }
    public double getDX() { return dx; }
    public double getDY() { return dy; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setDX(double dx) { this.dx = dx; }
    public void setDY(double dy) { this.dy = dy; }
    public Sprite getSprite() { return sprite; }
    public void setSprite(Sprite sprite) { this.sprite = sprite; }
    public int getRotation() { return this.rotation; }
    public void setMoveSpeed(double df) { this.moveSpeed = df; }
    public double getMoveSpeed() { return this.moveSpeed; }    
    public void setFireInterval(double df) { this.fireInterval = df; }
    public double getFireInterval() { return this.fireInterval; }
    public double getHorizontalMovement() { return dx; }
    public void moveHorizontallyRight() { this.dx+=moveSpeed; }
    public void moveHorizontallyLeft() { this.dx-=moveSpeed; }
    public double getVerticalMovement() { return dy; }
    public void moveVerticallyUp() { this.dy-=moveSpeed; }
    public void moveVerticallyDown() { this.dy+=moveSpeed; }
    public void resetMovement() { this.dy = 0; this.dx = 0; }
    // </editor-fold>
    
    /**
     * Constructor de la entidad.
     * @param gameClient Cliente que pintará la entidad.
     * @param ref Referencia al archivo sprite.
     * @param uref Identificador único de sprite.
     * @param x Posición de la entidad en el eje X.
     * @param y Posición de la entidad en el eje Y.
     * @param rotation Rotación inicial de la entidad en grados.
     */
    public Entity(GameClient gameClient, String ref, String uref, double x, double y, int rotation) {
        this.x = x;
        this.y = y;        
        this.gameClient = gameClient;
        this.sprite = SpriteManager.getSprite(ref, uref, rotation);
        this.rotation = rotation;
        
        if(this.sprite==null) {
            gameClient.displayError("There was an error loading the sprite " + ref + ".");
            System.exit(0);
        }
    }
    
    /**
     * Modifica la posición de la entidad.
     * Aplica la rotación al sprite.
     * @param delta 
     */
    public void move(long delta) {
        oldX = (int)x;
        oldY = (int)y;
        
        x += (int)(delta * dx) / 1000;
        y += (int)(delta * dy) / 1000;
    }
    
    /**
     * Actualiza la rotación de la entidad y de su sprite actual.
     * @param rotation Rotación en grados.
     */
    public void setRotation(int rotation) {
        this.rotation = rotation;
        sprite.setRotation(rotation);
    }
    
    /**
     * Dibuja la entidad en el contexto gráfico.
     * @param g 
     */
    public void draw(Graphics2D g) {
        sprite.draw(g, (int)x, (int)y, (int)oldX, (int)oldY);
    }
    
    /**
     * Comprueba si hay colisión.
     * @param other
     * @return 
     */
    public boolean collidesWith(Entity other) {
        collider.setBounds(
            (int)x, (int)y, 
            sprite.getWidth(), 
            sprite.getHeight()
        );
        
        otherCollider.setBounds(
            (int)other.getX(), 
            (int)other.getY(), 
            (int)other.getSprite().getWidth(), 
            (int)other.getSprite().getHeight()
        );
   
        return collider.intersects(otherCollider);
    }
    
    /**
     * Notifica a la entidad que se ha colisionado con otra entidad.
     * @param other 
     */
    public abstract void onCollision(Entity other);
}
