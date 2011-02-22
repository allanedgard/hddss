/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.prototyping.hddss.cs;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.group.IGroup;
import trash.br.ufba.lasid.jds.cs.ClientServerProtocol;
import br.ufba.lasid.jds.comm.communicators.ICommunicator;
import br.ufba.lasid.jds.prototyping.hddss.Agent;
import br.ufba.lasid.jds.IProcess;
import trash.br.ufba.lasid.jds.prototyping.hddss.cs.comm.SimulatedClientServerCommunicator;
import org.apache.commons.collections.Buffer;
/**
 *
 * @author aliriosa
 */
public class Agent_ServiceComponent extends Agent implements ICommunicator, IProcess<Integer>{
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

    public void setID(Integer id) {
        this.ID = id.intValue();
    }

    public Buffer getInbox() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setInbox(Buffer inbox) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void multicast(IMessage m, IGroup g) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unicast(IMessage m, IProcess p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void receive(IMessage m) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer getID() {
        return this.ID;
    }

}
