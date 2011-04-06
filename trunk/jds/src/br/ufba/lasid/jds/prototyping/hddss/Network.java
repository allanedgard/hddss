package br.ufba.lasid.jds.prototyping.hddss;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/*
 * To change this template, choose Tools | Templates
 * and open the template nic_in the editor.
 */

/**
 *
 * @author aliriosa
 */
public abstract class Network extends Thread{
    Buffer buffer;
    Simulator conteiner;
    Channel Channels[][];
    long next = 0;
    long last = 0;
    double tqueue = 0.0;
    long clock = 0;
    long tick = 0;
    int totalticks = 0;
    boolean done = false;
    double processingTime = 0.0;
    
    static final String TAG = "network";


    int broadcasts[];
    int unicasts[];
    int multicasts[];

    
    Network(){
        buffer = new Buffer();
        broadcasts = new int[256];
        unicasts = new int[256];
        multicasts = new int[256];

    }
    
    public final boolean verifyChannel(int i, int j) {
        if (Channels[i][j] == null) {
            return false;
        }

        return Channels[i][j].status();

    }

    public final void handshaking(int p_i, int p_j) {
    	try {

    	    Channels[p_i][p_j] = (Channel)Factory.create(Channel.TAG, Channel.class.getName());
            Channels[p_i][p_j].connect(conteiner.p[p_i], conteiner.p[p_j]);
            Factory.setup(Channels[p_i][p_j], Channel.TAG);

        } catch (Exception e) {
                e.printStackTrace();
    	}
    }

    public void setProcessingTime(String v){
        processingTime = Double.parseDouble(v);
    }

    public final void init(Simulator cxt){
        conteiner = cxt;
        
        int n = cxt.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value().intValue();

        Channels = new Channel[n][n];
        totalticks = (int)(1/conteiner.ro);
    }

    public final void incTick() {
        synchronized(this){
            this.processaRelogio();
        }
    }

    @Override
    public final void run() {

        while (true) {
            this.incTick();
            this.yield();
            if (done)
                break;
        }
//        ri.end();
        
    }


    public final void processaRelogio() {
        synchronized(this){
            tick++;
            this.yield();
            //if ( (tick == 1)&& status() ) this.processa();
            if (tick > totalticks) {
                clock++;
                tick=0;
            }
        }
    }

    
    public void send(Message msg){
        synchronized(this){
        long dt = clock - last;
        last = clock;
        tqueue -= dt;

        tqueue = tqueue < 0? 0: tqueue;

//        long at = delay();
        tqueue += delay(msg);
        long at = (long) tqueue;
        conteiner.get(RuntimeSupport.Variable.QueueDelayTrace).<DescriptiveStatistics>value().addValue(tqueue);
//         try{
//            System.out.println(msg.type + ", " + XObject.objectToByteArray(msg.content).length);
//           }catch(Exception e){
//               e.printStackTrace();
//               System.exit(0);
//           }
            propagate(msg, at);
        }
       
    }

    public void propagate(Message msg, double at){
      synchronized(this){
         if(isMulticast(msg)){
            multicast(msg, at);
            return;
         }

         if(isRelay(msg)){
            relay(msg, at);
            return;
         }

         if(isBroadcast(msg)){
            broadcast(msg, at);
            return;
         }//end if isBroadcast
         
         unicast(msg, at);
      }
    }
    public void loopback(Message msg){
        synchronized(this){
            int address = msg.sender;
            Agent p = conteiner.p[address];
            p.getInfra().nic_in.add((int)(p.getInfra().clock.value()) + 1, msg);
        }
    }
    public boolean isMulticast(Message msg){
       return msg.multicast;
    }
    public boolean isLoopback(Message msg){
        return isLoopback(msg.sender, msg.destination);
    }

    public boolean isLoopback(int p_i, int p_j){
        return (p_i == p_j);
    }

    public boolean isBroadcast(Message msg){
        int n = conteiner.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value().intValue();
        return (msg.destination == n);
    }

    public boolean isRelay(Message msg){
        return (msg.relayTo != -1);
    }

