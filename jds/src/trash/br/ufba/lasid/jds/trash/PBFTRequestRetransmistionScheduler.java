/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.trash;

import trash.br.ufba.lasid.jds.jbft.pbft.PBFT2;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.RetransmiteRequestAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.IScheduler;
import trash.br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class PBFTRequestRetransmistionScheduler extends PBFTRequestScheduler{
    
    public PBFTRequestRetransmistionScheduler(PBFT2 protocol, IScheduler scheduler){
        super(protocol, scheduler);
        REQUESTID = "__PBFTRequestRetransmistionSchedulerREQUESTID";
    }

    public String getTAG(){
        return PBFT2.CLIENTRETRANSMISSIONTIMEOUT;
    }

    @Override
    public void addToRequestBuffer(PBFTMessage request){
        getRequestBuffer().add(request);
    }

    public void makePerform(Wrapper w){
        ((PBFT2)getProtocol()).perform(new RetransmiteRequestAction(w));
    }

    public boolean cancel(PBFTMessage m) {
//        m.put(REQUESTID, new Long(-1));
        return true;
    }

    public boolean cancelAll() {
       // throw new UnsupportedOperationException("Not supported yet.");
        return true;
    }
   
}
