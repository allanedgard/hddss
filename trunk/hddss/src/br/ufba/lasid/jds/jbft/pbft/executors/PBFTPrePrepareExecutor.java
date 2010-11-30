/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.security.Authenticator;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTPrePrepareExecutor extends Executor{
    Buffer buffer = null;
    public PBFTPrePrepareExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = (PBFTMessage) act.getMessage();

        if(checkPrePrepare(m)){
            PBFTMessage p = createPrepare(m);
            getProtocol().getCommunicator().multicast(
                m, (Group)getProtocol().getContext().get(PBFT.LOCALGROUP)
            );
        }
        
    }

    /**
     * [TODO] this method must check the encriptation of the message and do the
     * procedures specified in Castro and Liskov (1999).
     * @param m
     * @return
     */

    private boolean checkPrePrepare(PBFTMessage m) {

        if(isValidPrepare(m)){
            addToBuffer(m);
            return hasQuorum();
        }
        return false;
    }
    /**
     * [TODO]check authentication, view, and sequence number
     * @param m
     * @return
     */
    private boolean isValidPrepare(PBFTMessage m) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private boolean hasQuorum() {
       int f  = ((Integer)getProtocol().getContext().get(PBFT.ALLOWABLENUMBEROFFAULTREPLICAS)).intValue();
       int quorum = 3 * f + 1;

       /**
        * [FIX] it doesn't work to parallel prepepare
        */
       return (buffer.size() >= quorum); //it's surely wrong      
    }

    private void addToBuffer(PBFTMessage m) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * [TODO]
     * @param m
     * @return
     */
    private PBFTMessage createPrepare(PBFTMessage pp) {
        Authenticator<PBFTMessage> auth =
                (Authenticator<PBFTMessage>) getProtocol().getContext().get(
                    PBFT.CLIENTMSGAUTHENTICATOR
                 );
        
        PBFTMessage p = new PBFTMessage(PBFTMessage.TYPE.PREPARE);

        p.put(PBFTMessage.VIEWFIELD, pp.get(PBFTMessage.VIEWFIELD));
        p.setSequenceNumber(pp.getSequenceNumber());
        p.put(PBFTMessage.DIGESTFIELD, pp.get(PBFTMessage.DIGESTFIELD));
        p.put(PBFTMessage.REPLICAIDFIELD, getProtocol().getLocalProcess().getID());

        p = auth.encrypt(p);
        
        return p;
    }



}
