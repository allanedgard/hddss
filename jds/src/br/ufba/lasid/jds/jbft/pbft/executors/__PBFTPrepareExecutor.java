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
public class __PBFTPrepareExecutor extends Executor{

    public __PBFTPrepareExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {        
        
        PBFTMessage m = (PBFTMessage) act.getWrapper();
        
        if(checkPrepare(m)){
            //makeCommit(m);
        }
        
    }

    /**
     *
     * @param m
     * @return
     */
    /*
    private PBFTMessage makeCommit(PBFTMessage m) {
        Authenticator authenticator =
            ((PBFT)getProtocol()).getServerAuthenticator();

        PBFTMessage c = new PBFTMessage();

        c.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVECOMMIT);
        c.put(PBFTMessage.VIEWFIELD, m.get(PBFTMessage.VIEWFIELD));
        c.put(PBFTMessage.SEQUENCENUMBERFIELD, m.get(PBFTMessage.SEQUENCENUMBERFIELD));
        c.put(PBFTMessage.DIGESTFIELD, m.get(PBFTMessage.DIGESTFIELD));
        c.put(PBFTMessage.REPLICAIDFIELD, getProtocol().getLocalProcess().getID());
        c.put(PBFTMessage.REQUESTFIELD, m.get(PBFTMessage.REQUESTFIELD));
        
        c = (PBFTMessage)authenticator.encrypt(c);

       getProtocol().getCommunicator().multicast(
            c, ((PBFT)getProtocol()).getLocalGroup()
        );

        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTPrepareExecutor.execute] commit " + c
          + " was sending by server(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );

        return c;
    }
*/
    public boolean isValidPrepare(PBFTMessage m){
        Authenticator authenticator =
        ((PBFT)getProtocol()).getServerAuthenticator();
        
        if(!authenticator.check(m)){
            return false;
        }

        return (authenticator.chechDisgest(m) && belongsToCurrentView(m) && existsPrePrepare(m));
        
    }

    private void addToBuffer(PBFTMessage m) {

        Buffer buffer = ((PBFT)getProtocol()).getPrepareBuffer();
        
        if(buffer.contains(m)){
            ((PBFT)getProtocol()).getDebugger().debug(
                "[PBFTPrepareExecutor.execute] prepare " + m
              + " was rejected in server(p" + getProtocol().getLocalProcess().getID() + ") "
              + " at time " + ((PBFT)getProtocol()).getTimestamp() + " "
              + "because it's already in received."
             );

            return;
        }

        buffer.add(m);

        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTPrepareExecutor.execute] prepare " + m
          + " was buffered in server(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );
        
    }


    /**
     * this method must check the encriptation of the message and do the
     * procedures specified in Castro and Liskov (1999).
     * @param m
     * @return
     */
    private boolean checkPrepare(PBFTMessage m) {
        
        if (isValidPrepare(m)){
            addToBuffer(m);
            return gotQuorum(m);
        }
        
        return false;
    }

    private boolean existsPrePrepare(PBFTMessage m) {
        return ((PBFT)getProtocol()).existsPrePrepare(m);
    }

    private boolean belongsToCurrentView(PBFTMessage m) {
        return ((PBFT)getProtocol()).belongsToCurrentView(m);
    }

    private boolean gotQuorum(PBFTMessage m) {
        return ((PBFT)getProtocol()).gotQuorum(m);
    }



}
