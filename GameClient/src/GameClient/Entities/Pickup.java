package GameClient.Entities;

import GameClient.GameClient;
import GameClient.GameVariables;
import GameClient.Library.Entity;

/**
 * Coleccionable que aumenta la velocidad de disparo y 
 * movimiento de un jugador.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class Pickup extends Entity {

    public Pickup(
        GameClient game, 
        String ref, 
        String uref, 
        int x, 
        int y, 
        int rotation
    ) {
        super(game, ref, uref, x, y, rotation);
    }

    /**
     * Si colisiona con un jugador, añadirle los efectos del objeto.
     * @param other 
     */
    @Override
    public void onCollision(Entity other) {
        if(other instanceof Tank) {
            if(((Tank)other).getPickupsCollected() < 4) {
                other.setFireInterval(other.getFireInterval()-GameVariables.PICKUP_FIRE_INTERVAL_CHANGE);  
                other.setMoveSpeed(other.getMoveSpeed()+GameVariables.PICKUP_MOVE_SPEED_CHANGE);  
                ((Tank)other).addPickupCollected();
            }  
            gameClient.removeEntity(this);
        }
    }
}
