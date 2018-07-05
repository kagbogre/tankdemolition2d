package GameClient.Entities;

import GameClient.GameClient;
import GameClient.Library.Entity;

/**
 * Obstáculo (paredes).
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class Obstacle extends Entity {
    public Obstacle(
        GameClient game, 
        String ref, 
        String uref, 
        int x, 
        int y, 
        int rotation
    ) {
        super(game, ref, uref, x, y, rotation);
    }

    @Override
    public void onCollision(Entity other) {}
}
