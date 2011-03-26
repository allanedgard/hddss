/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.Quorum;
import trash.br.ufba.lasid.jds.comm.QuorumTable;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.cs.IClient;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTTimeoutDetector;
import br.ufba.lasid.jds.util.IPayload;
import br.ufba.lasid.jds.util.ISchedule;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;
import br.ufba.lasid.jds.util.JDSUtility;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class PBFTClient extends PBFT implements IPBFTClient{


    protected  Buffer applicationBox = BufferUtils.blockingBuffer(new UnboundedFifoBuffer());
    protected Hashtable<Long, PBFTRequest> rtable = new Hashtable<Long, PBFTRequest>();
    protected ISchedule timer;

    public Buffer getApplicationBox(){
        return applicationBox;
    }
    
    protected  IClient client;

    public IClient getClient() {
        return client;
    }

    public void setClient(IClient client) {
        this.client = client;
    }

    
    protected  long retransmissionTimeout = Long.valueOf(120000);
    /**
     * Get the request retransmission timeout.
     * @return - the timeout.
     */
    public long getRetransmissionTimeout() {
        return retransmissionTimeout;
    }

    /**
     * Define the request retransmission timeout.
     * @param retransmissionTimeout
     */
    public void setRetransmissionTimeout(long retransmissionTimeout) {
        this.retransmissionTimeout = retransmissionTimeout;
    }

    protected  QuorumTable qtable = new QuorumTable();

    /**
     * Perform an asynchronous call of the remote pbft service.
     * @param payload - the operation payload.
     */

    public IPayload syncCall(IPayload payload){
            PBFTRequest r = createRequestMessage(payload);
            r.setSynch(true);
            rtable.put(r.getTimestamp(), r);
            emit(r);
            return (IPayload) getApplicationBox().remove();
    }

    /**
     * Create a new PBFT' IClient request message.
     * @param payload -- the application payload.
     * @return -- a new pbft request message.
     */
    protected PBFTRequest createRequestMessage(IPayload payload){

        PBFTRequest r = new PBFTRequest(payload, getClockValue(), getLocalProcessID());
        r.setSent(false);

        return r;

    }

    /**
     * do a request to server group and doSchedule the request retransmission.
     * @param request -- the client request.
     */
    protected void emit(PBFTRequest request){

        if(request!= null && !request.wasSent()){

            try{

                SignedMessage m = getAuthenticator().encrypt(request);
                
                PDU pdu = new PDU();

                pdu.setSource(getLocalProcess());
                pdu.setDestination(getRemoteProcess());
                pdu.setPayload(m);                
                getCommunicator().multicast(pdu, (IGroup)getRemoteProcess());
                JDSUtility.debug(
                  "[PBFTClient] c" + getLocalProcessID() + " sent " + request + " " +
                  "to " + getRemoteProcess() + " at time " + getClockValue() + "."
                );

                request.setSent(true);

                doSchedule(request);
                
            }catch(Exception ex){
                Logger.getLogger(PBFTClient.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }
    }
    

    public Quorum getQuorum(PBFTReply r){

        Quorum q = null;

        if(r!= null && r.getTimestamp() != null){

            q = qtable.get(r.getTimestamp());

            int f = getServiceBFTResilience();

            if(q == null){
                q = new Quorum(f + 1);
                qtable.put(r.getTimestamp(), q);
            }

        }
        
        return q;

        
    }

    public boolean updateState(PBFTReply r){

        if(r != null){
            
            Quorum quorum = getQuorum(r);

            if(quorum != null){

                for(IMessage m :  quorum){

                    PBFTReply r1 = (PBFTReply)m;
                    
                    if(!(r1.isSameRound(r) && !r1.isSameServer(r))){
                        return false;
                    }
                    
                }

                quorum.add(r);

                JDSUtility.debug(
                   "[PBFTClient:updateState(reply)] c"  + getLocalProcessID() + ", " + 
                   "at time " + getClockValue() + ", updated a entry in its log " +
                   "for the received " + r + "."
                );

                if(quorum.complete()){
                    getTimer().cancel();
                    //revokeSchedule(r.getTimestamp());
                    rtable.remove(r.getTimestamp());
                    qtable.remove(r.getTimestamp());
                    quorum.clear();
                    
                    return true;
                }

            }
        }

        return false;
    }
    protected ISchedule getTimer(){
        if(timer == null){
            PBFTTimeoutDetector ttask = new PBFTTimeoutDetector() {
                @Override
                public void onTimeout() {
                    PBFTRequest r = (PBFTRequest) this.get("REQUEST");
                    if(r != null){
                        emit(r);
                    }
                    //r.setSent(shutdown);
                }
            };
            timer = getScheduler().newSchedule();
            timer.setTask(ttask);
        }

        return timer;
    }

    /*
     * doSchedule the retransmition of a request.
     * @param request
     */
    public void doSchedule(PBFTRequest request){
        PBFTTimeoutDetector ttask = (PBFTTimeoutDetector)getTimer().getTask();

        long now = getClockValue();
        long timeout = getRetransmissionTimeout();
        long timestamp = now + timeout;

        ttask.put("REQUEST", request);
        
        getTimer().schedule(timestamp);

        

        getTimer().schedule(timestamp);
        
    }

    public void handle(PBFTReply reply){
        
        if(canProceed(reply)){

            if(updateState(reply)){

               getApplicationBox().add(reply.getPayload());

            }//end if updateState(reply)

        }//end if wasAcceptedAsValidReply (reply)
        
    }

    public boolean canProceed(PBFTReply reply){
        if(!isAReplyForMe(reply.getClientID())){
            return false;
        }

        if(!hasARelatedRequest(reply)){
            JDSUtility.debug(
               "[PBFTClient:canProceed(reply)] c" + getLocalProcessID() + ", " +
               "at time " + getClockValue() + ", discarded " + reply + " because there isn't " +
               " a related request."
             );
            return false;
        }
        return true;
    }

    public boolean hasARelatedRequest(PBFTReply r){
        PBFTRequest request = rtable.get(r.getTimestamp());
        return (request != null && r.getClientID().equals(r.getClientID()));
    }

    public boolean isAReplyForMe(Object clientID){
        return getLocalProcessID().equals(clientID);
    }

//    public void revokeSchedule(PBFTRequest r){
//        revokeSchedule(r.getTimestamp());
//    }
//
//    public void revokeSchedule(long timestamp){
//
//        PBFTTimeoutDetector timeoutTask =
//            (PBFTTimeoutDetector)getTaskTable(PBFT.REQUESTTASKS).get(timestamp);
//
//        getScheduler().cancel(timeoutTask);
//
//        getTaskTable(PBFT.REQUESTTASKS).remove(timestamp);
//
//    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    public void asyncCall(IPayload payload) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void accept(IPayload payload) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}