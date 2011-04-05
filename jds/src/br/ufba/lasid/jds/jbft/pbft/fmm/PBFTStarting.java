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
public class PBFTStarting extends PBFTServerMode{

    public PBFTStarting(PBFTServerMultiModeMachine machine) {
        super(STARTING, machine);
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
        if(able()){
            MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
            if(queue != null) queue.enqueue(ppr);
            return;
        }

        swap();
    }

    public void handle(PBFTPrepare pr) {
        if(able()){
            MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
            if(queue != null) queue.enqueue(pr);
            return;
        }

        swap();
    }

    public void handle(PBFTCommit cm) {
        if(able()){
            MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
            if(queue != null) queue.enqueue(cm);
            return;
        }

        swap();
    }

    public void handle(PBFTStatusActive sta) {
        if(able()){
            MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
            if(queue != null) queue.enqueue(sta);
            return;
        }

        swap();
    }

   public void handle(PBFTStatusPending sp) {
        if(able()){
            MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
            if(queue != null) queue.enqueue(sp);
            return;
        }

        swap();
   }

    public void handle(PBFTFetch ft) {
        if(able()){
            MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
            if(queue != null) queue.enqueue(ft);
            return;
        }

        swap();
    }

    public void handle(PBFTMetaData mdt) {
        if(able()){
            MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
            if(queue != null) queue.enqueue(mdt);
            return;
        }

        swap();
    }

    public void handle(PBFTData dt) {
        if(able()){
            MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
            if(queue != null) queue.enqueue(dt);
            return;
        }

        swap();
    }

    public void handle(PBFTCheckpoint ck) {
        if(able()){
            MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
            if(queue != null) queue.enqueue(ck);
            return;
        }

        swap();
    }

    public void handle(PBFTBag bg) {
        if(able()){
            MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
            if(queue != null) queue.enqueue(bg);
            return;
        }

        swap();
    }

    public void handle(PBFTProcessingToken tk) {
        //getMachine().getProtocol().handle(tk);
    }

    public void handle(PBFTChangeView cv) {
        if(able()){
            MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
            if(queue != null) queue.enqueue(cv);
            return;
        }

        swap();
    }

    public void handle(PBFTChangeViewACK cva) {
        if(able()){
            MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
            if(queue != null) queue.enqueue(cva);
            return;
        }

        swap();
    }

    public void handle(PBFTNewView nwv) {
        if(able()){
            MessageQueue queue = getQueue(PBFTServerMessage.class.getName());
            if(queue != null) queue.enqueue(nwv);
            return;
        }

        swap();
    }

    @Override
    public boolean able() {
       return starting();
    }

    @Override
    public String toString() {
        try{
            return "STARTING (process " + getMachine().getProtocol().getLocalServerID() + ")";
        }catch(Exception except){
            return "STARTING   ";
        }
    }

}
