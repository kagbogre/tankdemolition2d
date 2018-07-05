package GameClient.Entities;

import GameClient.GameClient;
import GameClient.GameVariables;
import GameClient.Library.Animation;
import GameClient.Library.Animator;
import GameClient.Library.Entity;
import GameClient.Library.Interfaces.IAnimated;
import GameClient.Networking.Library.Protocol;
import GameClient.Resources.SpriteManager;
import java.awt.Graphics2D;
import java.awt.Point;
import GameClient.Library.Interfaces.IMouseListener;
import java.awt.Cursor;

/**
 * Entidad de un jugador.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class Tank extends Entity implements IAnimated, IMouseListener {
    private long lastProjectileFired;
    private int pickupsCollected;
    private int lives;
    private final String id;
    private final int team;
    
    private Animator animator;
    
    // <editor-fold defaultstate="collapsed" desc="Getters y setters">
    public int getPickupsCollected() { return pickupsCollected; }
    public boolean isDead() { return lives==0; }
    public void addLife() { if(lives < 4) lives++; } 
    public int getTeam() { return team; }
    public String getId() { return id; }
    public int getLives() { return lives; }
    public void setLives(int lives) {
        this.lives = lives;
        
        if(this.isDead()) {
            if(gameClient.getDebugEntity()==this) 
                gameClient.setDebugEntity(null);
            gameClient.removeEntity(this);
        }        
    }
    // </editor-fold>
    
    public Tank(
        GameClient game, 
        String uref, 
        String id, 
        int x, 
        int y, 
        int rotation, 
        int lives, 
        int pickupsCollected, 
        int team
    ) {
        super(game, team == Protocol.Teams.RED ? "Tanks/tank_red.png" : "Tanks/tank_blue.png", uref, x, y, rotation);
        this.lives = lives;
        this.pickupsCollected = pickupsCollected;
        this.id = id;
        this.lastProjectileFired = 0;
        this.team = team;
        
        animator = new Animator(this);
        
        Animation explosionAnimation = new Animation();
       
        explosionAnimation.setOffset(-80, -40);
        explosionAnimation.addSteps(9, "Explosion/$.png", uref, 75, rotation);
        
        animator.addAnimation("die", explosionAnimation);
    }
    
    public void addPickupCollected() {
        gameClient.updatePickupCollected(id);
        pickupsCollected++;
    }
    
    public void subtractLife() {
        if(lives > 0) {
            lives--;            
        }
        
        if(isDead()) {
            animator.start("die");
        }
        
        gameClient.updateLives(id, lives, team);
    }

    @Override
    public void move(long delta) {
        if ((dx < 0) && (x < 10)) {
            return;
        }

        if ((dx > 0) && (x > GameVariables.VIEWPORT_WIDTH - 90)) {
            return;
        }
        
        if((dy < 0) && (y < 0)) {
            return;
        }
        
        if((dy > 0) && (y > GameVariables.VIEWPORT_HEIGHT - 105)) {
            return;
        }
        
        if(dx < 0) {
            // yendo a la izquierda
            setRotation(270);
        }
        
        if(dx > 0) {
            // yendo a la derecha
            setRotation(90);
        }
        
        if(dy < 0) {
            // yendo arriba
            setRotation(0);
        }
        
        if(dy > 0) {
            // yendo abajo
            setRotation(180);
        }       
        
        if(dy < 0 && dx > 0) {
            // arriba y derecha
            setRotation(45);
        }
        
        if(dy > 0 && dx > 0) {
            // abajo y derecha
            setRotation(135);
        }
        
        if(dy < 0 && dx < 0) {
            // arriba e izquierda
            setRotation(315);
        }
        
        if(dy > 0 && dx < 0) {
            // abajo e izquierda
            setRotation(225);
        }

        super.move(delta);
    }
    
    @Override
    public void draw(Graphics2D g) {
        g.setFont(GameVariables.NORMAL_FONT_BOLD);
        
        String tempId = "Tank " + id.substring(id.length() - 4).toUpperCase();

        g.drawString(tempId, (int)x - (g.getFontMetrics().stringWidth(tempId) / 2), (int)y);  
        
        for (int i = lives; i > 0; i--) {
            g.drawImage(
                SpriteManager.getSprite("heart.png", "", 0).getImage(), 
                (int)x - (i * 20), 
                (int)y + 2, 
                null
            );       
        }
        
        for (int i = 0; i < pickupsCollected; i++) {
            g.drawImage(
                SpriteManager.getSprite("bolt_small.png", "", 0).getImage(), 
                ((int)x - 30) - (i * 20), 
                ((int)y + 20), 
                null
            );
        }        
        
        super.draw(g);
    }
    
    /**
     * Tratar de lanzar un proyectil.
     * No la lanzará si no ha pasado en intervalo de espera.
     * @return 
     */
    public boolean tryFire() {
        if (System.currentTimeMillis() - lastProjectileFired < fireInterval) {
            return false;
        }       
        
        lastProjectileFired = System.currentTimeMillis();
        
        Projectile projectile = new Projectile(gameClient, "projectile.png", "", (int) x, (int) y, rotation);
        projectile.setOwner(this);
        
        gameClient.addEntity(projectile);
        
        return true;
    }
    
    /**
     * Cuando termine una animación.
     * @param state Estado del animator que se acaba de terminar.
     */
    @Override
    public void onAnimationCycleEnd(String state) {
        if(state.equals("die")) {
            gameClient.removeEntity(this);
            if(gameClient.getDebugEntity()!=null&&gameClient.getDebugEntity().equals(this))
                gameClient.setDebugEntity(null);
        }
    }
    
    /**
     * Si colisiona con otro tanque, volvemos a la posición anterior.
     * Si el tanque está destruido no colisiona.
     * @param other 
     */
    @Override
    public void onCollision(Entity other) {
        if(((other instanceof Tank) && !((Tank)other).isDead()) || other instanceof Obstacle) {
            // Revert back to old position
            this.x = oldX;
            this.y = oldY;
        }
    }    
    
    @Override
    public void onClick(Point point) {
        gameClient.setDebugEntity(this);
    }

    @Override
    public void onHoverStart() {
        gameClient.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    @Override
    public void onHoverEnd() {
        gameClient.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
}
