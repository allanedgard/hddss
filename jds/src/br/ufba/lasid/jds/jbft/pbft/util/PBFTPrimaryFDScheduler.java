/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.ChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.actions.DetectPrimaryFailureAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Scheduler;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class PBFTPrimaryFDScheduler extends PBFTRequestScheduler{

    public PBFTPrimaryFDScheduler(PBFT protocol, Scheduler scheduler) {
        super(protocol, scheduler);
        REQUESTID = "__PBFTPrimaryFDSchedulerREQUESTID";
    }

    @Override
    public void addToRequestBuffer(PBFTMessage request) {
        //do nothing, it's was done before
    }

    public String getTAG(){
        return PBFT.PRIMARYFAULTTIMEOUT;
    }

    @Override
    public void makePerform(Wrapper w){
        ((PBFT)getProtocol()).perform(new DetectPrimaryFailureAction(w));
    }

    
}
