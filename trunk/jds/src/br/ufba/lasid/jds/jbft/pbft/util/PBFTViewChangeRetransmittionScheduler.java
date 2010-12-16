/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.RetransmiteChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Buffer;
import br.ufba.lasid.jds.util.Scheduler;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class PBFTViewChangeRetransmittionScheduler extends PBFTRequestScheduler{

    Buffer buffer = new Buffer();
    
    public PBFTViewChangeRetransmittionScheduler(PBFT protocol, Scheduler scheduler) {
        super(protocol, scheduler);
        REQUESTID = "__PBFTViewChangeRetransmittionSchedulerREQUESTID";
    }

    @Override
    public String getTAG(){
        return PBFT.VIEWCHANGERETRANSMITIONTIMEOUT;
    }

    @Override
    public void addToRequestBuffer(PBFTMessage request) {
        buffer.add(request);
    }


    @Override
    public synchronized Buffer getRequestBuffer(){
        return buffer;
    }

    @Override
    public void makePerform(Wrapper w) {
        buffer.clear();
        ((PBFT)getProtocol()).perform(new RetransmiteChangeViewAction());
    }

    public void cancelAll() {
        buffer.clear();
    }



}
