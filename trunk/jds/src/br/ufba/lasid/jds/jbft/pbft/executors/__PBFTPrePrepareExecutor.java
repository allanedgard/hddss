/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.security.Authenticator;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class __PBFTPrePrepareExecutor extends Executor{
    Object[] Quorum;
    public __PBFTPrePrepareExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = (PBFTMessage) act.getWrapper();

        if(checkPrePrepare(m)){
         
           //makePrepare(m);
        }
    }

    /**
     * [TODO] this method must check the encriptation of the message and do the
     * procedures specified in Castro and Liskov (1999).
     * @param m
     * @return
     */

    private boolean checkPrePrepare(PBFTMessage m) {
        if(isValidPrePrepare(m)){
            addToBuffer(m);
            return true;
        }
        return false;
    }
    /**
     * [TODO]check authentication, view, and sequence number
     * @param m
     * @return
     */
    private boolean isValidPrePrepare(PBFTMessage m) {
        Authenticator authenticator =
        ((PBFT)getProtocol()).getServerAuthenticator();
        if(!authenticator.check(m)){
            return false;
        }
        
        return authenticator.chechDisgest(m) && belongsToCurrentView(m);

    }

    private boolean belongsToCurrentView(PBFTMessage m) {
        return ((PBFT)getProtocol()).belongsToCurrentView(m);
    }


    private void addToBuffer(PBFTMessage m) {
        Buffer buffer = ((PBFT)getProtocol()).getPreprepareBuffer();
        if(buffer.contains(m)){
            ((PBFT)getProtocol()).getDebugger().debug(
                "[PBFTPrePrepareExecutor.execute] preperare " + m
              + " was rejected in server(p" + getProtocol().getLocalProcess().getID() + ") "
              + " at time " + ((PBFT)getProtocol()).getTimestamp() + " "
              + "because it's already in received."
             );

            return;            
        }

        buffer.add(m);

        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTPrePrepareExecutor.execute] prepepare " + m
          + " was buffered in server(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );
    }

    /**
     * 
     * @param m
     * @return
     */
/*    private PBFTMessage makePrepare(PBFTMessage pp) {
        Authenticator authenticator =
            ((PBFT)getProtocol()).getServerAuthenticator();

        PBFTMessage p = new PBFTMessage();
        
        p.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVEPREPARE);
        p.put(PBFTMessage.VIEWFIELD, pp.get(PBFTMessage.VIEWFIELD));
        p.put(PBFTMessage.SEQUENCENUMBERFIELD, pp.get(PBFTMessage.SEQUENCENUMBERFIELD));
        p.put(PBFTMessage.DIGESTFIELD, pp.get(PBFTMessage.DIGESTFIELD));
        p.put(PBFTMessage.REPLICAIDFIELD, getProtocol().getLocalProcess().getID());
        p.put(PBFTMessage.REQUESTFIELD, pp.get(PBFTMessage.REQUESTFIELD));

        p = (PBFTMessage)authenticator.encrypt(p);
        
       getProtocol().getCommunicator().multicast(
            p, ((PBFT)getProtocol()).getLocalGroup()
        );

        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTPrePrepareExecutor.execute] prepare " + p
          + " was sending by server(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );

        return p;
    }

*/

}
