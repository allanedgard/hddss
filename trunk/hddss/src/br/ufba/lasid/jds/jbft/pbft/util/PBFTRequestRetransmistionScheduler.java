/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.RetransmissionAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Buffer;
import br.ufba.lasid.jds.util.Clock;
import br.ufba.lasid.jds.util.Scheduler;
import br.ufba.lasid.jds.util.Task;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class PBFTRequestRetransmistionScheduler implements Scheduler, Task{
    
    public static String REQUESTID = "__RequestRetransmistionSchedulerREQUESTID";
    protected PBFT protocol = null;
    Scheduler scheduler = null;

    public PBFTRequestRetransmistionScheduler(PBFT protocol, Scheduler scheduler){
        this.protocol = protocol;
        this.scheduler = scheduler;
    }

    public PBFT getProtocol() {
        return protocol;
    }

    public void setProtocol(PBFT protocol) {
        this.protocol = protocol;
    }
    
    public void schedule(PBFTMessage request){

        request.put(REQUESTID, getRequestID(request));
        
        addToRequestBuffer(request);

        schedule(this, getClientRequestTimeout());
        
    }

    public long getClientRequestTimeout(){
        return ((Long)getProtocol().getContext().get(PBFT.CLIENTRETRANSMISSIONTIMEOUT)).longValue();
    }

    public long getRequestTimeout(PBFTMessage request){
        return ((Long)(request.get(PBFT.CLIENTRETRANSMISSIONTIMEOUT))).longValue();
    }
    public void addToRequestBuffer(PBFTMessage request){
        getRequestBuffer().add(request);
    }

    public Buffer getRequestBuffer(){
        return ((Buffer)(getProtocol().getContext().get(PBFT.REQUESTBUFFER)));
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

    public void runMe() {

        Buffer buffer = getRequestBuffer();

        for(Object item : buffer){

            PBFTMessage request = (PBFTMessage)item;
            
            long timeout = getRequestTimeout(request);

            if(timeout >=0 && timeout < getCurrentTime()){
                ((PBFT)getProtocol()).perform(new RetransmissionAction((Wrapper)request));
            }
        }

    }

    public long getCurrentTime(){
        return ((Clock)getProtocol().getContext().get(PBFT.CLOCKSYSTEM)).value();
    }
   
}
