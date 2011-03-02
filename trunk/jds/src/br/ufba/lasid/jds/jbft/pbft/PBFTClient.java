/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.IProcess;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;
import br.ufba.lasid.jds.util.Debugger;

/**
 *
 * @author aliriosa
 */
public class PBFTClient extends PBFT implements IPBFTClient{


    protected Buffer applicationBox = BufferUtils.blockingBuffer(new UnboundedFifoBuffer());

    public Buffer getApplicationBox(){
        return applicationBox;
    }
    
    protected IClient client;

    public IClient getClient() {
        return client;
    }

    public void setClient(IClient client) {
        this.client = client;
    }

    
    protected long retransmissionTimeout = Long.valueOf(120000);
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

    protected QuorumTable qtable = new QuorumTable();

    /**
     * Perform an asynchronous call of the remote pbft service.
     * @param payload - the operation payload.
     */
    @Deprecated
    public void asyncCall(IPayload payload) {
        //accept(payload, false); /*does not properly working*/
    }

    public IPayload syncCall(IPayload payload){
        
        accept(payload, true);

        return (IPayload) getApplicationBox().remove();
    }

    /**
     * Collect the payload from the application.
     * @param payload -- the application payload.
     */
    public void accept(IPayload payload, boolean sync){
    
        PBFTRequest request = createRequestMessage(payload);
        request.setSynch(sync);

        emit(request);

        Debugger.debug(
          "[PBFTClient] c" + getLocalProcess().getID() +
          " sent " + request + " to " + getRemoteProcess() +
          " at time " + getClock().value()
        );
        
    }
    public void accept(IPayload payload){
        accept(payload, true);
    }


    /**
     * Create a new PBFT' IClient request message.
     * @param payload -- the application payload.
     * @return -- a new pbft request message.
     */
    protected PBFTRequest createRequestMessage(IPayload payload){

        IProcess c = getLocalProcess();

        long timestamp = getClock().value();

  //      lastClientTimestamp = currClientTimestamp;
//        currClientTimestamp = timestamp;

        PBFTRequest r = new PBFTRequest(payload, timestamp, c.getID());
        
//        r.setLastTimestamp(lastClientTimestamp);
        r.setSent(false);

        return r;

    }

    /**
     * do a request to server group and doSchedule the request retransmission.
     * @param request -- the client request.
     */
    protected synchronized void emit(PBFTRequest request){

        if(request!= null && !request.wasSent()){


            try{

                SignedMessage m = getAuthenticator().encrypt(request);
                
                PDU pdu = new PDU();

                pdu.setSource(getLocalProcess());
                pdu.setDestination(getRemoteProcess());
                pdu.setPayload(m);
                
                getCommunicator().multicast(pdu, (IGroup)getRemoteProcess());

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

        if(r != null){
            if(r.getTimestamp() != null){

                q = qtable.get(r.getTimestamp());

                int f = getServiceBFTResilience();

                if(q == null){
                    q = new Quorum(f + 1);
                    qtable.put(r.getTimestamp(), q);
                }
                
            }
        }


        
        return q;

        
    }

    public boolean updateState(PBFTReply r){

        if(r != null){
            
            Quorum q = getQuorum(r);

            if(q != null){

                for(IMessage m :  q){

                    PBFTReply r1 = (PBFTReply)m;
                    
                    if(!(r1.isSameRound(r) && !r1.isSameServer(r))){
                        return false;
                    }
                    
                }

                q.add(r);

                Debugger.debug(
                   "[PBFTClient] c"  + getLocalProcess().getID() + ", at time "
                  + getClock().value() + ", updated a entry in its log for the received " + r
                );

            }
        }

        return true;
    }

    /*
     * doSchedule the retransmition of a request.
     * @param request
     */
    public void doSchedule(PBFTRequest request){

        PBFTTimeoutDetector timeoutTask = new PBFTTimeoutDetector()
        {

            @Override
            public void onTimeout() {

                //getScheduler().cancel(this);
                
                PBFTRequest r = (PBFTRequest) this.get("REQUEST");
                revokeSchedule(r);

                r.setSent(false);
                
                emit(r);

                Debugger.debug(
                  "[PBFTClient] c" + getLocalProcess().getID() +
                  " re-sent " + r + " to " + getRemoteProcess() +
                  " at time " + getClock().value()
                );


            }

            @Override
            public void cancel(){
                PBFTRequest r = (PBFTRequest) this.get("REQUEST");
                revokeSchedule(r);
            }

        };

        timeoutTask.put("REQUEST", request);
        
        getTaskTable(PBFT.REQUESTTASKS).put(
                request.getTimestamp(), timeoutTask
        );

        getScheduler().schedule(
                
           timeoutTask, getClock().value() + getRetransmissionTimeout()

        );

    }

    public void revokeSchedule(PBFTRequest r){
        revokeSchedule(r.getTimestamp());
    }
    
    public void revokeSchedule(long timestamp){

        PBFTTimeoutDetector timeoutTask =
            (PBFTTimeoutDetector)getTaskTable(PBFT.REQUESTTASKS).get(timestamp);

        getScheduler().cancel(timeoutTask);

        getTaskTable(PBFT.REQUESTTASKS).remove(timestamp);
        
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }



}