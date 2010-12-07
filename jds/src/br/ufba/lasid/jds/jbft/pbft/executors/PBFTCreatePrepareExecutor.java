/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferPrepareAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepareMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTCreatePrepareExecutor extends PBFTServerExecutor{

    public PBFTCreatePrepareExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage  pp = (PBFTMessage) act.getWrapper();
        PBFTMessage batch = (PBFTMessage) pp.get(PBFTMessage.REQUESTFIELD);
        PBFTMessage  p = makePrepare(pp);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "cretated prepare(" + p.get(PBFTMessage.SEQUENCENUMBERFIELD) +") "
          + "with batch size " +  batch.get(PBFTMessage.BATCHSIZEFIELD) + " "
          + "at time " + ((PBFT)getProtocol()).getTimestamp() + " "
        );

        getProtocol().perform(new BufferPrepareAction(p));
        
    }

    public PBFTMessage makePrepare(PBFTMessage pp){
        if(pp == null)
            return null;

        PBFTMessage p = new PBFTPrepareMessage();
        
        p.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVEPREPARE);
        p.put(PBFTMessage.VIEWFIELD, pp.get(PBFTMessage.VIEWFIELD));
        p.put(PBFTMessage.SEQUENCENUMBERFIELD, pp.get(PBFTMessage.SEQUENCENUMBERFIELD));
        p.put(PBFTMessage.DIGESTFIELD, pp.get(PBFTMessage.DIGESTFIELD));
        p.put(PBFTMessage.REPLICAIDFIELD, getProtocol().getLocalProcess().getID());
        p.put(PBFTMessage.REQUESTFIELD, pp.get(PBFTMessage.REQUESTFIELD));

        p = (PBFTMessage)encrypt(p);
        p = (PBFTMessage) makeDisgest(p);

        return p;

    }




}
