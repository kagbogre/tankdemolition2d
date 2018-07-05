package GameClient;

import GameClient.Entities.Obstacle;
import GameClient.Entities.Pickup;
import GameClient.Entities.Tank;
import GameClient.Entities.Projectile;
import GameClient.Library.Audio;
import GameClient.Library.Entity;
import GameClient.Library.Input;
import GameClient.Networking.SocketClient;
import GameClient.Networking.Library.Protocol;
import GameClient.Networking.Library.Interfaces.IGameSocketListener;
import GameClient.Library.Interfaces.IMouseListener;
import GameClient.Resources.AudioManager;
import GameClient.Resources.SpriteManager;
import GameClient.UI.GameOverScreen;
import GameClient.UI.Launcher;
import GameClient.UI.PauseMenu;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public final class GameClient extends Canvas 
    implements IGameSocketListener, MouseListener, MouseMotionListener {
    private final BufferStrategy gameBufferStrategy;

    private final ArrayList<Entity> entities;
    private final ArrayList<Entity> entityGarbageCollection;
    
    private Entity hoveredEntity;
    private Entity debugEntity;
    private Entity mainPlayer;
    private SocketClient socketClient;
    private String serverAddress;
    private PauseMenu pauseMenu;
    private Audio backgroundAudio;
    
    private final JFrame gameContainer;
    private final Timer fpsTimer;
    
    private long lastTickTime;
    private boolean gameTicking;
    
    // <editor-fold defaultstate="collapsed" desc="Getters y setters"> 
    public void setDebugEntity(Entity entity) { 
        debugEntity = entity; 
    }
    public Entity getDebugEntity() { 
        return debugEntity; 
    }
    public JFrame getContainer() { 
        return gameContainer; 
    }
    public String getServerAddress() { 
        return serverAddress; 
    }
    public void setServerAddress(String address) { 
        serverAddress = address; 
    }
    public SocketClient getSocketClient() { 
        return socketClient; 
    }
    public void setSocketClient(SocketClient socketClient) { 
        this.socketClient = socketClient; 
    }
    // </editor-fold>

    public GameClient() {
        gameTicking = true;
        
        ActionListener fpsTask = (ActionEvent evt) -> {
            GameVariables.FPS=GameVariables.FRAME_COUNT;
            GameVariables.FRAME_COUNT=0;
        };
        
        fpsTimer = new Timer(1000, fpsTask);
        fpsTimer.setRepeats(true);
        fpsTimer.start();
        
        entities = new ArrayList<>();
        entityGarbageCollection = new ArrayList<>();

        gameContainer = new JFrame(GameVariables.BUNDLE.getString("GAME_TITLE"));
        
        JPanel gamePanel = (JPanel) gameContainer.getContentPane();
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setPreferredSize(
            new Dimension(GameVariables.VIEWPORT_WIDTH, GameVariables.VIEWPORT_HEIGHT)
        );
        gamePanel.setLayout(null);
        gamePanel.add(this);
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setBounds(0, 0, GameVariables.VIEWPORT_WIDTH, GameVariables.VIEWPORT_HEIGHT);
        this.setIgnoreRepaint(true);

        gameContainer.setResizable(false);
        gameContainer.setBackground(Color.BLACK);
        gameContainer.pack();
        gameContainer.setLocationRelativeTo(null);
        gameContainer.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        this.addKeyListener(new Input());
        this.requestFocus();
        this.createBufferStrategy(2);

        gameBufferStrategy = getBufferStrategy();

        buildScene();
    }

    private void afterInitialLoad() {
        gameContainer.setVisible(true);
        backgroundAudio = AudioManager.getAudio(GameVariables.BUNDLE.getString("AUDIO_BACKGROUND"));
        backgroundAudio.playLoop();
    }
    
    private Graphics2D getGraphics2D() {
        Graphics2D g = (Graphics2D) gameBufferStrategy.getDrawGraphics();
        g.setColor(Color.black);
 
        g.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );   
        
        g.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION, 
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );
        
        g.scale(1.01, 1.01);
        
        return g;
    }
    
    private void paintBackgroundTexture(String ref, Graphics2D g) {
        Image background = SpriteManager.getSprite(ref, "", 0).getImage();

        int iw = background.getWidth(this);
        int ih = background.getHeight(this);
        
        if (iw > 0 && ih > 0) {
            for (int x = 0; x < getWidth(); x += iw) {
                for (int y = 0; y < getHeight(); y += ih) {
                    g.drawImage(background, x, y, iw, ih, this);
                }
            }
        }
    }
    
    private void readKeyboardInput() {
        if (Input.leftPressed && !Input.rightPressed) {
            mainPlayer.moveHorizontallyLeft();
            try {
                socketClient.updatePlayerMove((Tank)mainPlayer);
            } catch (IOException ignored) {}
        } 
        
        if (Input.rightPressed && !Input.leftPressed) {
            mainPlayer.moveHorizontallyRight();
            try {
                socketClient.updatePlayerMove((Tank)mainPlayer);
            } catch (IOException ignored) {}
        }

        if (Input.upPressed && !Input.downPressed) {
            mainPlayer.moveVerticallyUp();
            try {
                socketClient.updatePlayerMove((Tank)mainPlayer);
            } catch (IOException ignored) {}
        }

        if (Input.downPressed && !Input.upPressed) {
            mainPlayer.moveVerticallyDown();
            try {
                socketClient.updatePlayerMove((Tank)mainPlayer);
            } catch (IOException ignored) {}
        }        
        
        if (Input.spacePressed || Input.enterPressed) {
            boolean canFire = ((Tank)mainPlayer).tryFire();
            
            if(canFire) {
                try {
                    socketClient.updateProjectileCreate(
                        ((Tank)mainPlayer).getId(), 
                        (int)mainPlayer.getX(), 
                        (int)mainPlayer.getY(), 
                        mainPlayer.getRotation()
                    );
                } catch (IOException ignored) {}
            }
        }
        
        if(Input.escapePressed) {
            if(pauseMenu==null||pauseMenu.isDisposed()) {
                pauseMenu = new PauseMenu(this, true);
            }
        }
    }

    private void paintUIComponents(Graphics2D g) {
        g.setFont(GameVariables.NORMAL_FONT_BOLD);
        g.drawString(
            GameVariables.FPS + GameVariables.BUNDLE.getString("FRAMES_PER_SECOND"),
            10, 30
        );      
      
        // Puntuación azul
        g.drawImage(
            SpriteManager.getSprite(
                GameVariables.BUNDLE.getString("IMG_TANK_BLUE_SMALL"), "", 0
            ).getImage(),
            10, 40, null
        );
        g.drawString(String.valueOf(GameVariables.SCORE_BLUE), 50, 60);
        
        // Puntuación roja
        g.drawImage(
            SpriteManager.getSprite(
                GameVariables.BUNDLE.getString("IMG_TANK_RED_SMALL"), "", 0
            ).getImage(), 
            80, 40, null
        );
        
        g.drawString(String.valueOf(GameVariables.SCORE_RED), 120, 60);
        
        if(debugEntity!=null) {
            // Información debug
            g.setColor(Color.RED);
            g.setFont(GameVariables.SMALL_FONT_BOLD);
            g.drawString("Debug", 10, -110 + GameVariables.VIEWPORT_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawString("ID jugador: " + ((Tank)debugEntity).getId(), 10, -90 + GameVariables.VIEWPORT_HEIGHT);
            g.drawString("Coordenadas: (" + debugEntity.getX() + ", " + debugEntity.getY() + ")", 10, -70 + GameVariables.VIEWPORT_HEIGHT);   
            g.drawString("Rotación: " + debugEntity.getRotation(), 10, -50 + GameVariables.VIEWPORT_HEIGHT);   
            g.drawString("Sprite: " + debugEntity.getSprite().getName(), 10, -30 + GameVariables.VIEWPORT_HEIGHT);
        }

        // Vidas
        for (int i = 0; i < ((Tank)mainPlayer).getLives(); i++) {
            g.drawImage(
                SpriteManager.getSprite(
                    GameVariables.BUNDLE.getString("IMG_HEART"), "", 0
                ).getImage(), 
                GameVariables.VIEWPORT_WIDTH - 60 - (i*25), 15, null
            );
        }

        // Pickups obtenidos
        for (int i = 0; i < ((Tank)mainPlayer).getPickupsCollected(); i++) {
            g.drawImage(
                SpriteManager.getSprite(
                    GameVariables.BUNDLE.getString("IMG_BOLT_SMALL"), "", 0
                ).getImage(), 
                GameVariables.VIEWPORT_WIDTH - 55 - (i*25), 45, null
            );
        }      
    }
    
    private void calculateCollisions() {
        for (int p = 0; p < entities.size(); p++) {
            for (int s = p + 1; s < entities.size(); s++) {
                Entity collider = (Entity) entities.get(p);
                Entity otherCollider = (Entity) entities.get(s);

                if (collider.collidesWith(otherCollider)) {
                    collider.onCollision(otherCollider);
                    otherCollider.onCollision(collider);
                }
            }
        }        
    }

    private void cleanUpEntities() {
        entities.removeAll(entityGarbageCollection);
        entityGarbageCollection.clear();        
    }

    private void buildScene() {
        this.addEntity(
            new Obstacle(
                this, GameVariables.BUNDLE.getString("IMG_WALL_SINGLE"), 
                "", 140, 280, 0
            )
        );
        this.addEntity(
            new Obstacle(
                this, GameVariables.BUNDLE.getString("IMG_WALL_SINGLE"), 
                "", 390, 280, 0
            )
        );
        this.addEntity(
            new Obstacle(
                this, GameVariables.BUNDLE.getString("IMG_WALL_SINGLE"), 
                "", 640, 280, 0
            )
        );
    }

    private void paintEntities(long delta, Graphics2D g) {
        // Siempre utilizar for i para iterar sobre ArrayList.
        // Si no, ocurre el ConcurrentModificationException.
        
        // Mover a las entidades.
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.move(delta);
        }
        
        // Dibujar las entidades.
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.draw(g);
        }
    }

    private void dispose(Graphics g) {
        g.dispose();
        gameBufferStrategy.show();
    }

    private void loop() {

        (new Thread() {
            @Override
            public void run() {
                lastTickTime = System.currentTimeMillis();
                
                while (gameTicking) {
                    try {
                        tick();
                    } catch (InterruptedException ex) {}
                }
            }
        }).start();
    }
    
    private void resetEntityMovement() {
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            
            entity.resetMovement();
        }
    }

    private void tick() throws InterruptedException {
        if (((Tank)mainPlayer).isDead()) {
            new GameOverScreen(GameClient.this, true);
        } else {
            long delta = System.currentTimeMillis() - lastTickTime;
            lastTickTime = System.currentTimeMillis();
            Graphics2D g = getGraphics2D();
            this.paintBackgroundTexture(
                GameVariables.BUNDLE.getString("IMG_SCENE"), 
            g);
            this.paintEntities(delta, g);
            this.paintUIComponents(g);
            this.calculateCollisions();
            this.cleanUpEntities();
            this.dispose(g);
            this.resetEntityMovement();
            this.readKeyboardInput();
            GameVariables.FRAME_COUNT++;
            GameVariables.TICKS++;
            Thread.sleep(14);
        }
    }
    
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entityGarbageCollection.add(entity);
    }

    public void displayError(String message) {
        JOptionPane.showMessageDialog(
            this, 
            message, 
            GameVariables.BUNDLE.getString("ERROR"), 
            JOptionPane.ERROR_MESSAGE
        );
    }

    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public void close(boolean showLauncher) {
        if(showLauncher) new Launcher();

        gameTicking = false;
        gameBufferStrategy.dispose();
        
        if(backgroundAudio!=null) backgroundAudio.stop();
        
        fpsTimer.stop();
        gameContainer.dispose();
        
        try {
            if(socketClient.isConnected()) socketClient.forceDisconnect();
        } catch (IOException ignored) {}
    }

    // <editor-fold defaultstate="collapsed" desc="Eventos del cliente socket">

    @Override
    public void onDisconnect() {
        if(!socketClient.isForceDisconnect()) {
            displayError(GameVariables.BUNDLE.getString("MESSAGE_CLIENT_DISCONNECT"));
            close(true);            
        }  
    }

    @Override
    public void onPlayerJoin(Tank player) {
        this.addEntity(player);
    }

    @Override
    public void onPlayerMove(String playerId, double x, double y,/*double dx, double dy,*/ int rotation) {
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            
            if(entity instanceof Tank && ((Tank)entity).getId().equals(playerId)) {
                entity.setX(x);
                entity.setY(y);
                //entity.setDX(dx);
                //entity.setDY(dy);
                entity.setRotation(rotation);
            }
        }
    }
    
    @Override
    public void onProjectileCreate(String playerId, int x, int y, int rotation) {
        Projectile projectile = new Projectile(this, GameVariables.BUNDLE.getString("IMG_PROJECTILE"), "", x, y, rotation);
        
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            
            if(entity instanceof Tank && ((Tank)entity).getId().equals(playerId)) {
                projectile.setOwner(entity);
            }
        }
        
        this.addEntity(projectile);
    }

    @Override
    public void onPickupCreate(int x, int y) {
        Pickup pickup = new Pickup(this, GameVariables.BUNDLE.getString("IMG_BOLT"), "", x, y, 0);
        
        this.addEntity(pickup);
    }

    @Override
    public void onReceiveInitialState(String myId, ArrayList<Tank> players, int blueScore, int redScore) {
        entities.addAll(players);

        GameVariables.SCORE_BLUE = blueScore;
        GameVariables.SCORE_RED = redScore;
        
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            
            if(entity instanceof Tank && ((Tank)entity).getId().equals(myId)) {
                mainPlayer = (Tank) entity;
            }            
        }
        
        this.afterInitialLoad();
        this.loop();
    }

    @Override
    public void onPlayerLeave(String playerId) {
        for (int i = 0; i < entities.size(); i++) {
           Entity entity = entities.get(i);

           if(entity instanceof Tank && ((Tank)entity).getId().equals(playerId)) {
                if(this.getDebugEntity()!=null&&this.getDebugEntity().equals(entity)) 
                    this.setDebugEntity(null);
                
                this.removeEntity(entity);
           }            
        }
    }
    
    public void updatePickupCollected(String playerId) {
        try {
            if (playerId.equals(((Tank)mainPlayer).getId())) {
                socketClient.updatePickupCollected(playerId);
            }
        } catch (IOException ignored) {}
    }
    
    /**
     * Avisa al cliente socket un cambio en las vidas de un jugador.
     * También aumenta la puntuación del equipo contrario si las vidas es == 0.
     * @param playerId ID del jugador
     * @param lives Vidas
     * @param team Equipo del jugador
     */
    public void updateLives(String playerId, int lives, int team) {
        try {
            if (playerId.equals(((Tank)mainPlayer).getId())) {
                socketClient.updateLives(playerId, lives);
            }

            if (lives == 0) {
                if (team == Protocol.Teams.RED) {
                    GameVariables.SCORE_BLUE++;
                }

                if (team == Protocol.Teams.BLUE) {
                    GameVariables.SCORE_RED++;
                }
                
                if (playerId.equals(((Tank)mainPlayer).getId())) {
                    socketClient.updateScore(((Tank)mainPlayer).getTeam() == Protocol.Teams.BLUE ? Protocol.Teams.RED : Protocol.Teams.BLUE);
                }
            }
        } catch (IOException ignored) {}
    }
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc="Eventos del ratón"> 
    private ArrayList<Entity> getMouseAffectedEntities(Point point) {
        ArrayList<Entity> mouseAffectedEntities = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            int x = (int) entity.getX();
            int y = (int) entity.getY();

            int height = entity.getSprite().getHeight();
            int width = entity.getSprite().getWidth();

            if(entity instanceof IMouseListener) {
                if((point.x >= x && point.x <= (x+width))&&(point.y >= y && point.y <= (y+height))) {
                    mouseAffectedEntities.add(entity);
                }
            }
        } 
        
        return mouseAffectedEntities;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        Point point = e.getPoint();
       
        ArrayList<Entity> affected = getMouseAffectedEntities(point);

        for (int i = 0; i < affected.size(); i++) {
            Entity entity = affected.get(i);
            
            ((IMouseListener) entity).onClick(point);
        }  
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        Point point = e.getPoint();
        
        ArrayList<Entity> affected = getMouseAffectedEntities(point);
        
        if(hoveredEntity!=null) {
            if(affected.indexOf(hoveredEntity)==-1) {
                ((IMouseListener)hoveredEntity).onHoverEnd();
                hoveredEntity = null;
            }
        } else {
            if(affected.size()>0) {
                hoveredEntity = affected.get(0);

                ((IMouseListener)hoveredEntity).onHoverStart();                
            }
        }    
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    
    // </editor-fold>
}
