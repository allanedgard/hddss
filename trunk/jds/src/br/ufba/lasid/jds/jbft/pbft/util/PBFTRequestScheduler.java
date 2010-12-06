/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Buffer;
import br.ufba.lasid.jds.util.Scheduler;
import br.ufba.lasid.jds.util.Task;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *PBFTRequestScheduler
 * @author aliriosa
 */

public abstract class PBFTRequestScheduler implements Scheduler, Task{

    protected String REQUESTID = "__PBFTRequestSchedulerREQUESTID";
    protected PBFT protocol = null;
    Scheduler scheduler = null;

    public PBFTRequestScheduler(PBFT protocol, Scheduler scheduler){
        this.protocol = protocol;
        this.scheduler = scheduler;
    }

    public PBFT getProtocol() {
        return protocol;
    }

    public void setProtocol(PBFT protocol) {
        this.protocol = protocol;
    }

    public void schedule(PBFTMessage request, long time){
        request.put(getTAG(), time);
        request.put(REQUESTID, getRequestID(request));

        addToRequestBuffer(request);

        schedule(this, time);
    }

    public long getCurrentDefinedTimeout(){
        return ((Long)getProtocol().getContext().get(getTAG())).longValue();
    }
    public abstract String getTAG();
    
    public long getRequestTimeout(PBFTMessage request){
        return ((Long)(request.get(getTAG()))).longValue();
    }

    public void addToRequestBuffer(PBFTMessage request){
        getRequestBuffer().add(request);
    }

    public synchronized Buffer getRequestBuffer(){
        return ((PBFT)getProtocol()).getRequestBuffer();
    }

    public String getRequestID(PBFTMessage request){

        String cid = getRequestClientID(request);
        long timestamp = getRequestTimestamp(request);

        return cid + "." + timestamp;
    }

    public String getRequestClientID(PBFTMessage request){
        return ((br.ufba.lasid.jds.Process)(request.get(PBFTMessage.CLIENTFIELD))).getID().toString();
    }

    public long getRequestTimestamp(PBFTMessage request){
        return ((Long)request.get(PBFTMessage.TIMESTAMPFIELD)).longValue();
    }
    public void schedule(Task task, long time) {
        scheduler.schedule(task, time);
    }

    public void cancel(Task task) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public synchronized void runMe() {

        Buffer buffer = new Buffer();
        buffer.addAll(getRequestBuffer());

        for(Object item : buffer){

            PBFTMessage request = (PBFTMessage)item;

            long timeout = getRequestTimeout(request);

            if(/* timeout >=0 && */ timeout == getCurrentTime()){
                makePerform((Wrapper)request);
            }
        }
    }

    public long getCurrentTime(){
        return ((PBFT)getProtocol()).getTimestamp();
    }

    public abstract void makePerform(Wrapper w);
}
