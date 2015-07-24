package br.ufba.lasid.jds.prototyping.hddss;

import br.ufba.lasid.jds.util.IDebugger;
import java.util.ArrayList;

/*
 *  RuntimeContainer é uma camada que simula um OS ou se apresenta
 *  como o MIDDLEWARE no modo PROTÓTIPO
 * 
 *  Runtime é a INFRA subjacente para o AGENTE
 */

public class RuntimeContainer extends Thread implements RuntimeSupport, IDebugger{
    public AbstractClock clock;     //   Relogio LOCAL
    public CPU cpu;                 //   CPU de execucao
    public RuntimeSupport context;  //   Mantém um conjunto de VARIAVEIS 
                                    //   que representam o CONTEXTO LOCAL
            
    public Buffer nic_out;          //   Buffer de ENVIO     (SEND)
    public Buffer nic_in;           //   Buffer de RECEPCAO  (RECEIVE)
    public Buffer app_in;           //   Buffer de ENTREGA   (DELIVER)
    public Buffer exc_in;           //   Buffer de EXECUCAO  (EXECUTE)

    FaultModelAgent faultModel;     //   MODELO DE FALHAS

    Agent  agent;                   //   AGENTE que utiliza a INFRA

    public int nprocess = 0;        //   NUMERO DE PROCESSOS

    RuntimeVariables variables = new RuntimeVariables();

    public RuntimeContainer(RuntimeSupport context){
        this.context = context;
        nic_in = new Buffer();
        nic_out = new Buffer();
        app_in = new Buffer();
        exc_in = new Buffer();
        nprocess = context.get(RuntimeSupport.Variable.NumberOfAgents).<Integer>value();   
    }

    
    public boolean register(Agent agent){
        this.agent = agent;
        return true;
    }
          
    public final synchronized void execute(){
        /*
         *  EXECUTA A ACAO DO AGENTE
         */

         if(agent.status()){
               while(receive());
               while(deliver());
            //((Clock_Virtual)clock).tick();
            if( ((Clock_Virtual)clock).tick() && agent.status()) {
               agent.execute();
            }
           
            while(send());
            
         }
        
    }

    public final void increaseTick() {
        if (faultModel == null) {
            execute();
        }
        else faultModel.increaseTick();
    }
    
