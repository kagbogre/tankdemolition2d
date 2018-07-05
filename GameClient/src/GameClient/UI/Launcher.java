package GameClient.UI;

import GameClient.GameClient;
import GameClient.GameVariables;
import GameClient.Library.Audio;
import GameClient.Networking.SocketClient;
import GameClient.Resources.AudioManager;
import java.io.IOException;

/**
 * Lanzador del juego.
 * @author Kedi Agbogre <kedi97@gmail.com>
 */
public final class Launcher extends javax.swing.JFrame {
    private final Audio audio;
    /**
     * Creates new form AWTGameUI
     */
    public Launcher() {
        initComponents();

        setTitle(GameVariables.BUNDLE.getString("GAME_TITLE"));
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        
        audio = AudioManager.getAudio(GameVariables.BUNDLE.getString("AUDIO_INTRO"));
        
        audio.play();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTextFieldServer = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GameClient/Resources/Sprites/TankDemolition.png"))); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("GameClient/Bundle"); // NOI18N
        jButton1.setText(bundle.getString("CONNECT")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextFieldServer.setText(bundle.getString("LOCALHOST")); // NOI18N
        jTextFieldServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldServerActionPerformed(evt);
            }
        });
        jTextFieldServer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldServerKeyPressed(evt);
            }
        });

        jLabel2.setText(bundle.getString("COPYRIGHT")); // NOI18N

        jLabel3.setText(bundle.getString("INPUT_SERVER_ADDRESS")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3)
                            .addComponent(jTextFieldServer, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        start();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void start() {
        //audio.dispose();
        (new Thread() {
            @Override
            public void run() {
                GameClient gameClient = new GameClient();
                SocketClient socketClient = new SocketClient(gameClient);
                  
                gameClient.setSocketClient(socketClient);
                
                try {
                    audio.stop();
                    socketClient.connect(jTextFieldServer.getText()); 
                    gameClient.setServerAddress(jTextFieldServer.getText());
                } catch (IOException ex) {
                    gameClient.displayError(GameVariables.BUNDLE.getString("MESSAGE_UNABLE_TO_CONNECT"));
                    gameClient.close(true);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    gameClient.displayError(GameVariables.BUNDLE.getString("MESSAGE_INVALID_SERVER_FORMAT"));
                    gameClient.close(true);
                }
            }
        }).start();
        dispose();       
    }
    
    private void jTextFieldServerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldServerKeyPressed
        if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_ENTER) {
            start();
        }
    }//GEN-LAST:event_jTextFieldServerKeyPressed

    private void jTextFieldServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldServerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldServerActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        System.setProperty("sun.java2d.opengl","True");
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Launcher().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextFieldServer;
    // End of variables declaration//GEN-END:variables
}