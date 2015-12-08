/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.lasid.jds.prototyping.hddss.instances;


import br.ufba.lasid.jds.prototyping.hddss.Agent;
import br.ufba.lasid.jds.prototyping.hddss.Factory;
import br.ufba.lasid.jds.prototyping.hddss.RuntimeSupport.Variable;
import br.ufba.lasid.jds.prototyping.hddss.Scenario;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
/**
 *
 * @author Allan
 */
public class Scenario_ProtocoloHibridoDinamico extends Scenario {
    
    ArrayList<Integer> agents;
    ArrayList<Integer> agentsPerPartition;
    ArrayList<ArrayList<Integer>> partitions;
    int timely;
    int untimely;
    int initial, ending;

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
    
    
    
    @Override
    public void initAgents() throws Exception{
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
                        container.network.handshaking(i, j, untimely);
                    //System.out.println("["+i+"]"+"["+j+"]"+"=>"+container.network.channels[i][j]);
                }
        }    
    }
    
    
}
