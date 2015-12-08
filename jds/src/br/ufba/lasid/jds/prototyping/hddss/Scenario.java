/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss;

import br.ufba.lasid.jds.prototyping.hddss.RuntimeSupport.Variable;
import br.ufba.lasid.jds.prototyping.hddss.report.Reporter;

/**
 *
 * @author Anne
 */
public class Scenario {

    public Agent p[];
    public Simulator container;

    static final String TAG = "scenario";
    public Reporter reporter;
    public AbstractClock globalClock;
    
    
    public Scenario() {
        reporter = new Reporter();
    }

    public Simulator getContainer() {
        return container;
    }
    
    public final void increaseTick() {
        ((Clock_Virtual)globalClock).tick();
        this.execute();
    }
    
    public void execute() {
        
    }
    
    public final void init(Simulator s) {
        container = s;
        int n = container.get(Variable.NumberOfAgents).<Integer>value().intValue();
        p = new Agent[n];
        globalClock = new Clock_Virtual();
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
                p[i].setScenario(this);
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
        a.setInfra ( new RuntimeContainer(container) );
        a.getInfra().register(a);
        a.getInfra().nprocess = n;

        AbstractClock _clock = (AbstractClock) Factory.create(AbstractClock.TAG, AbstractClock.class.getName());
        Factory.setup(_clock, AbstractClock.TAG);

        a.getInfra().clock = _clock;
        if(a.getInfra().clock instanceof Clock_Virtual){
            //((Clock_Virtual)(a.getInfra().clock)).nticks = (int) (1/container.ro);
            Clock_Virtual.setNTicks((int) (1/container.ro));
            ((Clock_Virtual)(a.getInfra().clock)).rho =
                    ((new Randomize()).irandom(-container.maxro,container.maxro));
        }

         a.getInfra().cpu = (CPU) Factory.create(CPU.TAG, CPU.class.getName());

         Factory.setup(a.getInfra().cpu, CPU.TAG);

         a.getInfra().cpu.setClock(_clock);

        //a.infra.scheduler = scheduler;

        a.init();
    }

}
