/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.lasid.jds.prototyping.hddss;


import br.ufba.lasid.jds.prototyping.hddss.RuntimeSupport.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
/**
 *
 * @author Allan
 */
public class Scenario_ProtocoloA extends Scenario {
    
    ArrayList<Integer> agents;
    ArrayList<Integer> agentsPerPartition;
    ArrayList<ArrayList<Integer>> partitions;
    int reliable;
    int unreliable;

    public void setReliableChannelType(String s)
    {
        reliable = Integer.parseInt(s);
    }

    public void setUnreliableChannelType(String a)
    {
        unreliable = Integer.parseInt(a);
    }

    /*
    @Override
    public void initAgents() throws Exception{
        int z=-1;
        for(int i = 0; i < agents.size(); i++){
            String TAG = Agent.TAG;
            String TAGi = TAG + "["+i+"]";
            for(int j = 0; j < agents.get(i); j++){
                z++;
                if(!container.config.getString(TAGi, "null").equals("null")){
                    p[z] = (Agent) Factory.create(TAGi, Agent.class.getName());
                }else{
                    p[z] = (Agent) Factory.create(Agent.TAG, Agent.class.getName());
                }

                p[z].setAgentID(z);
                p[z].setType(container.get(Variable.Type).<String>value().charAt(0));

                prepareAgent(p[z]);

                if(!container.config.getString(TAGi, "null").equals("null")){
                    Factory.setup(p[z], TAGi);
                }
                else {
                    Factory.setup(p[z], TAG);
                }
                //System.out.println(z+"=>"+p[z].infra.faultModel);
            }
        }
    }
     * 
     */

    @Override
    public void initChannels() {
        int n = container.get(Variable.NumberOfAgents).<Integer>value().intValue();
        for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                    if(j==1)
                        container.network.handshaking(i, j, reliable);
                    else
                        container.network.handshaking(i, j, unreliable);
                    System.out.println("["+i+"]"+"["+j+"]"+"=>"+container.network.channels[i][j]);
                }
        }    
    }
    
    
}
