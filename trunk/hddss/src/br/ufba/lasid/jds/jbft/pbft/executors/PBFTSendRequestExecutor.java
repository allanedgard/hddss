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
import br.ufba.lasid.jds.security.Authenticator;

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

        Long timestamp =  ((PBFT)getProtocol()).getTimestamp();
        
        m.put(PBFTMessage.TIMESTAMPFIELD, timestamp);
        m.put(PBFTMessage.CLIENTFIELD, getProtocol().getLocalProcess());

        Authenticator authenticator =
            ((PBFT)getProtocol()).getClientMessageAuthenticator();

        m = (PBFTMessage)authenticator.encrypt(m);
        
        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTSendRequestExecutor.execute] sending of "
          + "(" + m + ") at time " + timestamp
         );


        getProtocol().getCommunicator().multicast(m, g);

        return m;
        
    }

    public void scheduleRetransmission(PBFTMessage m){
        
        Long timeout   = ((PBFT)getProtocol()).getRetransmissionTimeout();
        Long timestamp =((PBFT)getProtocol()).getTimestamp();
        Long rttime = new Long(timestamp.intValue() + timeout.longValue());

        PBFTRequestRetransmistionScheduler scheduler =
                (PBFTRequestRetransmistionScheduler)(((PBFT)getProtocol()).getClientScheduler());

        m.put(scheduler.getTAG(), rttime);

        scheduler.schedule(m);

        ((PBFT)getProtocol()).getDebugger().debug(
            "["+ getClass().getSimpleName()+ ".scheduleRetransmission] "
          + "scheduling of (" + m + ") for time " + rttime
         );
        
    }

}

