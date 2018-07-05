/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer.Library;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 *
 * @author kagbogre
 */
public class UDPWrapper {
    
    private byte[] response = new byte[256];
    
    
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    
    public UDPWrapper(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }
}
