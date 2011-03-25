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
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;

/**
 *
 * @author aliriosa
 */
public class PBFTRunning extends PBFTServerMode{
    
    public PBFTRunning(PBFTServerMultiModeMachine machine) {
        super(PBFTModes.RUNNING, machine);
    }

    public void handle(PBFTRequest rq) {
        if(able()){
            getMachine().getProtocol().handle(rq);
            return;
        }

        MessageQueue queue = getQueue(PBFTRequest.class.getName());
        if(queue != null) queue.enqueue(rq);
        getMachine().switchTo(PBFTModes.OVERLOADED);
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
        MessageQueue queue = getQueue(PBFTChangeView.class.getName());
        queue.enqueue(cv);
        getMachine().switchTo(PBFTModes.CHANGING);
    }

    public void handle(PBFTChangeViewACK cva) {
        MessageQueue queue = getQueue(PBFTChangeView.class.getName());
        queue.enqueue(cva);
        getMachine().switchTo(PBFTModes.CHANGING);
    }

    public void handle(PBFTNewView nwv) {
        MessageQueue queue = getQueue(PBFTChangeView.class.getName());
        queue.enqueue(nwv);
        getMachine().switchTo(PBFTModes.CHANGING);
    }

    @Override
    public boolean able() {
        long swsize = getMachine().getProtocol().getSlidingWindowSize();
        long currPP = getCurrentPrePrepareSEQ();
        long currEX = getCurrentExecuteSEQ();
        return (currEX + swsize >= currPP);
    }

    @Override
    public void enter() {
        MessageQueue queue = getQueue(PBFTRequest.class.getName());
        
        while(!queue.isEmpty()){
            PBFTRequest r = (PBFTRequest) queue.remove();
            handle(r);
        }
    }

    @Override
    public String toString() {
        try{
            return "RUNNING (process " + getMachine().getProtocol().getLocalServerID() + ")";
        }catch(Exception except){
            return "RUNNING";
        }
    }

}
