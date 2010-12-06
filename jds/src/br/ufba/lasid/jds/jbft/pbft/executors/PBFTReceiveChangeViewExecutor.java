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
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveChangeViewExecutor extends Executor{

    public PBFTReceiveChangeViewExecutor(DistributedProtocol protocol) {
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
        PBFTMessage m = (PBFTMessage) act.getWrapper();
        if ( ((PBFT)getProtocol()).isPrimary() ) {
           if (checkReceiveChangeView(m)) {
               
           }

        }

    }

    boolean checkReceiveChangeView(PBFTMessage m) {
         if (isValidChangeView(m)){
            addToBuffer(m);
            return gotQuorum(m);
        }

        return false;
    }

    public boolean isValidChangeView(PBFTMessage m){
        Authenticator authenticator =
        ((PBFT)getProtocol()).getServerAuthenticator();

        if(!authenticator.check(m)){
            return false;
        }

        return (authenticator.chechDisgest(m) );

    }

    boolean makeChangeView(PBFTMessage m) {
        return false; // to implement
    }


        private void addToBuffer(PBFTMessage m) {

        Buffer buffer = ((PBFT)getProtocol()).getChangeViewBuffer();

        if(buffer.contains(m)){
            ((PBFT)getProtocol()).getDebugger().debug(
                "[PBFTReceiveChangeView.execute] receive " + m
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

    private boolean gotQuorum(PBFTMessage m) {
        return ((PBFT)getProtocol()).gotQuorum(m);
    }



}

