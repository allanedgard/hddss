package br.ufba.lasid.jds.prototyping.hddss;

import br.ufba.lasid.jds.util.IDebugger;
import java.util.ArrayList;

/**
 * A RuntimeContainer can be a Operating System, a Middleware or a Simulator.
 * It allows to hide details about the execution infra of the agent.
 */
public class RuntimeContainer extends Thread implements RuntimeSupport, IDebugger{
    public AbstractClock clock;
    //public AbstractClock globalClock;
    public CPU cpu;
    public RuntimeSupport context;
    
    public Buffer nic_out; //send buffer
    public Buffer nic_in;  //receive buffer
    public Buffer app_in;  //deliver buffer
    public Buffer exc_in;  //execute buffer;

    FaultModelAgent faultModel;

    Agent  agent;

    static int MAX_PROCESSA = 100;

    public int nprocess = 0;

    RuntimeVariables variables = new RuntimeVariables();

    //public IScheduler scheduler;
    
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
    
    public synchronized void execute(){
//         nic_in.toString();
//         nic_out.toString();
//         app_in.toString();
//         exc_in.toString();

         if(agent.status()){
               while(receive());
               while(deliver());

            ((Clock_Virtual)clock).tick();
           
            if(((Clock_Virtual)clock).tickValue() == 1 && agent.status()) {
               agent.execute();
            }
           
            while(send());
//            long ftime = cpu.value();

//            for(long curr = clock.value(); curr <= ftime; curr ++){
//               while(send(curr));
//            }


            
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

                debug("Modelo de Falhas "+fault_model+" implantado em p"+agent.ID);

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
            if (agent.done)
                break;
        }
//        ri.end();
        context.ok();

        //super.run();
    }



    public boolean deliver(){
       Message m = deliver(clock.value());
       if(m != null){
            agent.preDeliver(m);
            agent.deliver(m);

            reportEvent(m, 'd');

//            if (m.payload) {
//                context.get(Variable.DlvDelayTrace).<DescriptiveStatistics>value().addValue(
//                        (int)clock.value() - m.physicalClock
//                );
//
//                context.get(Variable.RxDelayTrace).<DescriptiveStatistics>value().addValue(
//                        (int)clock.value() - m.receptionTime
//                );
//
//            }

          return true;
       }

       return false;
    }

    public Message deliver(long now){
               ArrayList a = null;
               a = app_in.getMsgs((int)now);
               if (a.isEmpty()) {
                   return null;
               }

            Message msg;
            msg = (Message) a.get(0);

            return msg;

    }

    public boolean receive(){

       Message m = receive(clock.value());
       if(m != null){
            agent.preReceive(m);
            agent.receive(m);
//             context.get(Variable.TxDelayTrace).<DescriptiveStatistics>value().addValue(
//                     (double)(m.receptionTime - m.physicalClock)
//             );

            reportEvent(m, 'r');

            return true;
       }

       return false;
          
    }

    public Message receive(long now){

            ArrayList a = null;

               a = nic_in.getMsgs((int)now);
               if (a.isEmpty()) {
                   return null;
               }

            Message msg;
            msg = (Message) a.get(0);

            msg.receptionTime = (int)now;

            return msg;

    }

    public Message pending(){
       return pending(clock.value());
    }
    
    public Message pending(long now){

            ArrayList a = exc_in.getMsgs((int)now);

            if (!(a != null && !a.isEmpty())) {
               return null;
            }

            Message msg;
            msg = (Message) a.get(0);

            return msg;
       
    }
    
    public boolean send(){
       return send(clock.value());
    }

    public boolean send(long now){
            ArrayList a = nic_out.getMsgs((int)now);
            if (a.isEmpty()) {
                return false;
            }

            Message msg;
            Network network = context.get(Variable.Network).<Network>value();

            msg = (Message) a.get(0);
            network.send(msg);
            reportEvent(msg, 's');

            return true;
       
    }
    
    public final void reportEvent(Message msg, char ev) {
        try{
                String saida = ""+
                agent.ID +"; "+
                ev+"; "+
                msg.sender+"; "+
                msg.destination+"; "+
                (int)cpu.value()+"; "+
                //(int)clock.value()+"; "+
                msg.physicalClock+"; "+
                msg.logicalClock+"; "+
                msg.type+"; "+
                msg.content;
                debug(saida);
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    private final int getAgentProcessingTime() {
        /*
         * Se o processo for síncrono o tempo de resposta do processament
         * é de um tick, caso contrário é aleatório.
         */
        if (clock.getMode() =='s') {
            return 1;
        }
        else {
            return (int) (MAX_PROCESSA * Math.random());
        }
    }

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

    public void nicout(Message m){
//        synchronized(this){
            //nic_out.add((int)(clock.value()), m);
            nic_out.add((int)(clock.value()), m);
//        }
    }

   public boolean advance() {
      return this.context.advance();
   }

   public long exec(Object data){
      
       return cpu.exec(data);
//       long stime = clock.value();
//       long ftime = stime + btime;

//       for(long i = stime; i < ftime; i++){
//          while(send(i));
//       }

   }
}
