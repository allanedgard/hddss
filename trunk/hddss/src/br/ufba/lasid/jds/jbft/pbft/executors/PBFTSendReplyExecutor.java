/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.executors.ClientServerSendReplyExecutor;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.jbft.pbft.PBFT;

/**
 *
 * @author aliriosa
 */
public class PBFTSendReplyExecutor extends ClientServerSendReplyExecutor{

    public PBFTSendReplyExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = (PBFTMessage) act.getMessage();
        
        makeReply(m);
        
    }

    private PBFTMessage makeReply(PBFTMessage m){
        m.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVEREPLY);

        Process destin = (Process)m.get(PBFTMessage.SOURCEFIELD);

        m.put(PBFTMessage.DESTINATIONFIELD, destin);
        m.put(PBFTMessage.SOURCEFIELD, getProtocol().getLocalProcess());
        m.put(PBFTMessage.VIEWFIELD, ((PBFT)getProtocol()).getCurrentView());
        m.put(PBFTMessage.REPLICAIDFIELD, getProtocol().getLocalProcess().getID());

        ((PBFT)getProtocol()).getDebugger().debug(
            "[PBFTSendReplyExecutor.execute] reply " + m
          + " was sending by server(p" + getProtocol().getLocalProcess().getID() + ") "
          + " to client(p" + destin.getID() + ")"
          + " at time " + ((PBFT)getProtocol()).getTimestamp()
         );

        getProtocol().getCommunicator().unicast(m, destin);

        return m;
    }

}
