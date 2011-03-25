/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.fmm;

import br.ufba.lasid.jds.adapters.IAfterEventListener;
import br.ufba.lasid.jds.comm.MessageQueue;
import br.ufba.lasid.jds.jbft.pbft.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
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
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class PBFTChanging extends PBFTServerMode implements IAfterEventListener{

    public PBFTChanging(PBFTServerMultiModeMachine machine) {
        super(PBFTModes.CHANGING, machine);
        try {
            getMachine().getProtocol().addListener(this, getMachine().getProtocol().getClass().getMethod("emitChangeView"));
            getMachine().getProtocol().addListener(this, getMachine().getProtocol().getClass().getMethod("installNewView"));
        } catch (Exception ex) {
            Logger.getLogger(PBFTChanging.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    public void handle(PBFTRequest rq) {
        MessageQueue queue = getQueue(PBFTRequest.class.getName());
        if(queue != null) queue.enqueue(rq);
    }

    public void handle(PBFTPrePrepare ppr) {
        //do nothing
    }

    public void handle(PBFTPrepare pr) {
        //do nothing
    }

    public void handle(PBFTCommit cm) {
        //do nothing
    }

    public void handle(PBFTStatusActive sta) {
        //do nothing
    }

    public void handle(PBFTFetch ft) {
        //do nothing
    }

    public void handle(PBFTMetaData mdt) {
        //do nothing
    }

    public void handle(PBFTData dt) {
        //do nothing
    }

    public void handle(PBFTCheckpoint ck) {
        //do nothing
    }

    public void handle(PBFTChangeView cv) {
        getMachine().getProtocol().handle(cv);
    }

    public void handle(PBFTChangeViewACK cva) {
        getMachine().getProtocol().handle(cva);
    }

    public void handle(PBFTNewView nwv) {
        getMachine().getProtocol().handle(nwv);
    }
    public void handle(PBFTBag bg) {
        //do nothing
    }

    public void handle(PBFTProcessingToken tk) {
        getMachine().getProtocol().handle(tk);
    }

    @Override
    public boolean able() {
        return true;
    }

    @Override
    public void enter() {
        MessageQueue queue = null;

        queue = getQueue(PBFTChangeView.class.getName());
        while(!queue.isEmpty()){
            PBFTChangeView cv = (PBFTChangeView) queue.remove();
            handle(cv);
        }

        queue = getQueue(PBFTChangeViewACK.class.getName());
        while(!queue.isEmpty()){
            PBFTChangeViewACK cva = (PBFTChangeViewACK) queue.remove();
            handle(cva);
        }

        queue = getQueue(PBFTNewView.class.getName());
        while(!queue.isEmpty()){
            PBFTNewView nwv = (PBFTNewView) queue.remove();
            handle(nwv);
        }

    }

    public void after(Method m, Object source, Object result, Object... args) {
        if(source instanceof IPBFTServer){
            if(m.getName().equals("emitChangeView")){
                getMachine().switchTo(PBFTModes.CHANGING);
            }
            if(m.getName().equals("installNewView")){
                getMachine().switchTo(PBFTModes.RUNNING);
            }
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
