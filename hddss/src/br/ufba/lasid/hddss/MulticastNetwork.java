/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hddss;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class MulticastNetwork extends NetworkDeterministic{
    NetworkMembership membership = new NetworkMembership();

    private class NetworkMembership extends Hashtable<Integer, ArrayList<Integer>>{
        
    }

    public void join(int group, Agent agent){
        
        if(group > 0) group *= -1;

        if(group == 0) {
            debug("["+MulticastNetwork.class.getName()+"]zero isn't a valid address for multicast group.");
            return;
        }

        ArrayList<Integer> members = membership.get(group);

        if(!membership.containsKey(group)){
            members = new ArrayList<Integer>();
        }

        if(!members.contains(agent.id)){
            members.add(agent.id);
        }

        membership.put(group, members);
    }

    public void leave(int group, Agent agent){
        if(group > 0) group *= -1;

        if(group == 0) {
            debug("["+MulticastNetwork.class.getName()+"]zero isn't a valid address for multicast group.");
            return;
        }

        ArrayList<Integer> members = membership.get(group);

        if(members != null) members.remove(new Integer(agent.id));

    }
    

    @Override
    public void propagate(Message msg, double at) {
        if(msg.destination < 0){
            multicast(msg, at);
            return;
        }
        
        super.propagate(msg, at);
    }


    public void multicast(Message msg, double at){
        int group = Math.abs(msg.destination);
        ArrayList<Integer> members = membership.get(group);

        for(Integer member : members){
            int p_i = msg.sender;
            int p_j = member.intValue();
            if(verifyChannel(p_i, p_j)){
                transfer(p_i, p_j, msg, at);
            }
        }
    }
}
