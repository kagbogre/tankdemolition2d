/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer.Library.Interfaces;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

/**
 *
 * @author kagbogre
 */
public interface IUDPCommand {
    public void execute(DataInputStream inputStream, DatagramPacket packet) throws IOException;    
}
