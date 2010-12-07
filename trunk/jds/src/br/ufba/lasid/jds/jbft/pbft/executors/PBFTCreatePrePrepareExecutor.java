/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferPrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepareMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTCreatePrePrepareExecutor extends PBFTServerExecutor{

    public PBFTCreatePrePrepareExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage  request = (PBFTMessage) act.getWrapper();

        PBFTMessage  pp = makePrePrepare(request);
        
        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "cretated preprepare(" + pp.get(PBFTMessage.SEQUENCENUMBERFIELD) +") "
          + "with batch size " + request.get(PBFTMessage.BATCHSIZEFIELD) + " "
          + "at time " + ((PBFT)getProtocol()).getTimestamp() + " "
        );

        getProtocol().perform(new BufferPrePrepareAction(pp));
    }

    public PBFTMessage makePrePrepare(PBFTMessage request){
        if(request == null)
            return null;

        PBFTMessage pp = new PBFTPrePrepareMessage();

        pp.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVEPREPREPARE);
        pp.put(PBFTMessage.REQUESTFIELD, request);
        pp.put(PBFTMessage.VIEWFIELD, ((PBFT)getProtocol()).getCurrentView());
        pp.put(PBFTMessage.SEQUENCENUMBERFIELD, PBFT.newSequenceNumber());
        pp.put(PBFTMessage.SOURCEFIELD, getProtocol().getLocalProcess());
        pp.put(PBFTMessage.REPLICAIDFIELD, getProtocol().getLocalProcess().getID());

        request.put(PBFTMessage.SEQUENCENUMBERFIELD, pp.SEQUENCENUMBERFIELD);
        
        pp = (PBFTMessage)encrypt(pp);
        pp = (PBFTMessage)makeDisgest(pp);

        return pp;

    }



}
