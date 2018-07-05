package GameClient;

import java.awt.Font;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;

/**
 * Variables del juego.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class GameVariables {
    public static int VIEWPORT_WIDTH = 900; 
    public static int VIEWPORT_HEIGHT = 640;
    public static int FPS = 0;
    public static int TICKS = 0;
    public static int FRAME_COUNT = 0;
    public static int SCORE_BLUE = 0;
    public static int SCORE_RED = 0;
    public static int PICKUP_FIRE_INTERVAL_CHANGE = 100;
    public static int PICKUP_MOVE_SPEED_CHANGE = 50;  
    public static Font NORMAL_FONT_BOLD = new Font("SanSerif", Font.BOLD, 24);
    public static Font SMALL_FONT_BOLD = new Font("SanSerif", Font.BOLD, 14);
    public static Font NORMAL_FONT_REGULAR = new Font("SanSerif", Font.PLAIN, 24);
    public static Font SMALL_FONT_REGULAR = new Font("SanSerif", Font.PLAIN, 14);
    public static ResourceBundle BUNDLE = getBundle("GameClient/Bundle");
}
