package GameClient.Networking.Library.Interfaces;

import GameClient.Entities.Tank;
import java.util.ArrayList;

/**
 *
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public interface IGameSocketListener {
    public void onDisconnect();
    public void onReceiveInitialState(String myId, ArrayList<Tank> players, int blueScore, int redScore);
    public void onPlayerJoin(Tank player);
    public void onPlayerLeave(String playerId);
    public void onPlayerMove(String playerId, double x, double y, int rotation);
    public void onProjectileCreate(String playerId, int x, int y, int rotation);
    public void onPickupCreate(int x, int y);
}
