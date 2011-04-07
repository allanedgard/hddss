/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.fmm;

import br.ufba.lasid.jds.comm.MessageQueue;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBag;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewACK;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTFetch;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMetaData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTNewView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTProcessingToken;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTServerMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusPending;

/**
 *
 * @author aliriosa
 */
public class PBFTOverloaded extends PBFTServerMode{

    public PBFTOverloaded(PBFTServerMultiModeMachine machine) {
        super(OVERLOADED, machine);
    }

    public void handle(PBFTRequest rq) {
        if(able()){
            MessageQueue queue = getQueue(PBFTRequest.class.getName());
            if(queue != null) queue.enqueue(rq);
            return;
        }
        swap();        
    }

    public void handle(PBFTPrePrepare ppr) {
        getMachine().getProtocol().handle(ppr);
    }

    public void handle(PBFTPrepare pr) {
        getMachine().getProtocol().handle(pr);
    }

    public void handle(PBFTCommit cm) {
        getMachine().getProtocol().handle(cm);
    }

    public void handle(PBFTStatusActive sta) {
        getMachine().getProtocol().handle(sta);
    }

   public void handle(PBFTStatusPending sp) {
      getMachine().getProtocol().handle(sp);
   }

    public void handle(PBFTFetch ft) {
        getMachine().getProtocol().handle(ft);
    }

    public void handle(PBFTMetaData mdt) {
        getMachine().getProtocol().handle(mdt);
    }

    public void handle(PBFTData dt) {
        getMachine().getProtocol().handle(dt);
    }

    public void handle(PBFTCheckpoint ck) {
        getMachine().getProtocol().handle(ck);
    }

    public void handle(PBFTBag bg) {
        getMachine().getProtocol().handle(bg);
    }

    public void handle(PBFTProcessingToken tk) {
        getMachine().getProtocol().handle(tk);
    }

    public void handle(PBFTChangeView cv) {
        if(able()){
            getMachine().getProtocol().handle(cv);
            return;
        }
        MessageQueue queue = getQueue(PBFTChangeView.class.getName());
        queue.enqueue(cv);
        swap();
    }

    public void handle(PBFTChangeViewACK cva) {
        if(able()){
            getMachine().getProtocol().handle(cva);
            return;
        }

        MessageQueue queue = getQueue(PBFTChangeViewACK.class.getName());
        queue.enqueue(cva);
        swap();
    }

    public void handle(PBFTNewView nwv) {
        if(able()){
            getMachine().getProtocol().handle(nwv);
            return;
        }

        MessageQueue queue = getQueue(PBFTNewView.class.getName());
        queue.enqueue(nwv);
        swap();
    }

    @Override
    public void enter() {
        MessageQueue queue = null;

        /* First, we execute the messeges related to change view procedure */
        queue = getMachine().getQueue(PBFTChangeView.class.getName());
        while(!queue.isEmpty()){
            PBFTChangeView cv = (PBFTChangeView) queue.remove();
            handle(cv);
        }

        queue = getMachine().getQueue(PBFTChangeViewACK.class.getName());
        while(!queue.isEmpty()){
            PBFTChangeViewACK cva = (PBFTChangeViewACK) queue.remove();
            handle(cva);
        }

        queue = getMachine().getQueue(PBFTNewView.class.getName());
        while(!queue.isEmpty()){
            PBFTNewView nwv = (PBFTNewView) queue.remove();
            handle(nwv);
        }

        /* Second, we execute the messeges related to normal working of the pbft */
        queue = getMachine().getQueue(PBFTServerMessage.class.getName());
        while(!queue.isEmpty()){
            PBFTServerMessage svr = (PBFTServerMessage) queue.remove();
            handle(svr);
        }

    }



    @Override
    public boolean able() {
       return overloaded();
    }

    @Override
    public String toString() {
        try{
            return "OVERLOADED (process " + getMachine().getProtocol().getLocalServerID() + ")";
        }catch(Exception except){
            return "OVERLOADED";
        }
    }
}
