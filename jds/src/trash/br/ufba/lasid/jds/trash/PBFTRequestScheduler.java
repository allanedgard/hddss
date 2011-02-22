/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.trash;

import trash.br.ufba.lasid.jds.jbft.pbft.PBFT2;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import trash.br.ufba.lasid.jds.util.Buffer;
import br.ufba.lasid.jds.util.IScheduler;
import br.ufba.lasid.jds.util.ITask;
import trash.br.ufba.lasid.jds.util.Wrapper;

/**
 *PBFTRequestScheduler
 * @author aliriosa
 */

public abstract class PBFTRequestScheduler implements IScheduler, ITask{

    protected String REQUESTID = "__PBFTRequestSchedulerREQUESTID";
    protected PBFT2 protocol = null;
    IScheduler scheduler = null;

    public PBFTRequestScheduler(PBFT2 protocol, IScheduler scheduler){
        this.protocol = protocol;
        this.scheduler = scheduler;
    }

    public PBFT2 getProtocol() {
        return protocol;
    }

    public void setProtocol(PBFT2 protocol) {
        this.protocol = protocol;
    }

    public void schedule(PBFTMessage request, long time){
//        request.put(getTAG(), time);
  //      request.put(REQUESTID, getRequestID(request));

        addToRequestBuffer(request);

        schedule(this, time);
    }

    public long getCurrentDefinedTimeout(){
        return ((Long)getProtocol().getContext().get(getTAG())).longValue();
    }
    public abstract String getTAG();
    
    public long getRequestTimeout(PBFTMessage request){
        Long timeout = null;//((Long)(request.get(getTAG())));
        if(timeout != null)
            return timeout.longValue();

        return -1L;
    }

    public void addToRequestBuffer(PBFTMessage request){
        getRequestBuffer().add(request);
    }

    public synchronized Buffer getRequestBuffer(){
        return ((PBFT2)getProtocol()).getRequestBuffer();
    }

    public String getRequestID(PBFTMessage request){

        String cid = getRequestClientID(request);
        long timestamp = getRequestTimestamp(request);

        return cid + "." + timestamp;
    }

    public String getRequestClientID(PBFTMessage request){
        return null;//((br.ufba.lasid.jds.IProcess)(request.get(PBFTMessage.CLIENTIDFIELD))).getID().toString();
    }

    public long getRequestTimestamp(PBFTMessage request){
        return -1L;//((Long)request.get(PBFTMessage.TIMESTAMPFIELD)).longValue();
    }
    public void schedule(ITask task, long time) {
        scheduler.schedule(task, time);
    }

    public boolean cancel(ITask task) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public synchronized void runMe() {

        Buffer buffer = new Buffer();
        buffer.addAll(getRequestBuffer());

        for(Object item : buffer){

            PBFTMessage request = (PBFTMessage)item;

            if(request != null){
                long timeout = getRequestTimeout(request);

                if(/* timeout >=0 && */ timeout == getCurrentTime()){
                    makePerform((Wrapper)request);
                }
            }
        }
    }

    public long getCurrentTime(){
        return 0;
        //return ((PBFT2)getProtocol()).newTimestamp();
    }

    public abstract void makePerform(Wrapper w);
}
