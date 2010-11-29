/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.cs;

import br.ufba.lasid.jds.cs.ClientServerProtocol;
import br.ufba.lasid.jds.comm.Communicator;
import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.prototyping.hddss.Agent;
import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.prototyping.hddss.cs.comm.SimulatedClientServerCommunicator;
/**
 *
 * @author aliriosa
 */
public class Agent_ServiceComponent extends Agent implements Communicator, Process<Integer>{
    protected ClientServerProtocol proto = new ClientServerProtocol();

    public ClientServerProtocol getProtocol() {
        return proto;
    }

    public void setProtocol(ClientServerProtocol proto) {
        this.proto = proto;
    }


    @Override
    public void setup() {
        super.setup();
        proto.setCommunicator(new SimulatedClientServerCommunicator(this));
        proto.setLocalProcess(this);
    }

    public void multicast(Message m, br.ufba.lasid.jds.Process group) {
        proto.getCommunicator().multicast(m, group);
    }

    public void unicast(Message m, br.ufba.lasid.jds.Process process) {
        proto.getCommunicator().unicast(m, process);
    }

    public Integer getID() {
        return new Integer(this.id);
    }

    public void setID(Integer id) {
        this.id = id.intValue();
    }

}
