package GameClient.Library;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * Audio en formato .wav.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class Audio implements LineListener {
    private final Clip clip;
    private boolean repeat = false;

    public Audio(Clip clip) {
        this.clip = clip;
        this.clip.addLineListener(this);
    }
    
    /**
     * Inicia la reproducción de un audio.
     */
    public void play() {
        repeat = false;
        clip.setMicrosecondPosition(0);
        clip.start();
    }
    
    /**
     * Inicia la reproducción de un audio en bucle.
     */
    public void playLoop() {
        repeat = true;
        clip.setMicrosecondPosition(0);
        clip.start();
    }
    
    public void stop() {
        repeat = false;
        clip.stop();
    }
    
    public void dispose() {
        clip.close();
    }

    /**
     * Escuchamos el evento de LineListener para saber cuándo
     * termina el audio, para poder repetirlo si es necesario.
     * @param event 
     */
    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
         
        if (type == LineEvent.Type.STOP) {
            if(repeat) {
                clip.setMicrosecondPosition(0);
                clip.start();
            }
        }        
    }
}
