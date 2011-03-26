/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.trash;

import br.ufba.lasid.jds.util.ISchedule;
import br.ufba.lasid.jds.util.ITask;
import trash.br.ufba.lasid.jds.jbft.pbft.PBFT2;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.RetransmiteChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import trash.br.ufba.lasid.jds.util.Buffer;
import br.ufba.lasid.jds.util.IScheduler;
import trash.br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class PBFTViewChangeRetransmittionScheduler extends PBFTRequestScheduler{

    Buffer buffer = new Buffer();
    
    public PBFTViewChangeRetransmittionScheduler(PBFT2 protocol, IScheduler scheduler) {
        super(protocol, scheduler);
        REQUESTID = "__PBFTViewChangeRetransmittionSchedulerREQUESTID";
    }

    @Override
    public String getTAG(){
        return PBFT2.VIEWCHANGERETRANSMITIONTIMEOUT;
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
        ((PBFT2)getProtocol()).perform(new RetransmiteChangeViewAction());
    }

    public boolean cancelAll() {
        buffer.clear();
        return true;
    }

    public ISchedule newSchedule() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ISchedule schedule(ITask task, long time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void cancel(ISchedule schedule) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}
