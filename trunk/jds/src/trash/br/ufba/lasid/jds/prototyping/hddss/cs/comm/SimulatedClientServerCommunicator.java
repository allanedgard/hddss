/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.prototyping.hddss.cs.comm;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.prototyping.hddss.Agent;
import br.ufba.lasid.jds.comm.communicators.ICommunicator;
import org.apache.commons.collections.Buffer;

/**
 *
 * @author aliriosa
 */
public class SimulatedClientServerCommunicator implements ICommunicator{
    
    protected Agent agent;

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    
    public SimulatedClientServerCommunicator(Agent agent){
        this.agent = agent;
    }
    
    public void multicast(IMessage m, Object group) {

        throw new UnsupportedOperationException("Not supported yet.");

    }

    public void unicast(IMessage m, IProcess destination) {

        Integer dest = (Integer) destination.getID();

        int source = agent.ID;
        int destin = dest.intValue();
        int now   = (int) agent.infra.clock.value();
        int type  = getTypeValue(m);
        
        agent.send(
         new br.ufba.lasid.jds.prototyping.hddss.Message(
            source, destin, type, 0, now, m
         )
        );

    }

    protected int getTypeValue(IMessage m){
        return -1; //((ClientServerMessage.TYPE)m.get(ClientServerMessage.TYPEFIELD)).getValue();
    }

    public void multicast(IMessage m, IGroup g) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void receive(IMessage m) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
