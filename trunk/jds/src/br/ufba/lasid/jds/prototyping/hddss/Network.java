package br.ufba.lasid.jds.prototyping.hddss;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

/*
 * To change this template, choose Tools | Templates
 * and open the template nic_in the editor.
 */

/**
 *
 * @author aliriosa
 */
public abstract class Network extends Thread{
    Buffer net_in;
    Simulator conteiner;
    Channel channels[][];
    long[][] delaymap;
    boolean fifo = false;
    long next = 0;
    long last = 0;
    double tqueue = 0.0;
    long clock = 0;
    long tick = 0;
    int totalticks = 0;
    boolean done = false;
    
    static final String TAG = "network";


    int broadcasts[];
    int unicasts[];
    int multicasts[];

    
    Network(){
        net_in = new Buffer();
        broadcasts = new int[256];
        unicasts = new int[256];
        multicasts = new int[256];

    }
    
    public final boolean verifyChannel(int i, int j) {
        if (channels[i][j] == null) {
            return false;
        }
        return channels[i][j].status();

    }

    public final void handshaking(int p_i, int p_j) {
    	try {

    	    channels[p_i][p_j] = (Channel)Factory.create(Channel.TAG, Channel.class.getName());
            channels[p_i][p_j].connect(conteiner.p[p_i], conteiner.p[p_j]);
            Factory.setup(channels[p_i][p_j], Channel.TAG);

        } catch (Exception e) {
                e.printStackTrace();
    	}
    }

    public final void init(Simulator cxt){
        conteiner = cxt;
        
        int n = cxt.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value().intValue();

        channels = new Channel[n][n];
        totalticks = (int)(1/conteiner.ro);
    }

