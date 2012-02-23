
package br.ufba.lasid.jds.prototyping.hddss;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;


/**
 *
 * @author Allan
 */
public class HDDSS_Socket extends Thread {
    DatagramSocket socket;
    //Socket socket;
    boolean finish;

    Buffer nic_in;
    int port;
    Agent agent;
    Registry registry;
    
    HDDSS_Socket(Agent a, String ad, int port) {
        //this.nic_in = nic_in;
        this.agent = a;
        this.port = port;
        try {
            finish=false;
            socket = new DatagramSocket(port);
            //socket = new Socket(ad, port);
            System.out.println("Connected to "+socket.getLocalSocketAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean waitRegister() {
        if (agent.getAgentID() ==0) {
             try{
                byte[] data = new byte[4];
                DatagramPacket packet = new DatagramPacket(data, data.length );
                socket.receive(packet);
             } catch (Exception e) {
                 e.printStackTrace();
             }
        }
        else {
            int number = agent.getAgentID();
            System.out.println(number);
            byte[] data = new byte[4];

                    // int -> byte[]
            for (int i = 0; i < 4; ++i) {
                int shift = i << 3; // i * 8
                data[3-i] = (byte)((number & (0xff << shift)) >>> shift);
            }
            try {
                // DatagramSocket socket = new DatagramSocket(Integer.parseInt(PORT[msg.sender]));
                InetAddress client = InetAddress.getByName(((MiddlewareRuntimeContainer) agent.getInfra()).IP[0]);
                DatagramPacket packet = new DatagramPacket(data, 4, client, 
                    Integer.parseInt( ((MiddlewareRuntimeContainer) agent.getInfra()).PORT[0]));
                socket.send(packet);
            } catch (Exception e) {
                 e.printStackTrace();
            }
        }
        return true;
    }   

    @Override
    public final void run() {
        int total=0;
        try {
            while (!finish) {
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
                
                TestBed.reporter.count("network unicasts");
                if(m.type >=0) TestBed.reporter.count("network unicasts class " + m.type);
                agent.preReceive(m);
                agent.receive(m);
                System.out.println("RECEIVED m from p"+m.sender);
                total++;
                System.out.println("total="+total);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }  
    }
    
}
