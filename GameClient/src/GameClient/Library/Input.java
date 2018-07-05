/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameClient.Library;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Manejo de entrada por teclado.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class Input extends KeyAdapter {
    
    // Conjunto de teclas soportadas.
    public static boolean leftPressed   = false;
    public static boolean rightPressed  = false;
    public static boolean upPressed     = false;
    public static boolean downPressed   = false;
    public static boolean spacePressed  = false;
    public static boolean escapePressed = false;
    public static boolean enterPressed  = false;
    
    /**
     * Al presionar sobre una tecla.
     * @param e 
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case (KeyEvent.VK_A):
            case (KeyEvent.VK_LEFT):
                leftPressed = true;
            break;
            
            case (KeyEvent.VK_D):
            case (KeyEvent.VK_RIGHT):
                rightPressed = true;
            break;
            
            case (KeyEvent.VK_W):
            case (KeyEvent.VK_UP):
                upPressed = true;
            break;
            
            case (KeyEvent.VK_S):
            case (KeyEvent.VK_DOWN):
                downPressed = true;
            break;
            
            case KeyEvent.VK_SPACE:
                spacePressed = true;
            break;
            
            case KeyEvent.VK_ENTER:
                enterPressed = true;
            break;
            
            case KeyEvent.VK_ESCAPE:
                escapePressed = true;
            break;
        }
    }
    
    /**
     * Al soltar una tecla.
     * @param e 
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case (KeyEvent.VK_A):
            case (KeyEvent.VK_LEFT):
                leftPressed = false;
            break;
            
            case (KeyEvent.VK_D):
            case (KeyEvent.VK_RIGHT):
                rightPressed = false;
            break;
            
            case (KeyEvent.VK_W):
            case (KeyEvent.VK_UP):
                upPressed = false;
            break;
            
            case (KeyEvent.VK_S):
            case (KeyEvent.VK_DOWN):
                downPressed = false;
            break;
            
            case KeyEvent.VK_SPACE:
                spacePressed = false;
            break;
            
            case KeyEvent.VK_ENTER:
                enterPressed = false;
            break;
            
            case KeyEvent.VK_ESCAPE:
                escapePressed = false;
            break;
        }        
    }
}
