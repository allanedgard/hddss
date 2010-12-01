/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.RetransmissionAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Scheduler;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class PBFTRequestRetransmistionScheduler extends PBFTRequestScheduler{
    
    public PBFTRequestRetransmistionScheduler(PBFT protocol, Scheduler scheduler){
        super(protocol, scheduler);
        REQUESTID = "__PBFTRequestRetransmistionSchedulerREQUESTID";
    }

    public String getTAG(){
        return PBFT.CLIENTRETRANSMISSIONTIMEOUT;
    }

    @Override
    public void addToRequestBuffer(PBFTMessage request){
        getRequestBuffer().add(request);
    }

    public void makePerform(Wrapper w){
        ((PBFT)getProtocol()).perform(new RetransmissionAction(w));
    }

    public void cancel(PBFTMessage m) {
        m.put(REQUESTID, new Long(-1));
    }
   
}