package br.ufba.lasid.jds.prototyping.hddss;
//

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
        } else{
            return infra.faultModel.status();
        }
    }

    
    @Override
    public final void run() {
       infra.start();
    }
   
   public final void send(Message msg){
      long at;
      infra.nic_out.add((int)(at = infra.cpu.value()), msg);
      infra.debug("(p" + ID + ") sent at " + at  + " " + msg.content);
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
      /* Este evento pode ser sobrecarregado pela ação específica do protocolo (NO ANYMORE)*/
      long at = 0;
      infra.app_in.add((int)(at = this.infra.cpu.value()), msg);
      infra.debug("(p" + ID + ") received at " + at + " " + msg.content);
      Simulator.reporter.stats("send-reception delay", at - msg.physicalClock);
      Simulator.reporter.stats("send-reception delay agent" + msg.sender + "/agent" + ID , at - msg.physicalClock);
      
    }
    
    
    public void deliver(Message msg) {
         long at = 0;
         infra.exc_in.add((int)(at = this.infra.cpu.value()), msg);
         infra.debug("(p" + ID + ") delivered at " + at + " " + msg.content);
         Simulator.reporter.stats("send-delivery delay", at - msg.physicalClock);
         Simulator.reporter.stats("send-delivery delay agent" + msg.sender + "/agent" + ID, at - msg.physicalClock);
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
            infra.debug("(p" + ID + ") retrieve to execute at " + t + " " + msg.content);
         }
         return msg;
         
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
      long cpuQueue = this.infra.exec(data);
      Simulator.reporter.stats("cpu queue delay of agent" + ID , cpuQueue);       
    }

}
