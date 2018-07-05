package GameClient.Resources;

import GameClient.Library.Audio;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Obtiene Audio de una forma centralizada.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class AudioManager {
    private static HashMap<String, Audio> audio;
    
    /**
     * Obtiene un archivo de audio y lo cachea.
     * @param ref Referencia de archivo
     * @return 
     */
    public static Audio getAudio(String ref) {
        if(audio == null) {
            audio = new HashMap<>();
        } else {
            if(audio.get(ref) != null) {
                return audio.get(ref);
            }            
        }
        
        Clip clip;
        
        try {
            URL url = AudioManager.class.getResource("Audio/" + ref);
            
            if(url == null) {
                System.out.println("Audio " + ref + " could not be loaded!");
                return null;
            }
            
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            
            Audio wav = new Audio(clip);
            
            audio.put(ref, wav);
            
            return wav;
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
            System.out.println(ex.getMessage());
            return null;
        } 
    }
}
