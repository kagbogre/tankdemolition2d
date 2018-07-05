package GameClient.Networking.Library;
/**
 * 
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public class Protocol {
    public class Messages {
        public final static int INITIAL_STATE = 0x00;
        public final static int PLAYER_JOIN = 0x01;
        public final static int PLAYER_MOVE = 0x02;
        public final static int PLAYER_LEAVE = 0x03;
        public final static int PROJECTILE_CREATE = 0x04;
        public final static int PICKUP_CREATE = 0x05;
        public final static int PICKUP_COLLECTED = 0x06;
        public final static int LIVES_UPDATE = 0x07;
        public final static int SCORE_UPDATE = 0x08;
    }

    public class Responses {}
    
    public class Teams {
        public final static int BLUE = 0x00;
        public final static int RED = 0x01;
    }
}
