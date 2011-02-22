/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTTimeoutDetector;
import br.ufba.lasid.jds.util.ITask;
import br.ufba.lasid.jds.util.StatedPBFTRequestMessage.RequestState;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.ufba.lasid.jds.util.Debugger;
import br.ufba.lasid.jds.util.StatedPBFTRequestMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTRequestCollectorServant extends PBFTCollectorServant<PBFTRequest>{


    public PBFTRequestCollectorServant(){
        
    }
    
    public PBFTRequestCollectorServant(PBFT p){
        setProtocol(p);
    }


    protected PBFTPrePrepare currentPrePrepare = null;
    
    /**
     * Collect the request sent by the client.
     * @param request -- the client request.
     */
    protected synchronized boolean accept(PBFTRequest request){
        
        PBFTServer pbft = (PBFTServer)getProtocol();

        if(request == null){
            Debugger.debug(
              "[PBFTRequestCollectorServant] s" + pbft.getLocalServerID() +
              " didn't accept " + request + ". (time = " + pbft.getClock().value() + ")"
            );
            return false;
        }

        if(!pbft.isTheNext(request)){
            Debugger.debug(
              "[PBFTRequestCollectorServant] s" + pbft.getLocalProcess().getID() +
              " didn't accept " + request + " because this is out of order."
            );

            return false;
        }
        /**
         * Check if request was already accepted.
         */
        if(pbft.getStateLog().wasAlreadyAccepted(request)){ 

            /**
             * Check if request was already served.
             */
            if(pbft.getStateLog().wasServed(request)){
                Debugger.debug(
                  "[PBFTRequestCollectorServant] s" + pbft.getLocalProcess().getID() +
                  " has already served " + request + "."
                );

                /**
                 * Retransmite the reply for a request that had been already
                 * served.
                 */
                emit(pbft.getStateLog().getReplyInRequestTable(request));

                return false;

            }

            Debugger.debug(
              "[PBFTRequestCollectorServant] s" + pbft.getLocalProcess().getID() +
              " has already accepted " + request + " so this was discarded."
            );


            return false;

        }

        /**
         * If the reply there is no more in the current state then PBFT2'll send
         * a reply if null payload.
         */
        if(pbft.getStateLog().noMore(request)){
            Debugger.debug(
              "[PBFTRequestCollectorServant] s" + pbft.getLocalProcess().getID() +
              " hasn't a response for  " + request + " any more."
            );

            emit(createNullReplyMessage(request));
            return false;

        }

        String digest = null;
        
        try{
            digest = pbft.getAuthenticator().getDisgest(request);
        } catch (Exception ex) {
            Logger.getLogger(PBFTRequestCollectorServant.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();

            return false;
        }

        Debugger.debug(
          "[PBFTRequestCollectorServant] s" + pbft.getLocalProcess().getID() +
          " accepted " + request + " at time " + pbft.getClock().value() + "."
        );


        /**
         * If a request is new then it will be accepted and put it in back log
         * state.
         */
        pbft.getStateLog().insertRequestInTables(
            digest, request, RequestState.WAITING
        );

        /**
         * Perform the batch procedure if the server is the primary replica.
         */
        if(pbft.isPrimary()){
            Debugger.debug(
              "[PBFTRequestCollectorServant] s" + pbft.getLocalProcess().getID() +
              " is the primary and is executing the batch procedure for " + request + "."
            );

            batch(digest);
            return true;

        }

        /**
         * Schedule a timeout for the arriving of the pre-prepare message if
         * the server is a secundary replica.
         */
        doSchedule(request, digest);
        return true;

    }

    public PBFTReply createNullReplyMessage(PBFTRequest request){
        
        PBFTServer pbft = (PBFTServer)getProtocol();

        PBFTReply reply = new PBFTReply(request, null, pbft.getLocalServerID(), pbft.getCurrentViewNumber());
        
        return reply;
        
    }


    /**
     * Add a request to the current request batch. If there isn't current batch
     * avail or the current batch is already complete then a new request batch
     * is created.
     * @param request -- the client request.
     */
    protected synchronized void batch(String digest){

        PBFTServer pbft = (PBFTServer)getProtocol();
        /**
         * If the server isn't the primary then it isn't able to execute the
         * batch procedure.
         */
        if(!pbft.isPrimary()){

            return;

        }

        /**
         * Get the current batch and add the request.
         */
        PBFTPrePrepare batch = getCurrentBatch();

        batch.getDigests().add(digest);

        if(isACompletedBatch(batch)){
            emit(batch);
            
        }

    }

    public synchronized void emit(PBFTPrePrepare pp){
        PBFTServer pbft = (PBFTServer)getProtocol();

        SignedMessage m;
        
        try {
            
            m = pbft.getAuthenticator().encrypt(pp);

            IGroup  g  = pbft.getLocalGroup();
            IProcess s = pbft.getLocalProcess();

            PDU pdu = new PDU();
            pdu.setSource(s);
            pdu.setDestination(g);
            pdu.setPayload(m);

            revokeSchedule(pp);
            
            pbft.getCommunicator().multicast(pdu, g);

            Debugger.debug(
              "[PBFTRequestCollectorServant]s" +  pbft.getLocalProcess().getID() +
              " sent preprepare " + pp + " at timestamp " + pbft.getClock().value() +
              " to group " + pbft.getLocalGroup() + "."
            );


            

        } catch (Exception ex) {
            Logger.getLogger(PBFTRequestCollectorServant.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();

        }
        
        setCurrentPrePrepare(null);
    }

    public void emit(PBFTReply r){
        PBFTServer pbft = (PBFTServer)getProtocol();

        SignedMessage m;

        try {

            m = pbft.getAuthenticator().encrypt(r);

            PDU pdu = new PDU();

            IProcess client = br.ufba.lasid.jds.Process.create(r.getClientID());
            IProcess server = pbft.getLocalProcess();
            
            pdu.setDestination(client);
            pdu.setSource(server);
            pdu.setPayload(m);


            pbft.getCommunicator().unicast(pdu, client);

        } catch (Exception ex) {
            Logger.getLogger(PBFTRequestCollectorServant.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    protected synchronized PBFTPrePrepare getCurrentPrePrepare(){
        return currentPrePrepare;
    }

    protected synchronized void setCurrentPrePrepare(PBFTPrePrepare preprepare){
        currentPrePrepare = preprepare;
    }

    /**
     * Get the current batch. If the current batch is null then a new batch'll
     * be created and the batch timeout will be scheduled. Otherwise, if the
     * current batch is complete then the current batch will be buffered and
     * a new batch will again be create and its timeout will be scheduled.
     * @return  -- the current batch;
     */
    protected synchronized PBFTPrePrepare getCurrentBatch(){
        return getCurrentBatch(true);
    }
    
    protected synchronized PBFTPrePrepare getCurrentBatch(boolean temporize) {

        /**
         * If the current batch is null then a new batch'll be created and the
         * batch timeout will be scheduled.
         */
        if(getCurrentPrePrepare() == null){

            setCurrentPrePrepare(
                createPrePrepareMessage(temporize)

            );

        }else{

            /**
             * If the current batch is complete then the current batch is
             * buffered and a new batch will be created and its respective
             * timeout will be scheduled.
             */
            if(isACompletedBatch(getCurrentPrePrepare())){

                emit(getCurrentPrePrepare());

                setCurrentPrePrepare(
                    createPrePrepareMessage(temporize)

                );


            }
        }

        return getCurrentPrePrepare();
    }

    /**
     * Create a pre-prepare message fro a client request.
     * @return -- a preprepare message.
     */
    protected PBFTPrePrepare createPrePrepareMessage(){
        return createPrePrepareMessage(false);
    }
    
    protected PBFTPrePrepare createPrePrepareMessage(boolean timporize){

        PBFTServer pbft = (PBFTServer)getProtocol();

        PBFTPrePrepare pp = new PBFTPrePrepare
        (
            pbft.getCurrentViewNumber(),
            PBFTServer.newSequenceNumber(),
            pbft.getLocalServerID()
        );


        if(timporize){
            doSchedule(pp);
        }


        return pp;
    }

    /**
     * Check if a request batch is complete. A batch is complete when its number
     * of requests is equals to the maximum specified for the protocol.
     * @param batch -- the batch that must be checked.
     * @return -- true if the batch is complete.
     */
    public boolean isACompletedBatch(PBFTPrePrepare pp) {
        try{
            return pp.getDigests().size() >= ((PBFTServer)getProtocol()).getBatchSize();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Schedule the batch flush in case of short inter-request times.
     * @param preprepare -- the batch request.
     */
    public synchronized void doSchedule(PBFTPrePrepare preprepare){
        
        PBFTServer pbft = (PBFTServer)getProtocol();

        PBFTTimeoutDetector timeoutTask =
                new PBFTTimeoutDetector()
        {

            PBFT pbft = (PBFT)getProtocol();
            
            /**
             * On expiration of the timeout, current batch it is buffered to be
             * pre-prepared.
             */
            @Override
            public void onTimeout() {
                
                //pbft.getScheduler().cancel(this);
                
                PBFTPrePrepare pp =
                        (PBFTPrePrepare)this.get("PREPREPARE");

                Debugger.debug(
                  "[PBFTRequestCollectorServant] batch timeout has been occurred"
                  + " in s" +  pbft.getLocalProcess().getID() +
                  " at timestamp " + pbft.getClock().value() + "."
                );

                emit(pp);

                //pbft.getScheduler().cancel(this);
            }

            @Override
            public void cancel(){
                pbft.getScheduler().cancel(this);
            }

        };


        timeoutTask.put("PREPREPARE", preprepare);

        pbft.getTaskTable(PBFT.BATCHTASKS).put(
                preprepare.getSequenceNumber(), (ITask)timeoutTask
        );

        long timestamp = pbft.getClock().value() + pbft.getBatchingTimeout().longValue();
        
        pbft.getScheduler().schedule(

           timeoutTask, timestamp

        );

        Debugger.debug(
          "[PBFTRequestCollectorServant] s" + pbft.getLocalProcess().getID() +
          " schedule a batch timeout for timestamp " + timestamp + "."
        );


    }

    public void revokeSchedule(PBFTPrePrepare  pp){

        PBFTServer pbft = ((PBFTServer)getProtocol());

        long seqn = pp.getSequenceNumber();

        PBFTTimeoutDetector timeoutTask =
            (PBFTTimeoutDetector) pbft.getTaskTable(PBFT.BATCHTASKS).get(seqn);

        if(timeoutTask != null){
            timeoutTask.cancel();
            pbft.getTaskTable(PBFT.BATCHTASKS).remove(seqn);

            Debugger.debug(
              "[PBFTRequestCollectorServant] s" + pbft.getLocalServerID() +
              " cancel batch timeout for " + pp + " at timestamp " + pbft.getClock().value()
            );
        }


    }


    /**
     * Schedule a view change in case of late response for the primary.
     * @param request -- the request.
     */

    public void doSchedule(PBFTRequest request, String digest){

        PBFTServer pbft = (PBFTServer)getProtocol();

        doSchedule(request, digest, pbft.getPrimaryFaultTimeout());

    }

    /**
     * Schedule a view change in case of late response for the primary.
     * @param request -- the client request.
     * @param timeout -- the view change timeout.
     */
    public void doSchedule(PBFTRequest request, String digest, long timeout){
        PBFTServer pbft = (PBFTServer)getProtocol();
        /**
         * A new timeout task is created.
         */
        PBFTTimeoutDetector timeoutTask =
                new PBFTTimeoutDetector()
        {
            PBFTServer pbft = (PBFTServer)getProtocol();

            /**
             * On the expiration of the timeout, perform a change view.
             */
            @Override
            public void onTimeout() {

//                emitChangeView();

                //pbft.getScheduler().cancel(this);
                
                Long timeout = (Long) get("TIMEOUT");
                PBFTRequest r = (PBFTRequest) get("REQUEST");
                String d = (String) get("DIGEST");

                StatedPBFTRequestMessage sprm = pbft.getStateLog().getStatedRequest(r);

                if(sprm != null && sprm.getState().equals(RequestState.WAITING)){
                    doSchedule(r, d, 2 * timeout);
                    return;
                }
                
            }

            @Override
            public void cancel(){
                
                pbft.getScheduler().cancel(this);
            }

        };

        timeoutTask.put("TIMEOUT", timeout);
        timeoutTask.put("REQUEST", request);
        timeoutTask.put("DIGEST", digest);

        pbft.getTaskTable(PBFT.REQUESTTASKS).put(
                digest, timeoutTask
        );

        long timestamp = pbft.getClock().value() + timeout;

        pbft.getScheduler().schedule(

           timeoutTask, timestamp

        );

        Debugger.debug(
          "[PBFTRequestCollectorServant] s" + pbft.getLocalProcess().getID() +
          " scheduled a new view procedure for timestamp " + timestamp + "."
        );


    }

    public boolean canConsume(Object object) {

        if(object instanceof PBFTRequest)
            return true;

        if(object instanceof PDU){
            PDU pdu = (PDU) object;
            return canConsume(pdu.getPayload());
        }

        if(object instanceof SignedMessage){
            try{

                SignedMessage m = (SignedMessage) object;

                return canConsume(m.getSignedObject().getObject());
                
            }catch(Exception ex){
              ex.printStackTrace();
            }
        }

        return false;
    }

}
