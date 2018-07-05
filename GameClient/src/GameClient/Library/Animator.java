package GameClient.Library;

import java.util.HashMap;

/**
 * Animator o controlador de animaciones.
 * Cada animación tiene su identificativo de estado.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class Animator {
    private HashMap<String, Animation> animations;
    
    private Entity entity;
    private String state;
    
    public Animator(Entity entity) {
        this.entity = entity;
        
        animations = new HashMap<>();
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    /**
     * Crea un estado con una animación.
     * @param state
     * @param animation 
     */
    public void addAnimation(String state, Animation animation) {
        animation.setAnimator(this);
        animations.put(state, animation);
    }
    
    /**
     * Comienza con el primer estado.
     * @param state 
     */
    public void start(String state) {
        this.state = state;
        
        if(animations.size()>0) {
            setState(state);
        }
    }
    
    public void stop() {
        Animation animation = animations.get(state);

        animation.stop();
    }

    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        (animations.get(this.state)).stop();
        
        this.state = state;

        (animations.get(state)).play(); 
    }
}
