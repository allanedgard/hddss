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
 * @author Anne
 */
public class Scenario_Spa extends Scenario{

    ArrayList<Integer> agents;
    ArrayList<Integer> agentsPerPartition;
    ArrayList<ArrayList<Integer>> partitions;
    int synchronous;
    int asynchronous;

    public void setNumberOfAgentsPerType(String n)
    {
        StringTokenizer st = new StringTokenizer(n);
        agents = new ArrayList<Integer>();
        while(st.hasMoreTokens())
        {
            agents.add(Integer.parseInt(st.nextToken()));
        }
    }

    public void setNumberOfAgentsPerPartition(String n)
    {
        StringTokenizer st = new StringTokenizer(n);
        agentsPerPartition = new ArrayList<Integer>();
        while(st.hasMoreTokens())
        {
            agentsPerPartition.add(Integer.parseInt(st.nextToken()));
        }
        definePartitions();
    }

    public void setSynchronousChannelType(String s)
    {
        synchronous = Integer.parseInt(s);
    }

    public void setAsynchronousChannelType(String a)
    {
        asynchronous = Integer.parseInt(a);
    }

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

    @Override
    public void initChannels() {
        int n = container.get(Variable.NumberOfAgents).<Integer>value().intValue();
        for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                    if(samePartition(i, j))
                        container.network.handshaking(i, j, synchronous);
                    else
                        container.network.handshaking(i, j, asynchronous);
                    //System.out.println("["+i+"]"+"["+j+"]"+"=>"+conteiner.network.channels[i][j]);
                }
        }
    }

    public void definePartitions() {
        partitions = new ArrayList<ArrayList<Integer>>();
        int z=-1;
        for(int i=0; i<agentsPerPartition.size(); i++) {
            ArrayList<Integer> part = new ArrayList<Integer>();
            for(int j=0; j<agentsPerPartition.get(i); j++) {
                z++;
                part.add(z);
            }
            partitions.add(i, part);
            //System.out.println(part.toString());
        }
    }

    public boolean samePartition(int x, int y) {
        for(int i=0; i<partitions.size(); i++) {
            if((partitions.get(i).contains((Integer)x)) && (partitions.get(i).contains((Integer)y)))
                return true;
        }
        return false;
    }

}
