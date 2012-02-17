
package br.ufba.lasid.jds.prototyping.hddss;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 *
 * @author Allan
 */
public class HDDSS_UDPSocket extends Thread {
    Buffer nic_in;
    int port;
    
    HDDSS_UDPSocket(Buffer nic_in, int port) {
        this.nic_in = nic_in;
        this.port = port;
    }
    
    @Override
    public final void run() {
        try {
            DatagramSocket socket = new DatagramSocket(port);

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
                System.out.println("RECEBEU");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }  
    }
    
}
