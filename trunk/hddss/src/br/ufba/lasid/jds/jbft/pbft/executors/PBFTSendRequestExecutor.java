/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.executors.ClientServerSendRequestExecutor;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTRequestRetransmistionScheduler;
import br.ufba.lasid.jds.util.Clock;
import br.ufba.lasid.jds.util.Debugger;

/**
 *
 * @author aliriosa
 */
public class PBFTSendRequestExecutor extends ClientServerSendRequestExecutor{

    public PBFTSendRequestExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        
        PBFTMessage m = makeRequest((PBFTMessage) act.getMessage());
        
        scheduleRetransmission(m);
    }

    public PBFTMessage makeRequest(PBFTMessage m){
        
        Group g = (Group) m.get(PBFTMessage.DESTINATIONFIELD);

        m = PBFTMessage.translateTo(m, PBFTMessage.TYPE.RECEIVEREQUEST);

        Long timestamp =  getTimestamp();
        
        m.put(PBFTMessage.TIMESTAMPFIELD, timestamp);
        m.put(PBFTMessage.CLIENTFIELD, getProtocol().getLocalProcess());
       
        getDebugger().debug("[PBFTSendRequestExecutor.execute] sending of (" + m + ") at time " + timestamp);

        getProtocol().getCommunicator().multicast(m, g);

        return m;
        
    }

    public void scheduleRetransmission(PBFTMessage m){
                
        Long timeout = getRetransmissionTimeout();
        Long timestamp = getTimestamp();
        Long rttime = new Long(timestamp.intValue() + timeout.longValue());
        m.put(PBFT.CLIENTRETRANSMISSIONTIMEOUT, rttime);

        PBFTRequestRetransmistionScheduler scheduler = (PBFTRequestRetransmistionScheduler)(getProtocol().getContext().get(PBFT.CLIENTSCHEDULER));

        scheduler.schedule(m);

        getDebugger().debug("[PBFTSendRequestExecutor.scheduleRetransmission] scheduling of (" + m + ") for time " + rttime);
        
    }

    public Long getRetransmissionTimeout(){
        return (Long)getProtocol().getContext().get(PBFT.CLIENTRETRANSMISSIONTIMEOUT);
    }

    public Long getTimestamp(){
        return new Long(((Clock)getProtocol().getContext().get(PBFT.CLOCKSYSTEM)).value());
    }

    public Debugger getDebugger(){
        return (Debugger) getProtocol().getContext().get(PBFT.DEBUGGER);
    }
}

