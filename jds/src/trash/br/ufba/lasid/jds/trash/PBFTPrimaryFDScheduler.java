/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.trash;

import trash.br.ufba.lasid.jds.jbft.pbft.PBFT2;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.DetectPrimaryFailureAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.IScheduler;
import trash.br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class PBFTPrimaryFDScheduler extends PBFTRequestScheduler{

    public PBFTPrimaryFDScheduler(PBFT2 protocol, IScheduler scheduler) {
        super(protocol, scheduler);
        REQUESTID = "__PBFTPrimaryFDSchedulerREQUESTID";
    }

    @Override
    public void addToRequestBuffer(PBFTMessage request) {
        //do nothing, it's was done before
    }

    public String getTAG(){
        return PBFT2.PRIMARYFAULTTIMEOUT;
    }

    @Override
    public void makePerform(Wrapper w){
        ((PBFT2)getProtocol()).perform(new DetectPrimaryFailureAction(w));
    }

    public boolean cancelAll() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return true;
    }

    
}
