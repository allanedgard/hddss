/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss;

import br.ufba.lasid.jds.prototyping.hddss.RuntimeSupport.Variable;

/**
 *
 * @author Anne
 */
public class Scenario {

    public Agent p[];
    Simulator container;

    static final String TAG = "scenario";

    Scenario() {
 
    }

    public void init(Simulator s) {
        container = s;
        int n = container.get(Variable.NumberOfAgents).<Integer>value().intValue();
        p = new Agent[n];
    }

    public void initAgents() throws Exception{
        int n = container.get(Variable.NumberOfAgents).<Integer>value().intValue();
        for(int i = 0; i < n; i++){
                String TAG = Agent.TAG;
                String TAGi = TAG + "["+i+"]";

                if(!container.config.getString(TAGi, "null").equals("null")){
                    p[i] = (Agent) Factory.create(TAGi, Agent.class.getName());
                }else{
                    p[i] = (Agent) Factory.create(Agent.TAG, Agent.class.getName());
                }

                p[i].setAgentID(i);
                p[i].setType(container.get(Variable.Type).<String>value().charAt(0));

                prepareAgent(p[i]);

                Factory.setup(p[i], TAG);
                
                if(!container.config.getString(TAGi, "null").equals("null")){
                    Factory.setup(p[i], TAGi);
                }            
            
            /*
             * OLD STUFF
             p[i] = (Agent) Factory.create(Agent.TAG, Agent.class.getName());

             p[i].setAgentID(i);
             p[i].setType(container.get(Variable.Type).<String>value().charAt(0));

             prepareAgent(p[i]);

             Factory.setup(p[i], Agent.TAG);
            */
        }
    }

    public void initChannels() {
        int n = container.get(Variable.NumberOfAgents).<Integer>value().intValue();
        for(int i = 0; i < n; i++)
                for(int j = 0; j < n; j++)
                    container.network.handshaking(i, j);
    }

    public void prepareAgent(Agent a) throws Exception{
        int n = container.get(Variable.NumberOfAgents).<Integer>value().intValue();
        a.infra = new RuntimeContainer(container);
        a.infra.register(a);
        a.infra.nprocess = n;

        AbstractClock _clock = (AbstractClock) Factory.create(AbstractClock.TAG, AbstractClock.class.getName());
        Factory.setup(_clock, AbstractClock.TAG);

        a.infra.clock = _clock;
        if(a.infra.clock instanceof Clock_Virtual){
            ((Clock_Virtual)(a.infra.clock)).nticks = (int) (1/container.ro);
            ((Clock_Virtual)(a.infra.clock)).rho =
                    ((new Randomize()).irandom(-container.maxro,container.maxro));
        }

         a.infra.cpu = (CPU) Factory.create(CPU.TAG, CPU.class.getName());

         Factory.setup(a.infra.cpu, CPU.TAG);

         a.infra.cpu.setClock(_clock);

        //a.infra.scheduler = scheduler;

        a.init();
    }

}