    public final void setFaultModel(String fault_model) {
    	try {
    		Class c = Class.forName(fault_model);

    		faultModel =
                    (FaultModelAgent)
                    c.newInstance();

                faultModel.initialize(this);

                debug("Modelo de Falhas "+fault_model+" implantado em p"+agent.getAgentID());

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    @Override
    public void run() {
        int finalTime = context.get(RuntimeSupport.Variable.FinalTime).<Integer>value();
        agent.startup();
        while (clock.value() < finalTime) {
            context.perform(this);
            this.yield();
            if (agent.isDone())
                break;
        }
        context.ok();
    }
    
    public final boolean deliver(){
       /*
         *  ESTE MÉTODO É INVOCADO AUTOMATICAMENTE NA SIMULACAO ou NA EMULACAO
         *  DE UM AGENTE
         * 
         */
       Message m = nextToDeliver(clock.value());
       if(m != null){
            agent.preDeliver(m);
            agent.deliver(m);
            reportEvent(m, 'd');
          return true;
       }
       return false;
    }

    private final Message nextToDeliver(long now){
        /*
         *  ESTE EVENTO É PRIVADO E AUXILIA o deliver(), PROVENDO A PRÓXIMA msg
         *  A SER ENTREGUE
         * 
         */
               ArrayList a = null;
               a = app_in.getMsgs(now);
               if (a.isEmpty()) {
                   return null;
               }
            Message msg;
            msg = (Message) a.get(0);
            return msg;
    }

    public final boolean receive(){
       /*
         *  ESTE MÉTODO É INVOCADO AUTOMATICAMENTE NA SIMULACAO ou NA EMULACAO
         *  DE UM AGENTE
         * 
         */
       
       // Message m = nextToReceive(clock.value());
        
       // O BUFFER DE RECEBIMENTO USA O TEMPO DO RELOGIO GLOBAL
        long at = 0;
        
        if (agent.getScenario() != null) {
          at = agent.getScenario().globalClock.value();
        }
        else {
          at = cpu.value();
        }
        
       Message m = nextToReceive( at );
       if(m != null){
            agent.preReceive(m);
            agent.receive(m);
            reportEvent(m, 'r');
            return true;
       }
       return false;  
    }

    protected final Message nextToReceive(long now){
        /*
         *  ESTE EVENTO É PRIVADO E AUXILIA o receive(), PROVENDO A PRÓXIMA msg
         *  A SER ENTREGUE
         *  REVER USO DO receive(now)
         */
            ArrayList a = null;
            a = nic_in.getMsgs(now);
            if (a.isEmpty()) {
                   return null;
            }
            Message msg;
            msg = (Message) a.get(0);
            return msg;
    }

    public final Message pending(){
       /*
         *  ESTE MÉTODO PODE SER INVOCADO PARA EXECUCAO DE MSGS
         *  DO BUFFER DE EXECUCAO DE UM AGENTE
         *  REVER SEU USO
         */       
       return nextPending(clock.value());
    }
    
    protected final Message nextPending(long now){
        /*
         *  ESTE EVENTO É PRIVADO E AUXILIA o pending(), PROVENDO A PRÓXIMA msg
         *  A SER ENTREGUE
         */
            ArrayList a = exc_in.getMsgs(now);

            if (!(a != null && !a.isEmpty())) {
               return null;
            }

            Message msg;
            msg = (Message) a.get(0);

            return msg;
    }
    
    public final boolean send(){
       /*
         *  ESTE MÉTODO É INVOCADO AUTOMATICAMENTE NA SIMULACAO ou NA EMULACAO
         *  DE UM AGENTE
         * 
         */
       return nextToSend(clock.value());
    }

    private final boolean nextToSend(long now){
        /*
         *  ESTE EVENTO É PRIVADO E AUXILIA o send(), PROVENDO A PRÓXIMA msg
         *  A SER ENTREGUE
         */        
            ArrayList a = nic_out.getMsgs(now);
            if (a.isEmpty()) {
                return false;
            }

            Message msg;


            msg = (Message) a.get(0);
            this.sendToNetwork(msg);
            reportEvent(msg, 's');
            return true;
    }
    
    public void sendToNetwork(Message m){
        /*
         *  ESTE EVENTO ENVIA A MENSAGEM PARA A REDE
         *  NO CASO DE PROTOTIPO, O ENVIO É PARA A REDE REAL
         *  SOBRESCRITO EM MIDDLEWARE
         * 
         */
        Network network = context.get(Variable.Network).<Network>value();
        network.send(m);
    } 
    
    public final void reportEvent(Message msg, char ev) {
        /*
         *  REPORTA EVENTOS PARA DEBUG (EX: sending, receiving e delivering)
         * 
         */
        try{
                String saida = ""+
                agent.getAgentID() +"; "+
                ev+"; "+
                cpu.value()+"; "+
                msg.toString();
                debug(saida);
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

/*    private final int getAgentProcessingTime() {
        
        return 1;
        /*
        if (clock.getMode() =='s') {
            return 1;
        }
        else {
            return (int) (MAX_PROCESSA * Math.random());
        }
        
    }
*/
    public final void debug(String d) {

        boolean _debug = (Boolean)context.get(Variable.Debug).value();
        java.io.PrintStream out = context.get(Variable.StdOutput).<java.io.PrintStream>value();
        
        if (_debug) out.println(d);
    }

    public Value get(Variable variable) {
        return variables.get(variable);
    }

    public <U> void set(Variable variable, U value){
        variables.set(variable, value);
    }
    public Value get(String name) {
        return variables.get(name);
    }

    public <U> void set(String name, U value) {
        variables.set(name, value);
    }

    public void perform(RuntimeContainer rs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void ok() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getNumberOfProcess() {
        return nprocess;
    }

   public boolean advance() {
      return this.context.advance();
   }

   public long exec(Object data){
       /*
        *   RETORNA TEMPO DE EXECUCAO DA CPU PARA UM DADO OBJETO
        */
       return cpu.exec(data);
   }
}
