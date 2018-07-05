package GameClient.Library;

import GameClient.Library.Interfaces.IAnimated;
import GameClient.Resources.SpriteManager;
import java.util.ArrayList;

/**
 * Animación de entidades.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class Animation {
    class AnimationStep {
        int duration;
        Sprite sprite;
        
        public int getDuration() {
            return duration;
        }
        public Sprite getSprite() {
            return sprite;
        }
        
        AnimationStep(Sprite sprite, int duration) {
            this.duration = duration;
            this.sprite = sprite;
        }
    }
    
    private ArrayList<AnimationStep> steps;
    
    private boolean loop;
    private boolean stopped;
    
    private Animator animator;
    
    private int count;
    private int offsetX;
    private int offsetY;
    
    public Animation() {
        count = 0;
        offsetX = 0;
        offsetY = 0;
        loop = false;
        stopped = true;
        steps = new ArrayList<>();
    }
    
    /**
     * Animator que ejecutará esta animación.
     * @param animator 
     */
    public void setAnimator(Animator animator) {
        this.animator = animator;
    }
    
    /**
     * Indica si la animación será en bucle.
     * Se repetirá hasta que se haga stop().
     * @param loop 
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
   
    /**
     * Añade un step de la animación.
     * La animación finaliza cuando pasa por todos los steps.
     * @param sprite
     * @param duration Duración de este step (ms).
     */
    public void addStep(Sprite sprite, int duration) {
        steps.add(new AnimationStep(sprite, duration));
    }
    
    /**
     * Añade un determinado número de steps.
     * @param count Número de sprites.
     * @param ref Referencia al archivo del sprite.
     * @param uref Referencia única del sprite.
     * @param duration Duración de cada step (ms).
     * @param rotation Rotación inicial del sprite.
     */
    public void addSteps(int count, String ref, String uref, int duration, int rotation) {
        for (int i = 0; i < count; i++) {
            Sprite sprite = SpriteManager.getSprite(
                ref.replace('$', String.valueOf(i).charAt(0)), uref + i, rotation
            );

            if (sprite != null) {
                sprite.setOffset(offsetX, offsetY);
                steps.add(new AnimationStep(sprite, duration));
            }
        }
    }
    
    /**
     * Definir un offset para que la animación aparezca exactamente
     * donde queremos.
     * @param offsetX Offset en el eje X.
     * @param offsetY Offset en el eje Y.
     */
    public void setOffset(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    
    /**
     * Circula por todos los frames hasta que termine, o 
     * continúa en bucle si así está establecido.
     */
    private void animate() {
        if(!stopped) {
            if(count==steps.size()) { 
                if(animator.getEntity() instanceof IAnimated) {
                    ((IAnimated) animator.getEntity())
                        .onAnimationCycleEnd(animator.getState());
                }
                
                if(loop) {
                    count = 0;
                } else {
                    stopped = true;
                    return;                    
                }
            }

            AnimationStep step = steps.get(count);
            
            animator.getEntity().setSprite(step.getSprite());
            
            count++;
            
            setTimeout(() -> {
                animate();
            }, step.getDuration());
        }
    }
    
    /**
     * Ejecuta código después de x milisegundos.
     * @param runnable
     * @param delay 
     */
    private void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (InterruptedException e){
                System.err.println(e);
            }
        }).start();
    }
    
    public void play() {
        stopped = false;
        count = 0;
        
        if(steps.size()>0) {
            animate();
        }
    }
    
    public void stop() {
        stopped = true;
        count = 0;
    }
}
