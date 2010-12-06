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
public class PBFTCommitExecutor extends Executor{

    public PBFTCommitExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = (PBFTMessage) act.getWrapper();
        if(checkCommit(m)){
            makeExecute(m);
       }
    }

    /**
     *
     * @param m
     * @return
     */
    private PBFTMessage makeExecute(PBFTMessage m) {

        PBFTMessage req = (PBFTMessage)m.get(PBFTMessage.REQUESTFIELD);
        
        if(!(req.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.RECEIVEREQUEST)))
            return req;

        req.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.EXECUTE);
        req.put(PBFTMessage.SEQUENCENUMBERFIELD, m.get(PBFTMessage.SEQUENCENUMBERFIELD));

        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTCommitExecutor.execute] call server.execute for request " + req
          + " by server(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );

        getProtocol().doAction(req);
        
        return req;
        
    }
    

    /**
     * this method must check the encriptation of the message and do the
     * procedures specified in Castro and Liskov (1999).
     * @param m
     * @return
     */
    private boolean checkCommit(PBFTMessage m) {
        if ( isValidCommit(m) ){
            addToBuffer(m);
            return gotQuorum(m);
        }

        return false;
        
    }

    private void addToBuffer(PBFTMessage m) {

        Buffer buffer = ((PBFT)getProtocol()).getCommitBuffer();

        if(buffer.contains(m)){
            ((PBFT)getProtocol()).getDebugger().debug(
                "[PBFTCommitExecutor.execute] commit " + m
              + " was rejected in server(p" + getProtocol().getLocalProcess().getID() + ") "
              + " at time " + ((PBFT)getProtocol()).getTimestamp() + " "
              + "because it's already in received."
             );

            return;
        }

        buffer.add(m);

        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTCommitExecutor.execute] prepare " + m
          + " was buffered in server(p" + getProtocol().getLocalProcess().getID() + ") "
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );

    }

    public boolean isValidCommit(PBFTMessage m){
        Authenticator authenticator =
        ((PBFT)getProtocol()).getServerAuthenticator();

        if(!authenticator.check(m)){
            return false;
        }

        return (authenticator.chechDisgest(m) && belongsToCurrentView(m) && existsPrepare(m));

    }


        /**
         * [TODO] this method verifies if there was a PrePrepare previously
         * to this message
         */
    private boolean existsPrepare(PBFTMessage m) {
        return ((PBFT)getProtocol()).existsPrepare(m);
    }

    private boolean belongsToCurrentView(PBFTMessage m) {
        return ((PBFT)getProtocol()).belongsToCurrentView(m);
    }


    private boolean gotQuorum(PBFTMessage m) {
        return ((PBFT)getProtocol()).gotQuorum(m);
    }

}
