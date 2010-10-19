package br.ufba.lasid.hddss;


import br.ufba.lasid.hddss.Simulator;
import java.lang.reflect.Method;

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
        Channels = new Channel[conteiner.n][conteiner.n];
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
        conteiner.atraso_fila.addValue(tqueue);
       // System.nic_out.println("queue=" + tqueue);
        
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
        int address = msg.destinatario;
        Agent p = conteiner.p[address];
        p.infra.nic_in.adiciona((int)p.infra.clock.value() + 1, msg);
    }

    public boolean isLoopback(Message msg){
        return isLoopback(msg.remetente, msg.destinatario);
    }

    public boolean isLoopback(int p_i, int p_j){
        return (p_i == p_j);
    }

    public boolean isBroadcast(Message msg){
        return (msg.destinatario == conteiner.n);
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
        unicasts[msg.tipo]++;
        if (isLoopback(msg)) {
            loopback(msg);
            return;
        }

        if (verifyChannel(msg.remetente, msg.destinatario))
            transfer(msg.remetente, msg.destinatario, msg, atraso);
    }

    public void transfer(int p_i, int p_j, Message msg, double atraso){
        Channels[p_i][p_j].entregaMensagem(msg, atraso);
    }

    public void broadcast(Message msg, double atraso){

        broadcasts[msg.tipo]++;
        
        int p_i = msg.remetente;

        for (int p_j=0; p_j < conteiner.n; p_j++) {

            if (isLoopback(p_i, p_j)){

                loopback(msg);
                
            }else if (verifyChannel(p_i, p_j)){

                    transfer(p_i, p_j, msg, atraso);

            }//end else if
        }//end for
        
    }
    
    public final void debug(String d) {
        if (conteiner.debug_mode)
            conteiner.out.println(d);
    }

    abstract double delay();
}