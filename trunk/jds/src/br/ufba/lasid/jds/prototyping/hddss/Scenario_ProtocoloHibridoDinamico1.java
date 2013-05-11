/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.lasid.jds.prototyping.hddss;


import br.ufba.lasid.jds.prototyping.hddss.RuntimeSupport.Variable;
import br.ufba.lasid.jds.prototyping.hddss.instances.Agent_AdaptConsensus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import sun.security.krb5.internal.Ticket;
/**
 *
 * @author Allan
 */
public class Scenario_ProtocoloHibridoDinamico1 extends Scenario {
    
    ArrayList<Integer> agents;
    ArrayList<Integer> agentsPerPartition;
    ArrayList<ArrayList<Integer>> partitions;
    int timely;
    int untimely;
    int initial, ending;
    Randomize r;
    
    public void setDeterministicChannelType(String s)
    {
        timely = Integer.parseInt(s);
    }

    public void setProbabilisticChannelType(String a)
    {
        untimely = Integer.parseInt(a);
    }

    public void setInitialAlt(String a)
    {
        initial = Integer.parseInt(a);
    }
    
    public void setEndingAlt(String a)
    {
        ending = Integer.parseInt(a);
    }
    
    int i1, j1;
    
    @Override
    public void execute() {
        
        int n = container.get(Variable.NumberOfAgents).<Integer>value().intValue();
        if (globalClock.tickValue()==0)
                if ( ((((Clock_Virtual)globalClock).value() % 1) == 0) && (i1<n) ) {
            //if ( (i1 % 2) != (j1 % 2) ) {
                //System.out.println("degradando i ="+ i1+" e j="+j1);
                //System.out.println( ((Agent_AdaptConsensus) p[1]).uncertain);
                
                    container.network.handshaking(i1, j1, untimely);
                    container.network.handshaking(j1, i1, untimely);
                    
            //} 
                j1++;
                if (j1==n) {
                    j1=0;
                    i1++;
                }
               
            }   
        if (globalClock.tickValue()==0)
            System.out.println("p1;"+((Clock_Virtual)globalClock).value()+";"+ ((Agent_AdaptConsensus) p[1]).uncertain.size() );
        //System.out.println( ((Agent_AdaptConsensus) p[1]).uncertain);
    }
    
    
    @Override
    public void initAgents() throws Exception{
        r = new Randomize();
        int n = container.get(Variable.NumberOfAgents).<Integer>value().intValue();
        for(int i = 0; i < n; i++){
                String TAG = Agent.TAG;
                String TAGi = TAG + "["+i+"]";
                String TAGAlt = TAG+ "Alternative";
                
                if ( (initial ==0 ) && (ending == 0)) {
                    if(!container.config.getString(TAGi, "null").equals("null")){
                        p[i] = (Agent) Factory.create(TAGi, Agent.class.getName());
                    }else{
                        p[i] = (Agent) Factory.create(Agent.TAG, Agent.class.getName());
                    }
                } else 
                { 
                    p[i] = (Agent) Factory.create(TAGAlt, Agent.class.getName()); 
                };

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

    @Override
    public void initChannels() {
        int n = container.get(Variable.NumberOfAgents).<Integer>value().intValue();
        for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                    if( (i%2) == (j%2) )
                        container.network.handshaking(i, j, timely);
                    else
                        container.network.handshaking(i, j, timely);
                    //System.out.println("["+i+"]"+"["+j+"]"+"=>"+container.network.channels[i][j]);
                }
        }    
    }
    
    
}
