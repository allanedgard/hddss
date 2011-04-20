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
    Buffer net_out;
    Simulator conteiner;
    Channel channels[][];
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
        net_out = new Buffer();
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

    public void setProcessingTime(String v){
        processingTime = Double.parseDouble(v);
    }

    public final void init(Simulator cxt){
        conteiner = cxt;
        
        int n = cxt.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value().intValue();

        channels = new Channel[n][n];
        totalticks = (int)(1/conteiner.ro);
    }

    public synchronized final void incTick() {
        this.clockTick();
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

    
    public void send(Message msg){
        synchronized(this){
            long dt = clock - last;
            last = clock;
            tqueue -= dt;


            tqueue = (tqueue < 0? 0: tqueue);
            //        long at = delay();
            tqueue += delay(msg);
            conteiner.get(RuntimeSupport.Variable.QueueDelayTrace).<DescriptiveStatistics>value().addValue(tqueue);
            propagate(msg);
        }
       
    }

//    public void propagate(){
//       Message m = null;
//       while((m = getOutput())!= null){
//          propagate(m);
//       }
//    }

//    long cur = 0;
//    public Message getOutput(){
//         return getOutput(clock);
//    }
//    public Message getOutput(long now){
//      ArrayList a = net_out.getMsgs((int)now);
//
//      if (a.isEmpty()) {
//          return null;
//      }
//
//      Message msg = (Message) a.get(0);
//
//      return msg;
//
//    }

//    public void propagate(Message msg){
//         propagate(msg, (long)Math.ceil(clock + tqueue));
//    }
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
            int address = msg.sender;
            Agent p = conteiner.p[address];
            p.getInfra().nic_in.add((int)(p.getInfra().cpu.value())+1, msg);
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

    public void relay(Message msg){
        synchronized(this){
            int p_i = msg.relayFrom;
            int p_j = msg.relayTo;

            if(verifyChannel(p_i, p_j)) transfer(p_i, p_j, msg);
        }
    }
    
    public void unicast(Message msg){
        synchronized(this){
            unicasts[msg.type]++;
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
            multicasts[msg.type]++;
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
            channels[p_i][p_j].deliverMsg(msg, (long)(tqueue));//Math.ceil(clock + tqueue));
        }
    }

    public void broadcast(Message msg){
        synchronized(this){
            int n = conteiner.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value();

            broadcasts[msg.type]++;

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
