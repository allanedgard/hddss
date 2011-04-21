package br.ufba.lasid.jds.prototyping.hddss;
//

import br.ufba.lasid.jds.util.IScheduler;

public class Agent extends Thread implements IAgent{
    public int ID;
    public char tipo;
    public transient Context contexto;
    public transient boolean done;
    public transient long exectime = 0;
    protected boolean shutdown = false;
    public static transient final String TAG = "agent";
    public transient RuntimeContainer infra;
    public transient final Object lock = this;
    private long lastExecution = 0;

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
   
   public final void send(Message msg){
      long at;      
      infra.nic_out.add((int)(at = infra.cpu.value()), msg);
      System.out.println("(p" + ID + ") sent at " + at  + " " + msg.content);

   }

   public long getProcessingTime(Object m){
      return 0;
   }
   
    public void startup(){
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
       long at = 0;
//        synchronized(lock){
            infra.app_in.add((int)(at = this.infra.cpu.value()), msg);
            System.out.println("(p" + ID + ") received at " + at + " " + msg.content);

            //infra.app_in.add((int)this.infra.cpu.value(), msg);
  //      }
    }
    
    
    public void deliver(Message msg) {
         long at = 0;
         infra.exc_in.add((int)(at = this.infra.cpu.value()), msg);
         System.out.println("(p" + ID + ") delivered at " + at + " " + msg.content);

    }
    long cur = 0;
    public final Message receive(){
         Message msg = null;
         long now = infra.cpu.value();
         cur = infra.clock.value();
         long t = 0;
         while(cur <= now){
            t = cur;
            msg = infra.pending(cur);
            if(msg != null){
               break;
            }

            cur +=1;
         }
         if(msg != null){
            System.out.println("(p" + ID + ") retrieve to execute at " + t + " " + msg.content);
         }
         return msg;
         
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


    public final void exec(Object data){

       this.infra.exec(lock);
       
    }

}