    public synchronized final void incTick() {
        this.clockTick();
         outcoming();
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


    public synchronized final void clockTick() {
      tick++;
      this.yield();
      if (tick > totalticks) {
       clock++;
       tick=0;
      }
    }

    public void outcoming(){
      while(true){
         ArrayList buffer = net_in.getMsgs((int)clock);
         if(buffer.isEmpty()){
            break;
         }

         Message m = (Message)buffer.get(0);
         proc(m);

         propagate(m);

      }
    }
    
    public void send(Message msg){
        synchronized(this){
           //proc(msg);
           // propagate(msg);
           incoming(msg);
        }
       
    }

    public void proc(Message msg){
         long dt = clock - last;
         last = clock;
         tqueue -= dt;
         tqueue = (tqueue < 0? 0: tqueue);
         //        long at = delay();
         tqueue += delay(msg);
         //conteiner.get(RuntimeSupport.Variable.QueueDelayTrace).<DescriptiveStatistics>value().addValue(tqueue);
         Simulator.reporter.stats("network queue delay", tqueue);
       
    }

    /* Simulate the travel of the message from agent to router network */
    double tripdelayBalance = 0.5;
    double inbalance(){
       return tripdelayBalance;
    }

    double outbalance(){
       return (1 - tripdelayBalance);
    }

    private long tripdelay(int p_i, int p_j, double balance){
         /* compute the timestamp of message arrival in network router */
         long delay = (long)Math.round(channels[p_i][p_j].delay() * balance);
         return delay;
    }
    
    protected long calcTimestamp(int p_i, int p_j, double balance){
         
         long timestamp = clock + tripdelay(p_i, p_j, balance);

         /* if network consider fifo service then the messages will arrive in fifo order */
         if(fifo){
            if(delaymap == null){
               int n = conteiner.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value();
               delaymap = new long[n][n];
            }

            if(timestamp < delaymap[p_i][p_j]){
               timestamp = delaymap[p_i][p_j] + 1;
            }

            delaymap[p_i][p_j] = timestamp;
         }


         return timestamp;
    }


   private void incoming(Message m){
       if(isLoopback(m)){
          loopback(m);
          return;
       }
       
       int p_i = m.sender;
       int p_j = m.destination;

       if(isMulticast(m)){
         p_j = p_i;
       }

       if(isBroadcast(m)){
         p_j = p_i;
       }

       if(isRelay(m)){
          p_i = m.relayFrom;
          p_j = m.relayTo;
       }

       net_in.add((int)calcTimestamp(p_i, p_j, inbalance()), m);

    }
        
    public void propagate(Message msg){
      synchronized(this){
         if(isMulticast(msg)){
            multicast(msg);
            return;
         }

         if(isRelay(msg)){
            relay(msg);
            return;
         }

         if(isBroadcast(msg)){
            broadcast(msg);
            return;
         }//end if isBroadcast
         
         unicast(msg);
      }
    }
    public void loopback(Message msg){
        synchronized(this){
            Simulator.reporter.count("network loopbacks");

            if(msg.type >=0) Simulator.reporter.count("network loopbacks class " + msg.type);

            int address = msg.sender;
            Agent p = conteiner.p[address];
            p.getInfra().nic_in.add((int)(p.getInfra().cpu.value())+1, msg);
        }
    }
    public boolean isMulticast(Message msg){
       return msg.multicast;
    }
    public boolean isLoopback(Message msg){
        return isLoopback(msg.sender, msg.destination) && !msg.multicast;
    }

    public boolean isLoopback(int p_i, int p_j){
        return (p_i == p_j);
    }

    public boolean isBroadcast(Message msg){
        int n = conteiner.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value().intValue();
        return (msg.destination == n) && !msg.multicast;
    }

    public boolean isRelay(Message msg){
        return (msg.relayTo != -1) && !msg.multicast;
    }

    public void relay(Message msg){
        synchronized(this){

            Simulator.reporter.count("network relays");

            if(msg.type >=0) Simulator.reporter.count("network relays class " + msg.type);

            int p_i = msg.relayFrom;
            int p_j = msg.relayTo;

            if(verifyChannel(p_i, p_j)) transfer(p_i, p_j, msg);
        }
    }
    
    public void unicast(Message msg){
        synchronized(this){

            Simulator.reporter.count("network unicasts");

            if(msg.type >=0) Simulator.reporter.count("network unicasts class " + msg.type);

            //unicasts[msg.type]++;
            if (isLoopback(msg)) {
                loopback(msg);
                return;
            }
            if (verifyChannel(msg.sender, msg.destination))
                transfer(msg.sender, msg.destination, msg);
        }
    }

    public void multicast(Message msg){
      synchronized(this){
         NetworkGroup g = gtable.get(msg.destination);
         if(g != null){
            
            Simulator.reporter.count("network multicasts");

            if(msg.type >=0) Simulator.reporter.count("network multicasts class " + msg.type);

            int p_i = msg.sender;
            
            for(int p_j : g){
               if (p_i != p_j && verifyChannel(p_i, p_j)){
                  transfer(p_i, p_j, msg);
               }
            }
         }
      }
    }

    public void transfer(int p_i, int p_j, Message msg){
        synchronized(this){
            long delay = (long)(tripdelay(p_i, p_j, outbalance()) + tqueue);
            channels[p_i][p_j].deliverMsg(msg, delay);//Math.ceil(clock + tqueue));
        }
    }

    public void broadcast(Message msg){
        synchronized(this){
            int n = conteiner.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value();

            Simulator.reporter.count("network broadcasts");
            
            if(msg.type >=0) Simulator.reporter.count("network broadcasts class " + msg.type);

            int p_i = msg.sender;

            for (int p_j=0; p_j < n; p_j++) {

                if (isLoopback(p_i, p_j)){

                    loopback(msg);

                }else if (verifyChannel(p_i, p_j)){

                        transfer(p_i, p_j, msg);

                }//end else if
            }//end for
        }
    }
    
    public final void debug(String d) {
        boolean debug = conteiner.get(RuntimeSupport.Variable.Debug).<Boolean>value();
        if (debug)
            conteiner.out.println(d);
    }

    public final void setFIFO(String fifo){
       this.fifo = Boolean.parseBoolean(fifo);
    }

    public final void setTripBalance(String b){
       tripdelayBalance = Double.parseDouble(b);

       if(tripdelayBalance > 1) tripdelayBalance = 0.5;
    }

    abstract double delay(Message m);

    public void setGroups(String gdefs){
       
      gdefs = gdefs.replace('[', ' ');
      gdefs = gdefs.replace(']', ' ');
      gdefs = gdefs.trim();
      
      StringTokenizer tokens = new StringTokenizer(gdefs);
      
      //NetworkGroup group = new NetworkGroup();
      while(tokens.hasMoreTokens()){
         String gtoken = tokens.nextToken(";");
         gtoken = gtoken.trim();
         //debug(gtoken);
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

         //System.out.println(g);
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
