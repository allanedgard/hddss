package br.ufba.lasid.jds.prototyping.hddss;
//

import br.ufba.lasid.jds.util.IScheduler;

public class Agent extends Thread implements IAgent{
    public int ID;
    public char tipo;
    public Context contexto;
    public boolean done;
    public long exectime = 0;
    protected boolean shutdown = false;
    public static final String TAG = "agent";
    public RuntimeContainer infra;
    public final Object lock = this;

    public int getAgentID() {
        return ID;
    }    

    public Agent() {
        done = false;
    }

    public Context getContexto() {
        return contexto;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public long getExectime() {
        return exectime;
    }

    public void setExectime(long exectime) {
        this.exectime = exectime;
    }

    public RuntimeContainer getInfra() {
        return infra;
    }

    public void setInfra(RuntimeContainer infra) {
        this.infra = infra;
    }

    public char getTipo() {
        return tipo;
    }

    public void setTipo(char tipo) {
        this.tipo = tipo;
    }
    
    /**
     * this method is used to perform particular configurations in the agent.
     */
    public void setup() {
    }

    public void setAgentID(int i){
        ID = i;
    }

    public void setType(char tp){
        tipo = tp;
    }

    public final void init() {
        setup();
    }
 

   public final boolean status() {
        if (infra.faultModel == null) {
            return true;
        } else
        return infra.faultModel.status();
    }

    
    @Override
    public final void run() {

        infra.start();
        
    }
    
   public void send(Message m){
       synchronized(this){
            infra.nic_out.add((int)(infra.clock.value()), m);
            //Debugger.debug("[p"+this.ID+"] send buffer =>" + infra.nic_out);
       }

   }
    public void startup(){
/*        int temp[] = {11, 22,33, 44, 55};
        for (int i = 0; i<temp.length; i++) {
            System.out.println("avanca");
            send(new Mensagem());
            //this.criamensagem(temp[i], this.ID, infra.nprocess, i, "teste",0);
        }*/
    }
    
    public void execute() {
        /*
         *   Este evento pode ser sobrecarregado pela ação específica
         *   do protocolo
         */
    }
    
    public void receive(Message msg) {
        /* 
         *   Este evento pode ser sobrecarregado pela ação específica 
         *   do protocolo
         */
        synchronized(this){
            infra.app_in.add((int)this.infra.clock.value(), msg);
        }
    }
    
    
    public void deliver(Message msg) {
        /*
         *   Este evento pode ser sobrecarregado pela ação específica
         *   do protocolo
         */
    }

    public long getExecutionTime() {
        return this.exectime;
    }

    public void setExecutionTime(long exectime) {
        this.exectime = exectime;
    }

    public IScheduler getScheduler(){
        return null;//(IScheduler)infra.scheduler;
    }

    public void shutdown(){
        stop();
        shutdown = true;
    }

    @Override
    public String toString() {
        return "Agent{" + "ID=" + ID + '}';
    }


}
