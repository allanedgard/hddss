/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.client;

import br.ufba.lasid.jds.BaseProcess;
import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.cs.IClient;
import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.voting.Quorum;
import br.ufba.lasid.jds.decision.voting.Quorumtable;
import br.ufba.lasid.jds.decision.voting.SoftQuorum;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.group.decision.Vote;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.client.decision.ReplySubject;
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

    //protected  QuorumTable qtable = new QuorumTable();
    protected Quorumtable<Long> qtable = new Quorumtable<Long>();

    /**
     * Perform an asynchronous call of the remote pbft service.
     * @param payload - the operation payload.
     */

    public IPayload syncCall(IPayload payload){
            PBFTRequest r = createRequestMessage(payload);

            r.setSize(payload.getSizeInBytes());
            
            r.setSynch(true);

            emit(r);
            rtable.put(r.getTimestamp(), r);
            
            return null; //(IPayload) getApplicationBox().remove();
    }

    /**
     * Create a new PBFT' IClient request message.
     * @param payload -- the application payload.
     * @return -- a new pbft request message.
     */
    protected PBFTRequest createRequestMessage(IPayload payload){

        PBFTRequest r = new PBFTRequest(payload, getClockValue(), getLocalProcessID());
//        r.setSent(false);

        return r;

    }

    /**
     * do a request to server group and schedule the request retransmission.
     * @param request -- the client request.
     */
    protected void emit(PBFTRequest request){
        String re = "";
        if(request!= null){
           if(rtable.containsKey(request.getTimestamp())){
               re = "re-";
           }

            try{

                SignedMessage m = getAuthenticator().encrypt(request);
                
                PDU pdu = new PDU();

                pdu.setSource(getLocalProcess());
                pdu.setDestination(getRemoteProcess());
                pdu.setPayload(m);                
                getCommunicator().multicast(pdu, (IGroup)getRemoteProcess());
                JDSUtility.debug(
                  "[PBFTClient] c" + getLocalProcessID() + " " + re + "sent " + request + " " +
                  "to " + getRemoteProcess() + " at time " + getClockValue() + "."
                );

                schedule(request);
                
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
                q = new SoftQuorum(f + 1);
                qtable.put(r.getTimestamp(), q);
            }

        }
        
        return q;

        
    }

    public ISubject getDecision(PBFTReply r){
        boolean complete = false;
        if(r != null){

            Quorum q = getQuorum(r);

            if(q != null){
               ISubject result = q.decide();

               if(result != null){
                  complete = true;
               }

               q.add(new Vote(r.getReplicaID(), new ReplySubject(r)));

               if(result == null){
                  result = q.decide();
               }

               if(!complete && result != null){
                  getTimer().cancel();
                  rtable.remove(r.getTimestamp());
                  qtable.remove(r.getTimestamp());
                  return result;
               }
            }
        }
        
        return null;
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
     * schedule the retransmition of a request.
     * @param request
     */
    public void schedule(PBFTRequest request){
       
        PBFTTimeoutDetector ttask = (PBFTTimeoutDetector)getTimer().getTask();

        long current = getClockValue();
        long timeout = getRetransmissionTimeout();
        
        long timestamp = current + timeout;

        ttask.put("REQUEST", request);
        
        getTimer().schedule(timestamp);
        
    }

    public void handle(PBFTReply reply){
        
        if(canProceed(reply)){
            ReplySubject rs = (ReplySubject)getDecision(reply);

            if(rs != null){
               IPayload result = (IPayload)rs.getInfo(ReplySubject.PAYLOAD);
               getClient().receiveResult(result);
               //getApplicationBox().add(result);

            }//end if getDecision(reply)

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

    @Override
    public void shutdown() {
        super.shutdown();
    }

    public void asyncCall(IPayload payload) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}