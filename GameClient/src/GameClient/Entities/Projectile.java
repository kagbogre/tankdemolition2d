package GameClient.Entities;

import GameClient.GameClient;
import GameClient.GameVariables;
import GameClient.Library.Audio;
import GameClient.Library.Entity;
import GameClient.Resources.AudioManager;

/**
 * Proyectil lanzado por los jugadores.
 * El punto de partida es la posición del tanque.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class Projectile extends Entity {
    private Entity owner;
    private final int currentTickCount;
    
    public Projectile(
        GameClient game, 
        String ref, 
        String uref, 
        int x, 
        int y, 
        int rotation
    ) {
        super(game, ref, uref, x+10, y+30, rotation);
        
        Audio audio = AudioManager.getAudio("blast.wav");
        audio.play(); 
        
        currentTickCount = GameVariables.TICKS;
    }
    /**
     * Define el dueño de este proyectil (al que no matará).
     * @param entity 
     */
    public void setOwner(Entity entity) {
        owner = entity;
    }
    
    /**
     * Movimiento especial para proyectiles.
     * Overridea el método por completo y no llama al super.
     * @param delta 
     */
    @Override
    public void move(long delta) {
        // Hace falta definir las posiciones actuales antes de modificarlas.
        // para poder hacer un renderizado correcto de la rotación.
        oldX = x;
        oldY = y;
        
        // Aplicamos el movimiento directamente, no modificamos dx ni dy.
        // Los proyectiles no cambian mágicamente de rotación.
        y += -Math.cos(Math.toRadians(rotation)) * 10;
        x += Math.sin(Math.toRadians(rotation)) * 10;        

        // Destruimos después de 100 ticks.
        if(GameVariables.TICKS - currentTickCount > 99) {
            gameClient.removeEntity(this);                
        }
    }
    
    /**
     * Cuando haya colisión, matar al otro.
     * @param other 
     */
    @Override
    public void onCollision(Entity other) {
        if(!other.equals(owner) && !(other instanceof Projectile) && !(other instanceof Pickup)) {
            
            if(other instanceof Tank) {
                ((Tank) other).subtractLife();
                
                Audio audio = (((Tank)other).getLives() > 0) ? 
                    AudioManager.getAudio("impact.wav") : 
                    AudioManager.getAudio("boom.wav");

                if (audio != null) {
                    audio.play();
                }
            } else if(!(other instanceof Obstacle)) {
                gameClient.removeEntity(other);
            } else {
                (AudioManager.getAudio("impact.wav")).play();
            }

            gameClient.removeEntity(this);
        }
    }
}
