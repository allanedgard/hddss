package br.ufba.lasid.hddss;

import java.util.ArrayList;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * A RuntimeContainer can be a Operating System, a Middleware or a Simulator.
 * It allows to hide details about the execution infra of the agent.
 * @author aliriosa
 */
public class RuntimeContainer extends Thread implements RuntimeSupport{
    public Clock clock;
    RuntimeSupport context;
    
    Buffer nic_out; //send buffer
    Buffer nic_in;  //receive buffer
    Buffer app_in;  //deliver buffer

    FaultModelAgent faultModel;

    Agent  agent;

    static int MAX_PROCESSA = 100;

    int nprocess = 0;

    RuntimeVariables variables = new RuntimeVariables();
    
    public RuntimeContainer(RuntimeSupport context){
        this.context = context;
        nic_in = new Buffer();
        nic_out = new Buffer();
        app_in = new Buffer();
        
    }

    public boolean register(Agent agent){
        this.agent = agent;
        return true;
    }
    
    public void execute(){
        if(agent.status()){
            while(send());
            while(receive());
            while(deliver());
        }
        ((Clock_Virtual)clock).tick();

        if(((Clock_Virtual)clock).tickValue() == 1 && agent.status()) {
            agent.execute();
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

                debug("Modelo de Falhas "+fault_model+" implantado em p"+agent.id);

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



    public synchronized boolean deliver(){
        ArrayList a = app_in.getMsgs((int)clock.value());
        if (a.isEmpty()) {
            return false;
        }

        Message msg;
        msg = (Message) a.get(0);
        
        reportEvent(msg, 'd');

        if (msg.payload) {
            context.get(Variable.DlvDelayTrace).<DescriptiveStatistics>value().addValue(
                    (int)clock.value() - msg.physicalClock
            );

            context.get(Variable.RxDelayTrace).<DescriptiveStatistics>value().addValue(
                    (int)clock.value() - msg.tempoRecepcao
            );

            context.get(Variable.RxDelayTrace).<DescriptiveStatistics>value().addValue(
                    msg.tempoRecepcao-msg.physicalClock
            );

        }
        agent.deliver(msg);
        return true;
            
        
    }

    public boolean receive(){
        ArrayList a = nic_in.getMsgs((int)clock.value());
        if (a.isEmpty()) {
            return false;
        } 

        Message msg;
        msg = (Message) a.get(0);
        reportEvent(msg, 'r');
        msg.tempoRecepcao = (int)clock.value();
        agent.receive(msg);
        return true;
          
    }


    public boolean send(){
        ArrayList a = nic_out.getMsgs((int)clock.value());
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
                agent.id +"; "+
                ev+"; "+
                msg.sender+"; "+
                msg.destination+"; "+
                (int)clock.value()+"; "+
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

        boolean debug = context.get(Variable.Debug).<Boolean>value();
        java.io.PrintStream out = context.get(Variable.StdOutput).<java.io.PrintStream>value();
        
        if (debug) out.println(d);
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



}
