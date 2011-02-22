/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.trash;

import trash.br.ufba.lasid.jds.jbft.pbft.PBFT2;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.BatchRequestAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import trash.br.ufba.lasid.jds.util.Buffer;
import br.ufba.lasid.jds.util.IScheduler;
import trash.br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class PBFTBatchingTimeoutScheduler extends PBFTRequestScheduler{
    Buffer batchBuffer = new Buffer();
    public PBFTBatchingTimeoutScheduler(PBFT2 protocol, IScheduler scheduler) {
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
        return PBFT2.BATCHINGTIMEOUT;
    }

    @Override
    public void makePerform(Wrapper w){

        PBFTMessage m = (PBFTMessage) w;
        
//        m.put(getTAG(), new Long(-1));

        getRequestBuffer().clear();
        System.out.println(
              "server [p" + getProtocol().getLocalProcess().getID()+"] expires "
            + "batch timeout at time " + ((PBFT2)getProtocol()).getTimestamp()
        );

        ((PBFT2)getProtocol()).perform(new BatchRequestAction());

    }

    public boolean cancelAll() {
        //throw new UnsupportedOperationException("Not supported yet.");
        return true;
    }

}
