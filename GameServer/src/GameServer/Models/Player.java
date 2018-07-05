package GameServer.Models;

import java.net.Socket;

/**
 *
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class Player {
    double x;
    double y;
    int rotation;
    String id;
    int lives;
    int pickupsCollected;
    int team;
    final Socket socket;
    
    public Player(String id, double x, double y, int rotation, int lives, int pickupsCollected, int team, Socket socket) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.lives = lives;
        this.pickupsCollected = pickupsCollected;
        this.team = team;
        this.socket = socket;
    }
    
    public int getPickupsCollected() {
        return pickupsCollected;
    }
    
    public void setPickupsCollected(int pickupsCollected) {
        this.pickupsCollected = pickupsCollected;
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
    
    public int getRotation() {
        return rotation;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public int getTeam() {
        return team;
    }
    
    public void setTeam(int team) {
        this.team = team;
    }
    
    public int getLives() {
        return lives;
    }
    
    public void setLives(int lives) {
        this.lives = lives;
    }
}
