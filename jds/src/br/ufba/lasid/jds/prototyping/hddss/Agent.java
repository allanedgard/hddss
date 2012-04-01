package br.ufba.lasid.jds.prototyping.hddss;

import br.ufba.lasid.jds.prototyping.hddss.report.Reporter;

public class Agent extends Thread implements IAgent{
    private int ID;
    private char tipo;
    public transient Context context;
    private transient boolean done;
    public transient long exectime = 0;
    protected boolean shutdown = false;
    public static transient final String TAG = "agent";
    private transient RuntimeContainer infra;
    public transient final Object lock = this;
    private Scenario scenario;

    
   /*
     *  ESTE MÉTODO INFORMA O VALOR REAL DO TEMPO, SERVINDO PARA CÁLCULOS DE 
     *  ATRASOS REAIS DE receive e deliver
     */
   private long getActualClockValue() {
        long at;
        if (scenario != null) {
            at = scenario.globalClock.value();
        }
        else {
            at = this.infra.clock.value();
        }
        return at;
   }
   
   /*
    *   ESTE MÉTODO INFORMA O REPORTER A SER UTILIZADO PARA REGISTRAR A stat
    */
   
    protected Scenario getScenario() {
        /*
         *  O ACESSO AO SCENARIO É SOMENTE PARA ALGUMAS ATIVIDADES DO
         *  FRAMEWORK DE SIMULACAO
         */
        return scenario;
    }
   
    public Reporter getReporter() {
        if (scenario!=null) {
            return scenario.reporter;
        }
        else return TestBed.reporter;
    }

    /*
     *  UNICA FORMA DE OBTER O ID DO AGENTE
     */
    public int getAgentID() {
        return ID;
    }    
    
    public void setAgentID(int i){
        ID = i;
    }

    /*
     *  AO INICIAR O AGENTE, SETA O FLAG done=false
     */
    public Agent() {
        done = false;
    }
    
    /*
     *  OBTEM O CONTEXTO, ESTE CONCEITO AINDA NAO FOI IMPLEMENTADO
     */
    public Context getContext() {
        return context;
    }

    /*
     *  DEFINE O CONTEXTO, ESTE CONCEITO AINDA NAO FOI IMPLEMENTADO
     */
    public void setContext(Context c) {
        this.context = c;
    }    
    
    /*
     *  UTILIZADO APOS O INIT DO AGENT PARA ASSOCIAR UM SCENARIO AO MESMO
     *  EM MODO SIMULACAO
     */
    public void setScenario(Scenario s) {
        scenario = s;
    }

    /*
     *  UTILIZADO APOS O INIT DO AGENT PARA ASSOCIAR UMA INFRA AO MESMO
     *  INCLUINDO CPU E CLOCK
     */
    public void setInfra(RuntimeContainer infra) {
        this.infra = infra;
    }

    /*  
     *  OBTEM A INFRA DO AGENTE
     */
    public RuntimeContainer getInfra() {
        return infra;
    }    
    
    /*
     *  AGENTE TERMINOU EXECUCAO???
     */
    public boolean isDone() {
        return done;
    }
    
    /*
     *  AGENTE DELIBERADAMENTE TERMINOU EXECUCAO    
     */
    public void setDone(boolean done) {
        this.done = done;
    }

    /*
     *   WTF é TIPO
     */
    public char getTipo() {
        return tipo;
    }

    public void setTipo(char tipo) {
        this.tipo = tipo;
    }
    
    /*
     *  CONFIGURAR O AGENTE  
     */
    public void setup() {
        
    };

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
    
   /*
   public final void send(Message msg){
       infra.send(msg);
   }
     * 
     */
   
   public final void createMessage(long realClock, int sender, int destination, int type, Object content, int logicalClock) {
        Message msg = new Message(sender, destination, type, logicalClock, realClock, content);
        
        long at = 0;
        if (scenario != null) {
          at = scenario.globalClock.value();
        }
        else {
          at = this.infra.clock.value();
        }
        msg.actualSendingTime=at;
        
        if (infra.faultModel == null) {
          infra.nic_out.add(realClock, msg);
        }
        else infra.faultModel.sendMessage(realClock,msg);
    }

