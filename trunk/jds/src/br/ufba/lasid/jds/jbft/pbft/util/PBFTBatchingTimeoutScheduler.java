/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.BatchRequestAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Buffer;
import br.ufba.lasid.jds.util.Scheduler;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class PBFTBatchingTimeoutScheduler extends PBFTRequestScheduler{
    Buffer batchBuffer = new Buffer();    
    public PBFTBatchingTimeoutScheduler(PBFT protocol, Scheduler scheduler) {
        super(protocol, scheduler);
        REQUESTID = "__PBFTBatchingTimeoutScheduler";
    }

    @Override
    public void addToRequestBuffer(PBFTMessage request) {
        if(!batchBuffer.contains(request))
            batchBuffer.add(request);
    }

    @Override
    public synchronized Buffer getRequestBuffer() {
        return batchBuffer;
    }
    
    public String getTAG(){
        return PBFT.BATCHINGTIMEOUT;
    }

    @Override
    public void makePerform(Wrapper w){

        PBFTMessage m = (PBFTMessage) w;
        
        m.put(getTAG(), new Long(-1));

        getRequestBuffer().clear();
        System.out.println(
              "server [p" + getProtocol().getLocalProcess().getID()+"] expires "
            + "batch timeout at time " + ((PBFT)getProtocol()).getTimestamp()
        );

        ((PBFT)getProtocol()).perform(new BatchRequestAction());

    }

}
