/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

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
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveNewViewExecutor extends PBFTServerExecutor{

    public PBFTReceiveNewViewExecutor(DistributedProtocol protocol) {
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
           if (checkReceiveNewView(m)) {
               enviaPrepares(m);
               ((PBFT) getProtocol()).setCurrentView((Integer)m.get(PBFTMessage.VIEWFIELD));
           }

        }

    }

    boolean checkReceiveNewView(PBFTMessage m) {
         if (isValidChangeView(m)){

            return true;
        }

        return false;
    }

    public boolean isValidChangeView(PBFTMessage m){
        Authenticator authenticator =
        ((PBFT)getProtocol()).getServerAuthenticator();

        if(!authenticator.check(m)){
            return false;
        }

        /*
         * Verifica se é a visão valida
         *
         */
        if ( (Integer)m.get(PBFTMessage.VIEWFIELD)-1 == ((PBFT) getProtocol()).getCurrentView() ) {

             return   (authenticator.chechDisgest(m)) && checkSetPrePrepareMsgs(m);
        }
        return false;
    }

    boolean makeChangeView(PBFTMessage m) {
        return false; // to implement
    }



    private boolean gotQuorum(PBFTMessage m) {
        return ((PBFT)getProtocol()).gotQuorum(m);
    }

    private boolean checkSetPrePrepareMsgs(PBFTMessage m) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void enviaPrepares(PBFTMessage m) {
        /*
         * Para cada requisicao encapsulada na mensagem envia um Prepare!
         *
         */
        throw new UnsupportedOperationException("Not yet implemented");
    }



}

