/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.group.SingleGroup;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTRequestRetransmistionScheduler;
import br.ufba.lasid.jds.prototyping.hddss.RuntimeSupport;
import br.ufba.lasid.jds.prototyping.hddss.cs.Agent_ServiceComponent;
import br.ufba.lasid.jds.prototyping.hddss.pbft.comm.SimulatedPBFTCommunicator;
import br.ufba.lasid.jds.prototyping.hddss.pbft.security.PBFTSimulatedAuthenticator;
import br.ufba.lasid.jds.util.Buffer;
import br.ufba.lasid.jds.util.Scheduler;

/**
 *
 * @author aliriosa
 */
public class Agent_PBFT extends Agent_ServiceComponent implements Group<Integer>{

    Group group = new SingleGroup();

    public void setServerGroupAddress(String addr){
        this.setGroupID(new Integer(addr));
    }

    public void setCurrentPrimary(String addr){
        getProtocol().getContext().put(PBFT.GROUPLEADER, new Integer(addr));
    }

    public void setCurrentPrimary(Integer addr){
        getProtocol().getContext().put(PBFT.GROUPLEADER, addr);
    }

    public void setCurrentView(String v){
        ((PBFT)getProtocol()).setCurrentView(new Integer(v));
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }


    @Override
    public void setup() {
        super.setup();
        setProtocol(new PBFT());
        getProtocol().getContext().put(PBFT.LOCALGROUP, getGroup());
        getProtocol().getContext().put(PBFT.CLOCKSYSTEM, infra.clock);
        getProtocol().getContext().put(PBFT.DEBUGGER, infra);
        getProtocol().getContext().put(PBFT.REQUESTBUFFER, new Buffer());        
        getProtocol().getContext().put(
            PBFT.CLIENTMSGAUTHENTICATOR,
            new PBFTSimulatedAuthenticator(PBFT.CLIENTMSGAUTHENTICATOR)
        );

        getProtocol().getContext().put(
            PBFT.SERVERAUTHENTICATOR,
            new PBFTSimulatedAuthenticator(PBFT.SERVERAUTHENTICATOR)
        );

        getProtocol().getContext().put(
            PBFT.CLIENTSCHEDULER,
            new PBFTRequestRetransmistionScheduler(
                (PBFT)getProtocol(),
                (Scheduler)infra.context.get(RuntimeSupport.Variable.Scheduler).value()
            )
        );

        getProtocol().setCommunicator(new SimulatedPBFTCommunicator(this));
        getProtocol().setLocalProcess(this);
        

    }

    @Override
    public void multicast(Message m, Process group) {
        getProtocol().getCommunicator().multicast(m, group);
    }

    public void setGroupID(Integer id) {
        group.setGroupID(id);
    }

    public Integer getGroupID() {
        return (Integer)group.getGroupID();
    }

    @Override
    public void receive(br.ufba.lasid.jds.prototyping.hddss.Message msg) {
        PBFTMessage m = (PBFTMessage)msg.getContent();
        getProtocol().doAction(m);        
    }



}
