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
        
        m.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVEREPLY);

        Process destin = (Process)m.get(PBFTMessage.SOURCEFIELD);

        m.put(PBFTMessage.DESTINATIONFIELD, destin);
        m.put(PBFTMessage.SOURCEFIELD, getProtocol().getLocalProcess());
        getProtocol().getCommunicator().unicast(m, destin);
        
    }


}
