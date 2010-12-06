/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTRequestRetransmistionScheduler;
import br.ufba.lasid.jds.security.Authenticator;

/**
 *
 * @author aliriosa
 */
public class PBFTChangeViewExecutor extends Executor{

    public PBFTChangeViewExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    /**
     * [TODO]
     * @param act
     */
    @Override
    public synchronized void execute(Action act) {
            ((PBFT)getProtocol()).getDebugger().debug(
                "[PBFTChangeViewExecutor.execute]"
             );

       //PBFTMessage m = makeChangeViewRequest((PBFTMessage) act.getMessage());
       //scheduleRetransmission(m);

    }

   public PBFTMessage makeChangeViewRequest(PBFTMessage m){

        Group g = (Group) m.get(PBFTMessage.DESTINATIONFIELD);

        m = PBFTMessage.translateTo(m, PBFTMessage.TYPE.CHANGEVIEW);

        Long timestamp =  ((PBFT)getProtocol()).getTimestamp();

        m.put(PBFTMessage.TIMESTAMPFIELD, timestamp);
        m.put(PBFTMessage.CLIENTFIELD, getProtocol().getLocalProcess());
        int newView = ((Integer) getProtocol().getContext().get(PBFTMessage.VIEWFIELD))+1;
        m.put(PBFTMessage.VIEWFIELD, newView);
        int n = ( (Integer) ((PBFT)getProtocol()).getContext().get(PBFT.CHECKPOINTNUMBER));
        m.put(PBFTMessage.CHECKPOINTNUMBER, n);
        try {
        m.put(PBFTMessage.CHECKPOINTMSGS, getCheckPointMessages());
        m.put(PBFTMessage.NSREQUESTS, getNotStableRequests());
        }
        catch(Exception e) { };
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

    public Object getCheckPointMessages() throws Exception {
         throw new Exception("not yet implemented");
    }

    public Object getNotStableRequests() throws Exception {
         throw new Exception("not yet implemented");
    }

    public void scheduleRetransmission(PBFTMessage m){

        Long timeout   = ((PBFT)getProtocol()).getRetransmissionTimeout();
        Long timestamp =((PBFT)getProtocol()).getTimestamp();
        long rttime = timestamp.intValue() + timeout.longValue();

        PBFTRequestRetransmistionScheduler scheduler =
                (PBFTRequestRetransmistionScheduler)(((PBFT)getProtocol()).getClientScheduler());

        m.put(scheduler.getTAG(), rttime);

        scheduler.schedule(m, rttime);

        ((PBFT)getProtocol()).getDebugger().debug(
            "["+ getClass().getSimpleName()+ ".scheduleRetransmission] "
          + "scheduling of (" + m + ") for time " + rttime
         );

    }
    
}
