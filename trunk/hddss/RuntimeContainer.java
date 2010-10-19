
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template nic_in the editor.
 */

/**
 * A RuntimeContainer can be a Operating System, a Middleware or a Simulator.
 * It allows to hide details about the execution infra of the agent.
 * @author aliriosa
 */
public class RuntimeContainer extends Thread{
    Clock clock;
    Simulator context;
    
    Buffer nic_out; //send buffer
    Buffer nic_in;  //receive buffer
    Buffer app_in;  //deliver buffer

    ProcessFaultModel faultModel;

    Agent  agent;

    static int MAX_PROCESSA = 100;

    int nprocess = 0;
    
    public RuntimeContainer(Simulator context){
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
        ((VirtualClock)clock).tick();

        if(((VirtualClock)clock).tickValue() == 1 && agent.status()) {
            agent.execute();
        }

        
    }

    public final void avancaTick() {
        if (faultModel == null) {
            execute();
        }
        else faultModel.avancaTick();
    }

    public final void setFaultModel(String fault_model) {
    	try {
    		Class c = Class.forName(fault_model);

    		faultModel =
                    (ProcessFaultModel)
                    c.newInstance();

                faultModel.inicializa(this);

                debug("Modelo de Falhas "+fault_model+" implantado em p"+agent.id);

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    @Override
    public void run() {
        agent.startup();
        while (clock.value() < context.tempofinal) {
            context.avanco(this);
            this.yield();
            if (agent.done)
                break;
        }
//        ri.end();
        context.ok();

        //super.run();
    }



    public synchronized boolean deliver(){
        ArrayList a = app_in.obtemMensagens((int)clock.value());
        if (a.isEmpty()) {
            return false;
        }

        Mensagem msg;
        msg = (Mensagem) a.get(0);
        
        reportEvent(msg, 'd');

        if (msg.payload) {
            context.atraso_entrega.addValue((int)clock.value() - msg.relogioFisico);
            context.atraso_recepcao.addValue((int)clock.value() - msg.tempoRecepcao);
            context.tempo_transmissao.addValue(msg.tempoRecepcao-msg.relogioFisico);
        }
        agent.deliver(msg);
        return true;
            
        
    }

    public boolean receive(){
        ArrayList a = nic_in.obtemMensagens((int)clock.value());
        if (a.isEmpty()) {
            return false;
        } 

        Mensagem msg;
        msg = (Mensagem) a.get(0);
        reportEvent(msg, 'r');
        msg.tempoRecepcao = (int)clock.value();
        agent.receive(msg);
        return true;
          
    }


    public boolean send(){
        ArrayList a = nic_out.obtemMensagens((int)clock.value());
        if (a.isEmpty()) {
            return false;
        }

        Mensagem msg;
        msg = (Mensagem) a.get(0);
        context.network.send(msg);
        reportEvent(msg, 's');
        return true;
                    
    }

    public final void reportEvent(Mensagem msg, char ev) {
        try{
                String saida = ""+
                agent.id +"; "+
                ev+"; "+
                msg.remetente+"; "+
                msg.destinatario+"; "+
                (int)clock.value()+"; "+
                msg.relogioFisico+"; "+
                msg.relogioLogico+"; "+
                msg.tipo+"; "+
                msg.conteudo;
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
        if (context.debug_mode)
            context.out.println(d);
    }



}