   public final void createMessage(Message msg) {

        long at = 0;
        if (scenario != null) {
          at = scenario.globalClock.value();
        }
        else {
          at = this.infra.clock.value();
        }
        msg.actualSendingTime=at;
        
        if (infra.faultModel == null) {
            infra.nic_out.add(msg.physicalClock, msg);
        }
        else infra.faultModel.sendMessage(msg.physicalClock,msg);
    }
   
   public final void createMessage(long realClock, int sender, int destination,
            int type, Object content, int logicalClock, boolean payload) {
        Message msg = new Message(sender, destination, type, logicalClock, realClock, content);
        msg.payload = payload;
        
        long at = 0;
        if (scenario != null) {
          at = scenario.globalClock.value();
        }
        else {
          at = this.infra.clock.value();
        }
        msg.actualSendingTime=at;
        
        if (infra.faultModel == null) {
            infra.nic_out.add(realClock, msg);
        }
        else infra.faultModel.sendMessage(realClock,msg);
    }

    public final void relayMessage(long realClock, Message msg, int to) {
        msg.relayFrom = ID;
        msg.relayTo = to;
        
        long at = 0;
        if (scenario != null) {
          at = scenario.globalClock.value();
        }
        else {
          at = this.infra.clock.value();
        }
        msg.actualSendingTime=at;
        
        if (infra.faultModel == null) {
            infra.nic_out.add(realClock, msg);
        }
        else infra.faultModel.sendMessage(realClock,msg);
    }
   
   
   
    public void startup(){
    }
    
    public void execute() {
    }
    
    public final void preReceive(Message msg) {
      /* Este evento pode ser sobrecarregado pela ação específica do protocolo (NO ANYMORE)*/
      long at = 0;
      long at1 = 0;
      at1 = this.infra.clock.value();
      if (scenario != null) {
          at = scenario.globalClock.value();
      }
      else {
          at = at1;
      }
      /* SETA O TEMPO REAL DE RECEPCAO */
      
      /* TEMPO DE RECEPCAO MEDIDO LOCALMENTE */
      msg.receptionTime = at1;
      msg.actualReceptionTime = at;
      
      infra.debug("(p" + ID + ") received at local time" + msg.receptionTime + " " + msg.content);
      infra.debug("(p" + ID + ") received at global time" + msg.actualReceptionTime + " " + msg.content);
      
      this.getReporter().stats("send-reception delay", msg.actualReceptionTime - msg.actualSendingTime);
      this.getReporter().stats("send-reception delay agent" + msg.sender + "/agent" + ID , msg.actualReceptionTime - msg.actualSendingTime);
      if (msg.payload) {
          this.getReporter().stats("APP send-reception delay", msg.actualReceptionTime - msg.actualSendingTime);          
      }
 }

    public void receive(Message msg) {
    }    
    
    public final void preDeliver(Message msg) {
        long at = 0;
        long at1 = 0;
        at1 = this.infra.clock.value();
        if (scenario != null) {
          at = scenario.globalClock.value();
        }
        else {
          at = at1;
        }
        infra.exc_in.add(this.infra.clock.value(), msg);

        getInfra().debug("p"+getAgentID()+" delivering m"+msg.logicalClock+" from "+msg.sender);
        infra.debug("(p" + ID + ") delivered at local time" + at1 + " " + msg.content);
        infra.debug("(p" + ID + ") delivered at global time" + at + " " + msg.content);
        
        this.getReporter().stats("send-delivery delay", at - msg.actualSendingTime);
        this.getReporter().stats("send-delivery delay agent" + msg.sender + "/agent" + ID, at - msg.actualSendingTime);
        this.getReporter().stats("blocking time", at - msg.actualReceptionTime);        
    }
    
    public void deliver(Message msg) {
    }    
    
    
    long cur = 0;
    
    
    public final Message receive(){
        /*
         *  ESTE METODO SERÁ REVISADO NO AJUSTE DO CODIGO DA CPU
         * 
         */
         Message msg = null;
         long now = infra.cpu.value();
         cur = infra.clock.value();
         long t = 0;
         while(cur <= now){
            t = cur;
            msg = infra.nextPending(cur);
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
      scenario.reporter.stats("cpu queue delay of agent" + ID , cpuQueue);       
    }


}
