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
public class PBFTChanging extends PBFTServerMode{

    public PBFTChanging(PBFTServerMultiModeMachine machine) {
        super(CHANGING, machine);
    }

    public void handle(PBFTRequest rq) {
       if(able()){
          getMachine().getProtocol().handle(rq);
          return;
       }
       swap();
    }

    public void handle(PBFTPrePrepare ppr) {
        MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
        queue.enqueue(ppr);
        if(!able()){
           swap();
        }
    }

    public void handle(PBFTPrepare pr) {
        MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
        queue.enqueue(pr);
        if(!able()){
           swap();
        }
    }

    public void handle(PBFTCommit cm) {
        MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
        queue.enqueue(cm);

        if(!able()){
           swap();
        }
    }

    public void handle(PBFTStatusActive sta) {
        if(!able()){
           MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
           queue.enqueue(sta);
           swap();
        }
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
    public void handle(PBFTBag bg) {
        MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
        queue.enqueue(bg);

        if(!able()){
           swap();
        }
    }

    public void execute() {
        getMachine().getProtocol().execute();
    }

    @Override
    public boolean able() {
        return changing();
    }

    @Override
    public void enter() {
        MessageQueue queue = null;

        /* First, we execute the client requests */
        queue = getMachine().getQueue(PBFTRequest.class.getName());
        while(!queue.isEmpty()){
            PBFTRequest r = (PBFTRequest) queue.remove();
            handle(r);
        }

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
    public String toString() {
        try{
            return "CHANGING (process " + getMachine().getProtocol().getLocalServerID() + ")";
        }catch(Exception except){
            return "CHANGING";
        }
    }

}
