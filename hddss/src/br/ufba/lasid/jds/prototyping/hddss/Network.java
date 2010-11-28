package br.ufba.lasid.jds.prototyping.hddss;


import br.ufba.lasid.jds.prototyping.hddss.Simulator;
import java.lang.reflect.Method;
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

    
    Network(){
        buffer = new Buffer();
        broadcasts = new int[256];
        unicasts = new int[256];

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

    public final void avancaTick() {
        this.processaRelogio();
    }

    @Override
    public final void run() {

        while (true) {
            this.avancaTick();
            this.yield();
            if (done)
                break;
        }
//        ri.end();
        
    }


    public final void processaRelogio() {
            tick++;
            this.yield();
            //if ( (tick == 1)&& status() ) this.processa();
            if (tick > totalticks) {
                clock++;
                tick=0;
            }

    }

    public synchronized void send(Message msg){
        long dt = clock - last;
        last = clock;
        tqueue -= dt;

        tqueue = tqueue < 0? 0: tqueue;

//        long at = delay();
        tqueue += delay();
        long at = (long) tqueue;
        conteiner.get(RuntimeSupport.Variable.QueueDelayTrace).<DescriptiveStatistics>value().addValue(tqueue);
//        conteiner.atraso_fila.addValue(tqueue);
       // System.nic_out.println("queue=" + tqueue);
        
       propagate(msg, at);
       
    }

    public void propagate(Message msg, double at){
        if(isRelay(msg)){

            relay(msg, at);

        }else{

            if(isBroadcast(msg)){

                broadcast(msg, at);

                return;
            }//end if isBroadcast

            unicast(msg, at);

        }//end if isRelay        
    }
    public void loopback(Message msg){
        int address = msg.sender;
        Agent p = conteiner.p[address];
        p.infra.nic_in.add((int)p.infra.clock.value() + 1, msg);
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
        int p_i = msg.relayFrom;
        int p_j = msg.relayTo;
        
        if(verifyChannel(p_i, p_j)) transfer(p_i, p_j, msg, (int)atraso);
    }
    
    public void unicast(Message msg, double atraso){
        unicasts[msg.type]++;
        if (isLoopback(msg)) {
            loopback(msg);
            return;
        }

        if (verifyChannel(msg.sender, msg.destination))
            transfer(msg.sender, msg.destination, msg, atraso);
    }

    public void transfer(int p_i, int p_j, Message msg, double atraso){
        Channels[p_i][p_j].deliverMsg(msg, atraso);
    }

    public void broadcast(Message msg, double atraso){
        
        int n = conteiner.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value();
        
        broadcasts[msg.type]++;
        
        int p_i = msg.sender;

        for (int p_j=0; p_j < n; p_j++) {

            if (isLoopback(p_i, p_j)){

                loopback(msg);
                
            }else if (verifyChannel(p_i, p_j)){

                    transfer(p_i, p_j, msg, atraso);

            }//end else if
        }//end for
        
    }
    
    public final void debug(String d) {
        boolean debug = conteiner.get(RuntimeSupport.Variable.Debug).<Boolean>value();
        if (debug)
            conteiner.out.println(d);
    }

    abstract double delay();
}
