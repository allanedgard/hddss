/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferCommitAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommitMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTCreateCommitExecutor extends PBFTServerExecutor{

    public PBFTCreateCommitExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage  p = (PBFTMessage) act.getWrapper();
        PBFTMessage batch = (PBFTMessage) p.get(PBFTMessage.REQUESTFIELD);
        PBFTMessage  c = makeCommit(p);

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "cretated commit(" + c.get(PBFTMessage.SEQUENCENUMBERFIELD) +") "
          + "with batch size " +  batch.get(PBFTMessage.BATCHSIZEFIELD) + " "
          + "at time " + ((PBFT)getProtocol()).getTimestamp() + " "
        );

       getProtocol().perform(new BufferCommitAction(c));

    }

    public PBFTMessage makeCommit(PBFTMessage p){
        if(p == null)
            return null;

        PBFTMessage c = new PBFTCommitMessage();

        c.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVECOMMIT);
        c.put(PBFTMessage.VIEWFIELD, p.get(PBFTMessage.VIEWFIELD));
        c.put(PBFTMessage.SEQUENCENUMBERFIELD, p.get(PBFTMessage.SEQUENCENUMBERFIELD));
        c.put(PBFTMessage.DIGESTFIELD, p.get(PBFTMessage.DIGESTFIELD));
        c.put(PBFTMessage.REPLICAIDFIELD, getProtocol().getLocalProcess().getID());
        c.put(PBFTMessage.REQUESTFIELD, p.get(PBFTMessage.REQUESTFIELD));

        c = (PBFTMessage) encrypt(c);
        c = (PBFTMessage) makeDisgest(c);

        return c;

    }




}
