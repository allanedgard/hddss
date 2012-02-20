
package br.ufba.lasid.jds.prototyping.hddss;

import java.io.*;
import java.net.*;

/**
 *
 * @author Allan
 */
public class HDDSS_Socket extends Thread {
    DatagramSocket socket;
    //Socket socket;

    Buffer nic_in;
    int port;
    
    HDDSS_Socket(Buffer nic_in, String ad, int port) {
        this.nic_in = nic_in;
        this.port = port;
        try {
            socket = new DatagramSocket(port);
            //socket = new Socket(ad, port);
            System.out.println("Connected to "+socket.getLocalSocketAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void run() {
        try {
            while (true) {
                byte[] data = new byte[4];
                DatagramPacket packet = new DatagramPacket(data, data.length );
                socket.receive(packet);

                int len = 0;
                // byte[] -> int
                for (int i = 0; i < 4; ++i) {
                    len |= (data[3-i] & 0xff) << (i << 3);
                }

                // now we know the length of the payload
                byte[] buffer = new byte[len];
                packet = new DatagramPacket(buffer, buffer.length );
                socket.receive(packet);

                ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
                ObjectInputStream oos = new ObjectInputStream(baos);
                Message m = (Message)oos.readObject();
                nic_in.add(0, m);
                System.out.println("RECEBEU m from p"+m.sender);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }  
    }
    
}
