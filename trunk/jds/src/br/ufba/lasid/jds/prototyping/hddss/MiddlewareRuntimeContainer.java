package br.ufba.lasid.jds.prototyping.hddss;

import java.io.*;
import java.net.*; 
import java.util.*;

public class MiddlewareRuntimeContainer extends RuntimeContainer {

    /*
     *  IP      - vector of IP data
     *  PORT    - vector of PORT data
     *  ID      - ID of the current agent
     */
    String IP[];
    String PORT[];
    int ID;
    
    Message[] receiveData;
    Message[] sendData;
    ObjectOutputStream output;
    ObjectInputStream input;
    
    /*
     *      Socket for communication
     */
    HDDSS_Socket s;
    
    MiddlewareRuntimeContainer(RuntimeSupport c) {
        super(c);
    }
    
    @Override
    public boolean register(Agent agent){
        this.agent = agent;
        ID = agent.ID;
        return true;
    }
    
    @Override
    public synchronized void execute(){
            super.execute();
    }

    @Override
    public void run() {
        System.out.println("Starting infra at agent "+this.ID);
        try {
            s = new HDDSS_Socket(nic_in, IP[ID],
                  Integer.parseInt(this.PORT[ID]));
            s.start();
        } catch (Exception e) {
        }
        super.run();
    }

    @Override
    public boolean receive()  {
       Message m = receive(0);
       if(m != null){
            agent.receive(m);
            reportEvent(m, 'r');
            return true;
       }
       return false;   
 }
  
    public boolean send(long now){
            ArrayList a = nic_out.getMsgs((int)now);
            if (a.isEmpty()) {
                return false;
            }

            Message msg;

            msg = (Message) a.get(0);
            if ( msg.destination != context.get(Variable.NumberOfAgents).<Integer>value() ) {
                System.out.println("from "+msg.sender+" to "+msg.destination);
                
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(msg);
                    oos.flush();
                    // get the byte array of the object
                    byte[] Buf= baos.toByteArray();

                    int number = Buf.length;
                    System.out.println(number);
                    byte[] data = new byte[4];

                    // int -> byte[]
                    for (int i = 0; i < 4; ++i) {
                        int shift = i << 3; // i * 8
                        data[3-i] = (byte)((number & (0xff << shift)) >>> shift);
                    }

                    System.out.println("PORTA="+PORT[msg.destination]);         
                    System.out.println("PORTA="+PORT[msg.sender]);  
                    // DatagramSocket socket = new DatagramSocket(Integer.parseInt(PORT[msg.sender]));
                    InetAddress client = InetAddress.getByName(IP[msg.destination]);
                    DatagramPacket packet = new DatagramPacket(data, 4, client, Integer.parseInt(PORT[msg.destination]));
                    s.socket.send(packet);

                    // now send the payload
                    packet = new DatagramPacket(Buf, Buf.length, client, Integer.parseInt(PORT[msg.destination]));
                    s.socket.send(packet);

                    System.out.println("ENVIOU");
                } catch(Exception e) {
                        e.printStackTrace();
                }
            }
            //network.send(msg);
            reportEvent(msg, 's');
            return true;
       
    }
    
}