    public void relay(Message msg, double atraso){
        synchronized(this){
            int p_i = msg.relayFrom;
            int p_j = msg.relayTo;

            if(verifyChannel(p_i, p_j)) transfer(p_i, p_j, msg, (int)atraso);
        }
    }
    
    public void unicast(Message msg, double delay){
        synchronized(this){
            unicasts[msg.type]++;
            if (isLoopback(msg)) {
                loopback(msg);
                return;
            }
            if (verifyChannel(msg.sender, msg.destination))
                transfer(msg.sender, msg.destination, msg, delay);
        }
    }

    public void multicast(Message msg, double delay){
      synchronized(this){
         NetworkGroup g = gtable.get(msg.destination);
         if(g != null){
            multicasts[msg.type]++;
            int p_i = msg.sender;
            
            for(int p_j : g){
               if (!isLoopback(p_i, p_j)) {
                  if (verifyChannel(p_i, p_j))
                     transfer(p_i, p_j, msg, delay);
               }
            }
         }
      }
    }

    public void transfer(int p_i, int p_j, Message msg, double delay){
        synchronized(this){
            Channels[p_i][p_j].deliverMsg(msg, delay);
        }
    }

    public void broadcast(Message msg, double delay){
        synchronized(this){
            int n = conteiner.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value();

            broadcasts[msg.type]++;

            int p_i = msg.sender;

            for (int p_j=0; p_j < n; p_j++) {

                if (isLoopback(p_i, p_j)){

                    loopback(msg);

                }else if (verifyChannel(p_i, p_j)){

                        transfer(p_i, p_j, msg, delay);

                }//end else if
            }//end for
        }
    }
    
    public final void debug(String d) {
        boolean debug = conteiner.get(RuntimeSupport.Variable.Debug).<Boolean>value();
        if (debug)
            conteiner.out.println(d);
    }

    abstract double delay(Message m);

    public void setGroups(String gdefs){
       
      gdefs = gdefs.replace('[', ' ');
      gdefs = gdefs.replace(']', ' ');
      gdefs = gdefs.trim();
      
      StringTokenizer tokens = new StringTokenizer(gdefs);
      
      NetworkGroup group = new NetworkGroup();
      while(tokens.hasMoreTokens()){
         String gtoken = tokens.nextToken(";");
         gtoken = gtoken.trim();
         System.out.println(gtoken);
         setGroup(gtoken);
      }
    }

    public void setGroup(String gdef){
      gdef = gdef.replace('[', ' ');
      gdef = gdef.replace(']', ' ');
      gdef = gdef.trim();
      String[] gparts = gdef.split("\\)");
      
      if(gparts != null && gparts.length == 2){
         String gtoken = gparts[0];
         String hstoken = gparts[1];

         gtoken = gtoken.trim();
         gtoken = gtoken.substring(2);
         gtoken = gtoken.trim();
         
         int gid = Integer.valueOf(gtoken);
         
         NetworkGroup g = gtable.get(gid) ;

         if(g == null){
            g = new NetworkGroup(gid);
            gtable.put(gid, g);
         }

         hstoken = hstoken.trim();
         hstoken = hstoken.replace('{', ' ');
         hstoken = hstoken.replace('}', ' ');
         hstoken = hstoken.trim();
         StringTokenizer tokens = new StringTokenizer(hstoken, ",");
         while(tokens.hasMoreTokens()){

            String htoken = tokens.nextToken(",");
            htoken = htoken.trim();

            int h = Integer.valueOf(htoken);

            if(!g.contains(h)){
               g.add(h);
            }
         }

         System.out.println(g);
      }    
    }

    NetworkGrouptable gtable = new NetworkGrouptable();
    
    class NetworkGrouptable extends Hashtable<Integer, NetworkGroup>{
       
    }

    class NetworkGroup extends ArrayList<Integer>{
       int groupid;

      public NetworkGroup() {
         this.groupid = -1;
      }
      public NetworkGroup(int groupid) {
         this.groupid = groupid;
      }       
    }
}
