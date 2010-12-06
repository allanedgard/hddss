/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

/**
 *
 * @author allan
 */

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.Protocol;
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
public class PBFTNewViewExecutor extends PBFTServerExecutor{

    public PBFTNewViewExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

   @Override
    public synchronized void execute(Action act) {
            ((PBFT)getProtocol()).getDebugger().debug(
                "[PBFTChangeViewExecutor.execute]"
             );

       PBFTMessage m = makeChangeViewRequest((PBFTMessage) act.getWrapper());
       scheduleRetransmission(m);

    }

   public PBFTMessage makeChangeViewRequest(PBFTMessage m){

        Group g = (Group) m.get(PBFTMessage.DESTINATIONFIELD);

        m = PBFTMessage.translateTo(m, PBFTMessage.TYPE.NEWVIEW);

        Long timestamp =  ((PBFT)getProtocol()).getTimestamp();
        try {
        m.put(PBFTMessage.SETPREPREPAREMSGS, getPrePrepareMessages());
        m.put(PBFTMessage.VIEWCHANGEMSGS, getViewChangeMessages());
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

    public Object getPrePrepareMessages() throws Exception {
         throw new Exception("not yet implemented");
    }

    public Object getViewChangeMessages() throws Exception {
         throw new Exception("not yet implemented");
    }

    public void scheduleRetransmission(PBFTMessage m){

        Long timeout   = ((PBFT)getProtocol()).getRetransmissionTimeout();
        Long timestamp =((PBFT)getProtocol()).getTimestamp();
        Long rttime = new Long(timestamp.intValue() + timeout.longValue());

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
