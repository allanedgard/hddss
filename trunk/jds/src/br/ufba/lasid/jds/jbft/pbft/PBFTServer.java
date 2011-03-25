/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.BaseProcess;
import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.ISystemEntity;
import br.ufba.lasid.jds.adapters.AfterEventtable;
import br.ufba.lasid.jds.adapters.BeforeEventtable;
import br.ufba.lasid.jds.adapters.EventHandler;
import br.ufba.lasid.jds.adapters.IAfterEventListener;
import br.ufba.lasid.jds.adapters.IBeforeEventListener;
import br.ufba.lasid.jds.adapters.IEventListener;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.cs.IServer;
import br.ufba.lasid.jds.ft.util.CheckpointLogEntry;
import br.ufba.lasid.jds.ft.IRecoverableServer;
import br.ufba.lasid.jds.ft.util.PartList;
import br.ufba.lasid.jds.ft.util.PartTree;
import br.ufba.lasid.jds.ft.util.PartTree.PartEntry;
import br.ufba.lasid.jds.ft.util.Parttable;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBag;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewACK;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTFetch;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMetaData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTNewView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTProcessingToken;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTServerMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import br.ufba.lasid.jds.util.DigestList;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTLogEntry;
import br.ufba.lasid.jds.util.JDSUtility;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.comm.MessageCollection;
import br.ufba.lasid.jds.comm.MessageQueue;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTClientSessionTable;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTQuorum;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTServerMessageSequenceNumberComparator;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTTimeoutDetector;
import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.state.managers.IRecovarableStateManager;
import br.ufba.lasid.jds.util.IPayload;
import br.ufba.lasid.jds.util.ITask;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;

/**
 *
 * @author aliriosa
 */
public class PBFTServer extends PBFT implements IPBFTServer{

    protected long checkpointPeriod  = 256;
    protected long batchTimeout = 0;
    protected long sendStatusPeriod = 1000;
    protected int  batchSize = 50;
    protected long changeViewRetransmissionTimeout = 0;
    protected int currentViewNumber = 0;
    protected Object currentPrimaryID = null;
    protected static long SEQ = -1;
    protected  IServer server;


    /**
     * Update the state of the PBFT. Insert the pre-prepare message in
     * the log entry.
     * @param m
     */
    public boolean updateState(PBFTReply reply){
    //    synchronized(this){

            /**
             * If the preprepare is null then do nothing.
             */
            if(reply != null){


                StatedPBFTRequestMessage statedRequest =
                    getStateLog().getStateRequest(
                        reply.getClientID(), reply.getTimestamp()
                    );

                if(statedRequest != null){

                    statedRequest.setReply(reply);

                    statedRequest.setState(
                       StatedPBFTRequestMessage.RequestState.SERVED
                    );

                    JDSUtility.debug(
                      "[PBFTServer] s"  + getLocalServerID() +
                      ", at time " + getClock().value() + ", update a entry in "
                    + "its log for " + reply
                    );

                    PBFTLogEntry entry = getStateLog().get(statedRequest.getSequenceNumber());
                    PBFTPrePrepare preprepare = entry.getPrePrepare();

                    for(String digest : preprepare.getDigests()){
                        StatedPBFTRequestMessage aStatedReq =
                                getStateLog().getStatedRequest(digest);

                        if(!aStatedReq.getState().equals(StatedPBFTRequestMessage.RequestState.SERVED)){
                            return true;
                        }
                    }

                    getStateLog().updateNextExecuteSEQ(preprepare.getSequenceNumber());
                    return true;
                }

            }
      //  }
        return false;

    }

    IRecovarableStateManager rStateManager;
    public void loadState(){
       try {
           
            /*START TO FIX: GET PARAMETERS FROM CONFIG*/
            String defaultFName = "replica" + getLocalServerID();
           /*instatiates a empty property collection*/
            Properties initOptions = new Properties();

            /*defines the persistent storage id as replicai*/
            initOptions.put( JDSUtility.PersistentStorageID,
                             defaultFName );

            initOptions.put( JDSUtility.Filename,
                             defaultFName );


            /*defines default value for the maximum cache page size as 4096*/
            initOptions.put( JDSUtility.MaximumPageSize,
                             "4096" );

            /*Uses the JDSUtility facilities to create a Recoverable State Manager */
            rStateManager = JDSUtility.create( JDSUtility.RecovarableStateManagerProvider,
                                               initOptions );

            rStateManager.setObjectStorageID(defaultFName);
            doRollback();
//            /* rollback to last stable checkpoint */
//            rStateManager.rollback();
//
//            /* gets the last stable checkpoint, or zero if it doesn't exist */
//            long seqn = rStateManager.getCurrentCheckpointID();
//
//            /*END TO FIX*/
//
////*//            chkStore = new PBFTCheckpointStorage(getLocalServerID());
////*//            Tuple tuple = chkStore.getLast();
//            if(seqn >= 0){ //tuple!=null
////*//                String index = (String)tuple.getKey();
////*//                String pair[] = index.split(";");
//
//                //*//long seqn = Long.valueOf(pair[0]);
//
//                getStateLog().setNextPrePrepareSEQ(seqn+1);
//                getStateLog().setNextPrepareSEQ(seqn+1);
//                getStateLog().setNextCommitSEQ(seqn+1);
//                getStateLog().setNextExecuteSEQ(seqn+1);
//
//                getStateLog().setCheckpointLowWaterMark(seqn);
//
//                //IRecoverableServer srv = (IRecoverableServer)getServer();
//                lServer.setCurrentState(rStateManager.getCurrentState());//*//((IState)tuple.getValue()).copy());
//                updateCurrentSequenceNumber(seqn);
//            }

           IRecoverableServer lServer = (IRecoverableServer)getServer();

            /**
             * Sets the current state managed by the state manager as the current
             * server state.
             */
            rStateManager.setCurrentState(lServer.getCurrentState());
            
        } catch (Exception ex) {
            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }
    @Override
    public void startup() {

        loadState();
        
        super.startup();

        schedulePeriodicStatusSend();
        
        emitFetch();
        //emit(createFetchMetaDataMessage(), getLocalGroup().minus(getLocalProcess()));

    }

    public void doCheckpoint(long seqn) {
        //synchronized(this){

            long lowWater = getCheckpointLowWaterMark();
            long startSEQ = lowWater+1;
            long finalSEQ = seqn;
            long execSEQ = getStateLog().getNextExecuteSEQ() - 1;

            for(long currSEQ = startSEQ; currSEQ <= finalSEQ; currSEQ ++){
                /**
                 * If the sequence number was executed by the replica then we'll be
                 * able to compute the checkpoint.
                 */
                if(currSEQ <= execSEQ){

                    Quorum q = getStateLog().getQuorum(CHECKPOINTQUORUMSTORE, String.valueOf(currSEQ));

                    if(q != null && q.complete()){

                        PBFTCheckpoint checkpoint = (PBFTCheckpoint)q.get(0);

                        CheckpointLogEntry clogEntry =
                                rStateManager.getLogEntry(checkpoint.getSequenceNumber());

                        if(!(
                            clogEntry != null &&
                            clogEntry.getDigest() != null &&
                            clogEntry.getDigest().equals(checkpoint.getDigest()
                        ))){
                            break;
                        }

                        //PBFTCheckpointTuple ctuple = getStateLog().getCachedState().get(checkpoint.getSequenceNumber());

//                        if(!(ctuple != null && ctuple.getDigest().equals(checkpoint.getDigest()))){
//                            break;
//                        }

                        if(!clogEntry.wasProcessed()){
                            try {
                                rStateManager.setCurrentState(clogEntry.getState());
                                rStateManager.checkpoint(clogEntry.getCheckpointID());
                                rStateManager.removeLogEntry(clogEntry.getCheckpointID());
                                lowWater = currSEQ;

                            } catch (Exception ex) {
                                Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

//                        if(!ctuple.isUpdated()){
//                            try {
//                                chkStore.write(checkpoint.getEntryKey(), ctuple.getCurrentState(), true);
//                                chkStore.commit();
//                                lowWater = currSEQ;
//
//                            } catch (IOException ex) {
//                                Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
//                                ex.printStackTrace();
//                            }
//                            ctuple.setUpdated(true);

                        }
                    }
                //}//
            }

            if(lowWater >= getStateLog().getCheckpointLowWaterMark()){
                getStateLog().setCheckpointLowWaterMark(lowWater);

                JDSUtility.debug("[PBFTServer:doCheckpoint(seqn)] s" + getLocalServerID() + ", at time " + getClockValue() + ", starts the garbage collection procedure with LCWM = " + lowWater +"...");

                getStateLog().doGarbage();

                JDSUtility.debug("[PBFTServer:doCheckpoint(seqn)] s" + getLocalServerID() + " complete the garbage collection procedure for LCWM = " + lowWater +"!");
            }else{
                JDSUtility.debug("[PBFTServer:doCheckpoint(seqn)] s" + getLocalServerID() + ", at time " + getClockValue() + ", does not have cached stated matching LCWM = " + seqn +"...");
            }
//        if(seqn > lowWater){
//            if(getHighestCheckpointIndex() == null){
//                 setHighestCheckpointIndex(new CheckpointIndex());
//                 getHighestCheckpointIndex().setSequenceNumber(seqn);
//            }
//
//            PBFTFetch fetch = createFetchMessage();
//            emit(fetch, new br.ufba.lasid.jds.BaseProcess(fetch.getSelectedReplierID()));
//            return;
//
//        }
    }

    protected  boolean lockCheckpointProcedure = false;

    public  boolean isLockCheckpoint() {
        return lockCheckpointProcedure;
    }

    public  void setLockCheckpoint(boolean lockCheckpointProcedure) {
        this.lockCheckpointProcedure = lockCheckpointProcedure;
    }
    
    protected  long slidindWindowSize = 1;

    public void setSlidingWindowSize(Long size) {
        slidindWindowSize = size;
    }

     Buffer window = BufferUtils.blockingBuffer(new UnboundedFifoBuffer());
    
    public  long getSlidingWindowSize(){
        return slidindWindowSize;
    }

    public  void waitWindow(){
        long ppSEQ = getStateLog().getNextPrePrepareSEQ();
        long exSEQ = getStateLog().getNextExecuteSEQ();
        boolean isFull = ppSEQ >= exSEQ + slidindWindowSize;
        while (isFull) {
            isFull = (Boolean)window.remove();
        }
    }
    
    public void slidWindow(){
        long ppSEQ = getStateLog().getNextPrePrepareSEQ();
        long exSEQ = getStateLog().getNextExecuteSEQ();
        boolean isFull = ppSEQ >= exSEQ + slidindWindowSize;
        
        window.add(isFull);
    }

    
  /*########################################################################
   # 1. Methods for handling client requests.
   #########################################################################*/
    public void handle(PBFTRequest req){
        
        JDSUtility.debug(
           "[PBFTServer:handle(request)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + req
        );

        if(canProceed(req)){
            
            JDSUtility.debug(
               "[PBFTServer:handle(request)] s" +getLocalServerID() + ", "  +
               "at time " + getClockValue() + ", accepted " + req + " " +
               "as a new request."
            );

            String digest = null;

            try{
                digest = getAuthenticator().getDigest(req);

            } catch (Exception ex) {

                Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                return;

            }

            /* it's a new request, so it'll be accepted and put it in backlog state. */
            getStateLog().insertRequestInTables(
                digest, req, StatedPBFTRequestMessage.RequestState.WAITING
            );

            JDSUtility.debug(
              "[PBFTServer:handle(request)] s" + getLocalServerID() + " " +
              "inserted " + req + " in waiting state."
            );

            /* Perform the batch procedure if the server is the primary replica. */
            if(isPrimary()){
                JDSUtility.debug(
                  "[PBFTServer:handle(request)] s" + getLocalServerID() + " (primary)" +
                  " is executing the batch procedure for " + req + "."
                );

                batch(digest);
                return;
            }

            /**
             * Schedule a timeout for the arriving of the pre-prepare message if
             * the server is a secundary replica.
             */
            scheduleViewChange(digest);

        }
    }

    public boolean canProceed(PBFTRequest req){
        
        IProcess client = new BaseProcess(req.getClientID());

        /* checks if the received request has been already accepted. */
        if(getStateLog().wasAccepted(req)){
            /* check if received request was already served. */
            if(getStateLog().wasServed(req)){
                JDSUtility.debug(
                  "[PBFTServer:canProceed(request)] s" + getLocalServerID() + " " +
                  "has already served " + req + "."
                );
                /* retransmite the reply when the request was already served */
                emit(getStateLog().getReplyInRequestTable(req), client);
                return false;
            }

            JDSUtility.debug(
              "[PBFTServer:canProceed(request)] s" + getLocalServerID() + " " +
              "has already accepted " + req + " so this was discarded."
            );

            return false;
        }

        /* If the reply doesn't exist anymore in the current state then it'll
           send a reply if null payload. */
        if(getStateLog().noMore(req)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(request)] s" + getLocalServerID() + " " +
              "hasn't a response for  " + req + " any more."
            );

            /* sends a null reply */
            emit(createNullReplyMessage(req), client);
            return false;
        }

        /* if the request isn't ordered then it'll be discarded */
        if(!isNextRequest(req)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(request)] s" + getLocalServerID() + " " +
              "didn't accept " + req + " because this is out of order."
            );
            return false;
        }        
        updateClientSession(req);
        return true;
    }

    protected  DigestList batch = new DigestList();

    protected boolean hasBatch(){
        return batch.size() > 0;
    }
    
    public void batch(String digest){
        if(isPrimary()){
            batch.add(digest);
            scheduleSendBatch(digest);
            if(isACompleteBatch()){
                emitBatch();
                return;
            }//end if is a complete batch
        }//end if is primary
    }

    protected boolean isACompleteBatch(){
        return batch.size() >= getBatchSize();
    }

    protected void emitBatch(){
        synchronized(this){
            /* if there was batch request */
            if(!batch.isEmpty()){
                /* Create a new pre-prepare message */
                PBFTPrePrepare preprepare = new PBFTPrePrepare(
                    getCurrentViewNumber(),
                    getStateLog().getNextPrePrepareSEQ(),
                    getLocalServerID()
                );

                preprepare.getDigests().addAll(batch);

                for(String digest: preprepare.getDigests()){
                    revokeSendBatch(digest);
                    batch.remove(digest);
                }
                updateState(preprepare);
                emit(preprepare, getLocalGroup().minus(getLocalProcess()));
            }
        }
    }

    public void revokeSendBatch(String digest){
        PBFTTimeoutDetector ttask = (PBFTTimeoutDetector)getTaskTable(PBFT.BATCHTASKS).get(digest);

        if(ttask != null){
            getScheduler().cancel(ttask);
            getTaskTable(PBFT.BATCHTASKS).remove(digest);
            JDSUtility.debug(
              "[PBFTServer:doRevoke(digest)] s" + getLocalServerID() +  " " +
              "revoked a batch timeout for (" + digest + ")."
            );
        }
    }

    public void scheduleSendBatch(String digest){
        PBFTTimeoutDetector ttask = new PBFTTimeoutDetector() {
            @Override
            public void onTimeout() {
                JDSUtility.debug(
                  "[PBFTServer::PBFTTimeoutDetector:onTimeout] s" + getLocalServerID() + " " +
                  "had a batch timer expired at time " + getClockValue() + "."
                );
                if(hasBatch()) emitBatch();
            }
        };

        long timestamp = getClockValue() + getBatchingTimeout();
        ttask.put("DIGEST", digest);

        getScheduler().schedule(ttask, timestamp);
        getTaskTable(PBFT.BATCHTASKS).put(digest, (ITask)ttask);
        JDSUtility.debug(
          "[PBFTServer:doSchedule(digest)] s" + getLocalServerID() + " " +
          "scheduled a batch timeout for " + timestamp + "(" + digest + ")."
        );
    }
    
    public void scheduleViewChange(String digest){
        scheduleViewChange(digest, getPrimaryFaultTimeout());
    }

    /**
     * Schedule a view change in case of late response for the primary.
     * @param request - the client request.
     * @param timeout - the view change timeout.
     */
    public void scheduleViewChange(String digest, long timeout){
        /* A new timeout task is created. */
        PBFTTimeoutDetector ttask = new PBFTTimeoutDetector(){
            /* On the expiration of the timeout, perform a change view. */
            @Override
            public void onTimeout() {

                long timeout = (Long) get("TIMEOUT");
                String digest = (String) get("DIGEST");

                StatedPBFTRequestMessage statedRequest = getStateLog().getStatedRequest(digest);

                if(statedRequest != null){
                    if(statedRequest.getState().equals(StatedPBFTRequestMessage.RequestState.WAITING)){
                        JDSUtility.debug(
                          "[PBFTServer:scheduleChangeView::PBFTTimeoutDetector:onTimeout()] s" + getLocalServerID() + ", " +
                          "at time " + getClockValue() + ", is starting a change view (" + statedRequest.getDigest() + ")"
                        );
                        
                        emitChangeView();
                    }//endif request is waiting
                }
            }
        };

        ttask.put("TIMEOUT", timeout);
        ttask.put("DIGEST", digest);

        getTaskTable(PBFT.REQUESTTASKS).put(digest, ttask);

        long timestamp = getClockValue() + timeout;

        getScheduler().schedule(ttask, timestamp);

        JDSUtility.debug(
          "[PBFTServer:doScheduleChangeView(digest, timeout)] s" + getLocalServerID() + ", " +
          "at time " + getClockValue() + ", scheduled a new view procedure for " +
          "timestamp " + timestamp + "(" + digest + ")."
        );
    }

    /**
     * revoke the timer assigned to a client request (i.e. the change view timer).
     * @param leafPartDigest
     */
    public void revokeViewChange(String digest){
        PBFTTimeoutDetector timeoutTask = (PBFTTimeoutDetector)getTaskTable(PBFT.REQUESTTASKS).get(digest);
        if(timeoutTask != null){
            getScheduler().cancel(timeoutTask);
            getTaskTable(PBFT.REQUESTTASKS).remove(digest);

            JDSUtility.debug(
              "[PBFTServer:doRevokeViewChange()] s" + getLocalServerID() + ", at " +
              "time " + getClockValue() +", revoked a change view timeout (" + digest + ")."
            );
        }
    }

 /*########################################################################
  # 2. Methods for handling pre-prepare messages.
  #########################################################################*/
    public void handle(PBFTPrePrepare pp){
        JDSUtility.debug(
           "[PBFTServer:handle(preprepare)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + pp
        );

        if(canProceed(pp)){
            if(updateState(pp)){
                if(!isPrimary()){
                    PBFTPrepare prepare = createPrepareMessage(pp);
                    long seqn = pp.getSequenceNumber();
                    
                    if(getStateLog().get(seqn) != null && !getStateLog().get(seqn).isNOP()){
                        emit(prepare, getLocalGroup().minus(getLocalProcess()));
                    }                    
                    updateState(prepare);
                }
            }
        }
    }

    public boolean canProceed(PBFTPrePrepare pp){
        /* If the preprepare hasn't a valid sequence / view number then it'll
           force a change view. */
        if(!(checkSequenceNumber(pp) && checkViewNumber(pp))){
            long nxPP = getStateLog().getNextPrePrepareSEQ();
            long nxPR = getStateLog().getNextPrepareSEQ();
            long nxCM = getStateLog().getNextCommitSEQ();
            long nxEX = getStateLog().getNextExecuteSEQ();
            long viewn  = getCurrentViewNumber();

            JDSUtility.debug(
              "[PBFTServer:canProceed(preprepare)] s" + getLocalServerID() + ", at " +
              "time " + getClockValue() + ", discarded " + pp + " because " +
              "it hasn't a valid sequence/view number. (viewn = " + viewn + ")"
              + "[PP = " + nxPP + ", PR = " + nxPR + ", CM =" + nxCM + ", EX = " + nxEX + "]"
            );

            return false;

        }

        /**
         * If the preprepare message wasn't sent by the primary replica then
         * it will be discarded.
         */
        if(!wasSentByPrimary(pp)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(preprepare)] s" + getLocalServerID() + ", " +
              "at time " + getClockValue() + ", discarded " + pp + " " +
              "because it wasn't sent by primary server s" + getCurrentPrimaryID()
            );
            
            return false;
        }
        return true;
    }

    public boolean updateState(PBFTPrePrepare preprepare){

//        synchronized(this){
            /**
             * If the preprepare is null then do nothing.
             */
            if(preprepare == null){

                return false;

            }

            /*
             * Get composite key of the prepare.
             */

            Long entryKey = preprepare.getSequenceNumber();

            /**
             * If the entry key is diferent of null then update state. Otherwise do
             * nothing.
             */
            if(entryKey != null) {

                /**
                 * Get the batch in the preprepare.
                 */

                 DigestList digests = preprepare.getDigests();

                /**
                 * For each request in batch, check if such request was received.
                 */
                for(String digest : digests){

                    /**
                     * If some request in the bacth wasn't received then the log
                     * state won't be able to be updated.
                     */
                    if(!getStateLog().hasARequestWaiting(digest)){
                        JDSUtility.debug(
                            "[PBFTServer:updateStatus(preprepare)] s" +getLocalServerID() + ", " +
                            "at time " + getClockValue() + ", couldn't update pre-prepare " + preprepare + " " +
                            "because it has a digest (" + digest + ") that hasn't been in waiting state anymore."

                        );
                       return false;

                    }

                }

                /**
                 * get a log  entry for current preprepare.
                 */
                PBFTLogEntry entry = getStateLog().get(entryKey);

                /*
                 * if there isn't a entry then create one.
                 */
                if(entry == null){

                    entry = new PBFTLogEntry(preprepare);

                    /**
                     * Update the entry in log.
                     */
                    getStateLog().put(entryKey, entry);

                    JDSUtility.debug(
                      "[PBFTServer:updateStatus(preprepare)] s"  + getLocalProcess().getID() +
                      ", at time " + getClock().value() + ", created a new entry in "
                    + "its log for " + preprepare
                    );

                    /**
                     * For each request in batch, check if such request was received.
                     */
                    for(String digest : preprepare.getDigests()){

                        StatedPBFTRequestMessage statedRequest = getStateLog().getStatedRequest(digest);

                        statedRequest.setState(StatedPBFTRequestMessage.RequestState.PREPREPARED);
                        statedRequest.setSequenceNumber(preprepare.getSequenceNumber());

                        if(!isPrimary()){
                            revokeViewChange(digest);
                        }

                    }

                    getStateLog().updateNextPrePrepareSEQ(preprepare);

                    return true;
                }

            }
  //      }
            return false;


    }

    /**
     * Create a new Prepare Message from a pre-prepare message.
     * @param pp - the pre-prepare message.
     * @return the created prepare message.
     */
    public PBFTPrepare createPrepareMessage(PBFTPrePrepare pp){
        PBFTPrepare p = new PBFTPrepare(pp, getLocalServerID());
        return p;
    }

 /*########################################################################
  # 3. Methods for handling prepare messages.
  #########################################################################*/
    public void handle(PBFTPrepare p){
        JDSUtility.debug(
           "[PBFTServer:handle(prepare)] s" + getLocalServerID() + ", at " +
           "time " + getClockValue() + ", received " + p
        );

        if(canProceed(p)){
            if(updateState(p)){
                
                PBFTCommit commit = createCommitMessage(p);
                long seqn = p.getSequenceNumber();
                if(!getStateLog().get(seqn).isNOP()){
                    emit(commit, getLocalGroup().minus(getLocalProcess()));
                }

                updateState(commit);
            }
        }
    }

    public boolean canProceed(PBFTPrepare p){
        /**
         * If the prepare message was sent by the primary then it will
         * be discarded.
         */
        if(wasSentByPrimary(p)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(prepare)] s" + getLocalServerID() + ", " +
              "at time " + getClockValue() + ", discarded " + p + " " +
              "because it was sent by the primary " + getCurrentPrimaryID()
            );

            return false;
        }

        /**
         * If the preprepare message wasn't sent by a group member then it will
         * be discarded.
         */
        if(!wasSentByAGroupMember(p)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(prepare)] s" + getLocalServerID() + ", at " +
              "time " + getClockValue() + ", discarded " + p + " because " +
              "it wasn't sent by a member of the group " + getLocalGroup()
            );

            return false;
        }

        if(!getStateLog().wasPrePrepared(p)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(prepare)] s" + getLocalServerID() + ", at " +
              "time " + getClockValue() + ", discarded " + p + " because " +
              "it hasn't received a related pre-prepare."
            );

            return false;
        }

        /**
         * If the preprepare hasn't a valid sequence or view number then force a
         * change view.
         */
        if(!checkViewNumber(p)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(prepare)] s" + getLocalServerID() + ", at " +
              "time " + getClockValue() + ", discarded " + p + " because " +
              "it hasn't a valid view number. (CURRENT-VIEW = " + getCurrentViewNumber() + ")."
            );

            return false;

        }

        /**
         * If the preprepare hasn't a valid sequence or view number then force a
         * change view.
         */
        if(!inAValidSequenceRange(p)){
            long lcwm = getCheckpointLowWaterMark();
            long hcwm = getCheckpointHighWaterMark();
            JDSUtility.debug(
              "[PBFTServer:canProceed(prepare)] s" + getLocalServerID() + ", at " +
              "time " + getClockValue() + ", discarded " + p + " because " +
              "it hasn't a valid sequence (LCWM = " + lcwm + ", HCWM = " + hcwm + ")."
            );

            return false;

        }
        return true;
    }

    public PBFTCommit createCommitMessage(PBFTPrepare p){
        PBFTCommit c = new PBFTCommit(p, getLocalServerID());
        return c;
    }

    public boolean updateState(PBFTPrepare prepare){
            /* If the preprepare is null then do nothing. */
            if(prepare == null) return false;

            /* Get composite key of the prepare. */
            Long entryKey = prepare.getSequenceNumber();

            /**
             * If the entry key is diferent of null then update state. Otherwise do
             * nothing.
             */
            if(entryKey != null) {

                /* Get the batch in the preprepare. */
                 DigestList digests = prepare.getDigests();

                /* For each request in batch, check if such request was received. */
                for(String digest : digests){
                    /**
                     * If some request in the bacth wasn't received then the log
                     * state won't be able to be updated.
                     */
                    if(!getStateLog().hasARequestPrePrepared(digest)) return false;
                }

                /**
                 * get a log  entry for current preprepare.
                 */
                PBFTLogEntry entry = getStateLog().get(entryKey);

                /*
                 * if there isn't a entry then create one.
                 */
                if(entry != null){

                    PBFTQuorum q = entry.getPrepareQuorum();

                    if(q == null){

                        int f = getServiceBFTResilience();

                        q = new PBFTQuorum(2 * f);

                        entry.setPrepareQuorum(q);

                        JDSUtility.debug(
                          "[PBFTServer:updateState(prepare)] s"  + getLocalServerID() +
                          ", at time " + getClockValue() + ", created a new certificate for " + prepare + "."
                        );

                    }

                    for(IMessage m : q){
                        PBFTPrepare p1 = (PBFTPrepare) m;
                        if(!(
                            p1.getSequenceNumber().equals(prepare.getSequenceNumber()) &&
                            p1.getViewNumber().equals(prepare.getViewNumber())         &&
                            p1.getDigests().containsAll(prepare.getDigests())
                        )){
                            return false;
                        }

                        if(p1.getReplicaID().equals(prepare.getReplicaID())){
                            JDSUtility.debug(
                              "[PBFTServer:updateState(prepare)] s"  + getLocalServerID() +
                              ", at time " + getClockValue() + ", cann't use " + prepare +
                              " to update its log because it's a duplicated prepare."
                            );
                            return false;
                        }
                    }

                    q.add(prepare);

                    JDSUtility.debug(
                      "[PBFTServer:updateState(prepare)] s"  + getLocalServerID() +
                      ", at time " + getClockValue() + ", inserted " + prepare +
                      " in the local quorum certicate."
                    );


                    Long seqn = prepare.getSequenceNumber();

                    if(q.complete()){

                        for(String digest : prepare.getDigests()){

                            StatedPBFTRequestMessage statedReq =
                                    getStateLog().getStatedRequest(digest);

                            statedReq.setState(
                                    StatedPBFTRequestMessage.RequestState.PREPARED
                            );

                            statedReq.setSequenceNumber(prepare.getSequenceNumber());
                        }

                        JDSUtility.debug(
                          "[PBFTServer:accept(prepare)] s" + getLocalServerID()     +
                          ", at time " + getClockValue() + ", has just complete " +
                          "the prepare phase for sequence number (" + seqn + ") and "     +
                          "view number (" + prepare.getViewNumber() + ")."
                        );

                        getStateLog().updateNextPrepareSEQ(prepare);

                        /* Update the entry in log. */
                        getStateLog().put(entryKey, entry);

                        JDSUtility.debug(
                          "[PBFTServer] s"  + getLocalServerID() +
                          ", at time " + getClockValue() + ", update a entry in "
                        + "its log for " + prepare
                        );

                        return true;
                    }

                }
            }
      //  }
        return false;

    }


/*########################################################################
  # 4. Methods for handling commit messages.
  #########################################################################*/
    public void handle(PBFTCommit commit){

        JDSUtility.debug(
           "[PBFTServer:handle(commit)] s" + getLocalServerID() + ", at time " + 
           "" + getClockValue() + ", received " + commit
        );
        long seqn = commit.getSequenceNumber();
        int viewn = commit.getViewNumber();
        if(canProceed(commit)){
            if(updateState(commit)){
                handle(new PBFTProcessingToken(viewn, seqn));
            }
        }
    }

    public boolean canProceed(PBFTCommit commit){
        /**
         * If the preprepare message wasn't sent by a group member then it will
         * be discarded.
         */
        if(!wasSentByAGroupMember(commit)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(commit)] s" + getLocalServerID() + ", at " +
              "time " + getClockValue() + ", discarded " + commit + " because " +
              "it wasn't sent by a member of the group " + getLocalGroup()
            );

            return false;
        }

        if(!getStateLog().wasPrepared(commit)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(commit)] s" + getLocalServerID() + ", at " +
              "time " + getClockValue() + ", discarded " + commit + " because " +
              "it hasn't received a related prepare."
            );

            return false;
        }

        /**
         * If the preprepare hasn't a valid sequence or view number then force a
         * change view.
         */
        if(!checkViewNumber(commit)){

            JDSUtility.debug(
              "[PBFTServer:canProceed(commit)] s"  + getLocalServerID() + ", at " +
              "time " + getClockValue() + ", discarded " + commit + " because " +
              "it hasn't a valid view number (CURRENT-VIEW = " + getCurrentViewNumber() + ")."
            );
            return false;
        }

        /**
         * If the preprepare hasn't a valid sequence or view number then force a
         * change view.
         */
        if(!inAValidSequenceRange(commit)){
            long lcwm = getCheckpointLowWaterMark();
            long hcwm = getCheckpointHighWaterMark();
            JDSUtility.debug(
              "[PBFTServer:canProceed(commit)] s"  + getLocalServerID() + ", at " +
              "time " + getClockValue() + ", discarded " + commit + " because it " +
              "hasn't a valid sequence number (LCWM = " + lcwm + ", HCWM = " + hcwm + ")."
            );

            return false;

        }
        return true;

    }

    public boolean updateState(PBFTCommit commit){
        //synchronized(this){

            /**
             * If the preprepare is null then do nothing.
             */
            if(commit == null){

                return false;

            }

            /*
             * Get composite key of the prepare.
             */

            Long entryKey = commit.getSequenceNumber();

            /**
             * If the entry key is diferent of null then update state. Otherwise do
             * nothing.
             */
            if(entryKey != null) {

                /**
                 * get a log  entry for current preprepare.
                 */
                PBFTLogEntry entry = getStateLog().get(entryKey);

                /*
                 * if there isn't a entry then create one.
                 */
                if(entry != null){

                    Quorum pq = entry.getPrepareQuorum();

                    if(pq != null && pq.complete()){

                        for(IMessage m : pq){
                            PBFTPrepare p = (PBFTPrepare) m;
                            if(!(
                                p.getSequenceNumber().equals(commit.getSequenceNumber()) &&
                                p.getViewNumber().equals(commit.getViewNumber())
                            )){
                                return false;
                            }
                        }

                        PBFTQuorum q = entry.getCommitQuorum();

                        if(q == null){

                            int f = getServiceBFTResilience();

                            q = new PBFTQuorum(2 * f + 1);

                            entry.setCommitQuorum(q);
                        }

                        for(IMessage m : q){

                            PBFTCommit c1 = (PBFTCommit) m;

                            if(!(
                                 c1.getSequenceNumber().equals(commit.getSequenceNumber()) &&
                                 c1.getViewNumber().equals(commit.getViewNumber())
                             )){
                                return false;
                            }

                            if(c1.getReplicaID().equals(commit.getReplicaID())){
                                JDSUtility.debug(
                                  "[PBFTServer:updateState(commit)] s"  + getLocalServerID() +
                                  ", at time " + getClockValue() + ", cann't use " + commit +
                                  " to update its log because it's a duplicated commit."
                                );

                                return true;
                            }
                        }

                        q.add(commit);

                        JDSUtility.debug(
                          "[PBFTServer:updateState(commit)] s"  + getLocalServerID() +
                          ", at time " + getClockValue() + ", inserted " + commit +
                          " in the local quorum certicate."
                        );


                        /**
                         * Update the entry in log.
                         */
                        getStateLog().put(entryKey, entry);


                        Long seqn = commit.getSequenceNumber();

                        if(q.complete()){

                            PBFTPrePrepare  pp = getStateLog().getPrePrepare(seqn);

                            for(String digest : pp.getDigests()){

                                StatedPBFTRequestMessage statedReq =
                                        getStateLog().getStatedRequest(digest);

                                statedReq.setState(
                                        StatedPBFTRequestMessage.RequestState.COMMITTED
                                );

                                statedReq.setSequenceNumber(commit.getSequenceNumber());


                                JDSUtility.debug(
                                  "[PBFTServer:updateState(commit)] s" + getLocalServerID()     +
                                  ", at time " + getClockValue() + ", committed " + statedReq.getRequest() +
                                  " for processing in view number (" + commit.getViewNumber() + ")."
                                );

                            }

                            getStateLog().updateNextCommitSEQ(commit);

                            JDSUtility.debug(
                              "[PBFTServer:updateState(commit)] s"  + getLocalServerID() +
                              ", at time " + getClockValue() + ", update a entry in "
                            + "its log for " + commit
                            );

                            return true;
                        }
                    }
                }
            }
        //}
        return false;
    }

 /*########################################################################
  # 5. Methods for handling checkpoint messages.
  #########################################################################*/
    public void handle(PBFTCheckpoint checkpoint){

        JDSUtility.debug(
           "[PBFTServer:handle(checkpoint)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + checkpoint
        );

        if(canProceed(checkpoint)){

            if(updateState(checkpoint)){

                long hcwm = getCheckpointHighWaterMark();
                long seqn = checkpoint.getSequenceNumber();

                if(seqn > hcwm){
                    JDSUtility.debug(
                      "[PBFTServer:handle(checkpoint)] s" + getLocalServerID() + ", at " +
                      "time " + getClockValue() + ", detected a stable checkpoint " +
                      "certificate with sequence number (" + seqn + ") " + "greater than " +
                      "its high checkpoint water mark (HCWM = " + hcwm + ")."
                    );
                    JDSUtility.debug(
                      "[PBFTServer:handle(checkpoint)] s" + getLocalServerID() + ", at " +
                      "time " + getClockValue() + ", is going to start a start transfer procedure."
                    );

                    emitFetch();
                    return;
                }

                CheckpointLogEntry clogEntry = rStateManager.getLogEntry(seqn);
                if(clogEntry != null){
                    doCheckpoint(seqn);
                    return;
                }//end if ctupe != null
                
                JDSUtility.debug(
                  "[PBFTServer:handle(checkpoint)] s" + getLocalServerID() + ", at " +
                  "time " + getClockValue() + ", is going to fetch the current state " + 
                  "because it hasn't a cached state for the stable checkpoint " + checkpoint + "."
                );
                
                /** 
                 * We must work better on this after we finish change view 
                 * procedure.
                 */
                emitFetch();
                                
            }//end if updateState(checkpoint)

        }//end if canProceed(checkpoint)
        
    }//end handle(checkpoint);
    
    public boolean canProceed(PBFTCheckpoint checkpoint){
//        synchronized(this){
            /**
             * If the preprepare message wasn't sent by a group member then it will
             * be discarded.
             */
            if(!wasSentByAGroupMember(checkpoint)){
                JDSUtility.debug(
                  "[PBFTServer:canProceed(checkpoint)] s" + getLocalServerID() + ", at " +
                  "time " + getClockValue() + ", discarded " + checkpoint + " because " +
                  "it wasn't sent by a member of the group " + getLocalGroup()
                );

                return false;
            }//end if wasSentByAGroupMember(checkpoint)

            /**
             * If the preprepare message wasn't sent by a group member then it will
             * be discarded.
             */
            long lcwm = getCheckpointLowWaterMark();
            long seqn = checkpoint.getSequenceNumber();

            if(lcwm > seqn){
                JDSUtility.debug(
                  "[PBFTServer:canProceed(checkpoint)] s" + getLocalServerID() + ", at " +
                  "time " + getClockValue() + ", discarded " + checkpoint + " because " +
                  "it has a sequence number < current LCWM = " + lcwm + "). "
                );

                return false;
            }//end if lcwm  > seqn
  //      }
        return true;
        
    }//end canProceed(checkpoint)

    /**
     * Update the state of the PBFT. Insert the pre-prepare message in
     * the log entry.
     * @param m
     */
    public boolean updateState(PBFTCheckpoint checkpoint){

        /* If the preprepare is null then do nothing. */
        if(checkpoint == null) return false;

        /* Get composite key of the prepare. */
        Long entryKey = checkpoint.getSequenceNumber();

        /* If the entry key is not null then it'll update state */
        if(entryKey != null) {

            Quorum q = getStateLog().getQuorum(CHECKPOINTQUORUMSTORE, entryKey.toString());

            if(q == null){

                int f = getServiceBFTResilience();

                q = new Quorum(2 * f + 1);

                getStateLog().getQuorumTable(CHECKPOINTQUORUMSTORE).put(entryKey.toString(), q);
            }

            for(IMessage m : q){

                PBFTCheckpoint c1 = (PBFTCheckpoint) m;
                if(!(
                    c1.getSequenceNumber().equals(checkpoint.getSequenceNumber()) &&
                    c1.getDigest().equals(checkpoint.getDigest())
                )){
                    JDSUtility.debug(
                      "[PBFTServer:updateState(checkpoint)] s"  + getLocalServerID() +
                      ", at time " + getClock().value() + ", cann't use " + checkpoint +
                      " the digests don't match (" + c1 +  " / " + checkpoint + ")."
                    );

                    return false;
                }

                if(c1.getReplicaID().equals(checkpoint.getReplicaID())){
                    JDSUtility.debug(
                      "[PBFTServer:updateState(checkpoint)] s"  + getLocalServerID() +
                      ", at time " + getClockValue() + ", cann't use " + checkpoint +
                      " to update its log because it's a duplicated checkpoint."
                    );
                    return false;
                }
            }

            q.add(checkpoint);

            JDSUtility.debug(
              "[PBFTServer:updateState(checkpoint)] s"  + getLocalServerID() +
              ", at time " + getClockValue() + ", updated a entry in "
            + "its log for " + checkpoint
            );

            if(q.complete()){

                JDSUtility.debug(
                  "[PBFTServer:updateState(checkpoint)] s" + getLocalServerID()     +
                  ", at time " + getClockValue() + ", has already complete  a quorum for " +
                  " checkpoint with sequence number (" + checkpoint.getSequenceNumber() + ")."
                );

                return true;
            }

        }
        return false;

    }




    protected Object getReplierID(){
    //    synchronized(this){

            /**
             * Get the local group
             */
            IGroup g = getLocalGroup();

            int range = g.getGroupSize();

            Object sortedReplierID = null;
            /**
             * While a replier has not been selected.
             */
            while(sortedReplierID == null){

                /**
                 * Sort a process
                 */
                int pindex = (int) (Math.random()* range);

                IProcess p = (IProcess) g.getMembers().get(pindex);

                /**
                 * If the selected process isn't the primary and isn't the local replica
                 * then it'll be selected.
                 */
                if(!isPrimary(p) && !p.getID().equals(getLocalServerID())){
                    sortedReplierID = p.getID();
                }
            }
            return sortedReplierID;
      //  }
    }



 /*########################################################################
  # 6. Methods for handling state transferring messages (a) metadata fetch.
  #########################################################################*/
    
    public void handle(PBFTMetaData receivedMD) {
        if(canProceed(receivedMD)){
            if(updateState(receivedMD)){
                /** 
                 * update the partition table with all received and certified
                 * subpartitions.
                 */
                updateParttable(receivedMD.getSubparts());
                try{
                    doStepNextStateTransfer(receivedMD.getReplicaID(), null);
                }catch(Exception except){
                    except.printStackTrace();
                }
                
            }//end received meta-data is certificated
        }//end received meta-data is validated
    }//end handle received meta-data

    Parttable transferring = new Parttable();
    Parttable  transferred = new Parttable();

    public void doStepNextStateTransfer(Object replierID, Long lrectransfer) throws Exception{
        int LEVELS = rStateManager.getParttreeLevels();
        int ORDER  = rStateManager.getParttreeOrder();

        /* if is a valid recod id*/
        if(lrectransfer != null && lrectransfer >= 0){
            /* move part from in transferring state to transferred state */
            PartEntry transferredPart = transferring.remove(lrectransfer);
            if(transferredPart != null){
                transferred.put(lrectransfer, transferredPart);
            }
        }


        /* get the part with the maximum record index (this is, in depthest level)*/
        PartEntry part = getPartWithMaximumPartindex(transferring);

        /* if still exists a part */
        while(part != null){
            long lpart = part.getPartLevel();
            long ipart = part.getPartIndex();
            long cpart = part.getPartCheckpoint();

            /* if the part is a leaf the fetch the data */
            if(PartTree.isPage(LEVELS, lpart)){
                //Object replierID = receivedMD.getReplicaID();
                emitFetch(lpart, ipart, cpart, replierID);
                return;
            }else{/*else check the part*/
                try{
                    String dpart = PartTree.subpartsDigest(transferred, part, ORDER, LEVELS);
                    /* if the part is valid then it'll be moved from transferring to transferred state */
                    if(part.getDigest().equals(dpart)){
                        lrectransfer = PartTree.getRecordindex(ORDER, lpart, ipart);
                        if(lrectransfer != null && lrectransfer >= 0){
                            transferring.remove(lrectransfer);
                            transferred.put(lrectransfer, part);
                        }
                    }else{
                        /* else this replica will select another reply and 
                         retrive the part and its subparts */
                        emitFetch(lpart, ipart, cpart, getReplierID());
                        return;
                    }
                    
                }catch(Exception except){
                    except.printStackTrace();
                }
            }
            /*get next*/
            part = getPartWithMaximumPartindex(transferring);
        }

        /* insert the transferred parts into the parttree */
        ArrayList<Long> recids = new ArrayList(transferred.keySet());
        for(Long recid : recids){
            part = transferred.get(recid);
            rStateManager.put(recid, part);
            transferred.remove(recid);
        }

        doRollback();
    }
    
    public void doRollback() throws Exception{
        /* rollback to last stable checkpoint */
        rStateManager.rollback();

        /* gets the last stable checkpoint, or zero if it doesn't exist */
        long seqn = rStateManager.getCurrentCheckpointID();

        IRecoverableServer lServer = (IRecoverableServer)getServer();

        if(seqn >= 0){ //tuple!=null
            getStateLog().setNextPrePrepareSEQ(seqn+1);
            getStateLog().setNextPrepareSEQ(seqn+1);
            getStateLog().setNextCommitSEQ(seqn+1);
            getStateLog().setNextExecuteSEQ(seqn+1);

            getStateLog().setCheckpointLowWaterMark(seqn);

            lServer.setCurrentState(rStateManager.getCurrentState());
            updateCurrentSequenceNumber(seqn);
        }
        
    }
    
    public void updateParttable(PartList parts){

        int order  = rStateManager.getParttreeOrder();
        for(int p = 0; p < parts.size(); p++){

            PartEntry part = parts.get(p);
            long ipart = part.getPartIndex();
            long lpart = part.getPartLevel();
            long recid = PartTree.getRecordindex(order, lpart, ipart);
            transferring.put(recid, part);
            
        }//end for each part in parts
        
    }//end updateParttable(parts)

    public PartEntry getPartWithMaximumPartindex(Parttable parttable) throws Exception{

        if(!parttable.isEmpty()){
            ArrayList<Long> irecords = new ArrayList<Long>();
            irecords.addAll(parttable.keySet());

            Collections.sort(irecords);

            int last = irecords.size() - 1;

            return parttable.get(irecords.get(last));
        }

        return null;
    }
    
    public PBFTMetaData createMetaDataMessage(long lpart, long ipart, long checkpointID, PartList subparts){
        PBFTMetaData md = new PBFTMetaData(checkpointID, lpart, ipart, getLocalServerID());
        md.setSubparts(subparts);
        return md;
    }
    
    public void handle(PBFTFetch f){
        
        JDSUtility.debug(
           "[PBFTServer:handle(fetch)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + f
        );
        
        if(canProceed(f)){
            long rlcSEQ = f.getLastStableCheckpointSequenceNumber();
            long cpart = f.getPartCheckpoint();

            long lpart = f.getPartLevel();
            long ipart = f.getPartitionIndex();
            int LEVELS = rStateManager.getParttreeLevels();
            
            Object rid = f.getReplicaID();
            //IStore store = getCheckpointStore();
            try {
                
                PartList subparts = null;
                PartEntry part = rStateManager.getPart(lpart, ipart);
                BaseProcess rServer = new BaseProcess(rid);
                /* store the paramenters of the partition */
                if(part != null && part.getPartLevel() == lpart && part.getPartIndex() == ipart){

                    cpart = part.getPartCheckpoint();

                    if(PartTree.isPage(LEVELS, part)){
                        /**
                         * the recovarable state manager has been prepared to
                         * work with persisitent data storage (for example see
                         * <B>getDataStorage</B>), however the pbft code hasn't
                         * been prepared yet. This implementantion of the pbft
                         * only works with volatile data storage in object
                         * storage. (Alirio Sá)
                         */
                        IMemory mem = rStateManager.getObjecStorage();
                        long ipage = part.getPartIndex();
                        IPage page = mem.readPage(part.getPartIndex());

                        emit(createDataMessage(ipage, page), rServer);

                        mem.release();
                                                
                    }else{
                        
                        /* Gets all subparts of the current part */
                        subparts = rStateManager.getFamily(lpart, ipart, rlcSEQ);

                        /* if there is parts that match with the specified select criteria */
                        if(subparts != null && !subparts.isEmpty()){
                            PBFTMetaData md = createMetaDataMessage(lpart, ipart, cpart, subparts);
                            emit(md, rServer);
                        }//end if exist subparts
                    }//end if is leaf part
                }//end if part was found
            } catch (Exception ex) {
                Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }
    }
    
    public boolean canProceed(PBFTFetch f){
        if(!wasSentByAGroupMember(f)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(fetch)] s" + getLocalServerID() + ", " +
              "at time " + getClockValue() + ", discarded " + f + " " +
              "because it wasn't sent by a member of the group " + getLocalGroup()
            );

            return false;
        }

        long wcwm = f.getPartCheckpoint();
        long lcwm = getStateLog().getCheckpointLowWaterMark();

        Object replier = f.getSelectedReplierID();

        if(lcwm >= wcwm){
            if(replier != null && getLocalServerID().equals(replier)){
                return true;
            }

            if(replier == null){
                return true;
            }
        }
        return false;
    }

    public void emitFetch(long lpart, long ipart, long cpart, Object replierID){
        emit(
            createFetchMessage(lpart, ipart, cpart, replierID),
            new BaseProcess(replierID)
        );
    }
    
    public void emitFetch(){
        emit(
            createFetchMessage(),
            getLocalGroup().minus(new BaseProcess(getLocalServerID()))
        );
    }

    /* Use it when we don't know the part that must be fetched and we don't know the replier */
    public PBFTFetch createFetchMessage(){
        return createFetchMessage(null);
    }

    /* Use it when we don't know the part that must be fetched but we know the replier */
    public PBFTFetch createFetchMessage(Object replierID){
        long lpart = 0, ipart = 0, cpart = -1;
        return createFetchMessage(lpart, ipart, cpart, replierID);
    }

    /*Use it when we know the exatly part that must be fetched but we know the replier */
    public PBFTFetch createFetchMessage(long lpart, long ipart, long cpart, Object replierID){
        long lcwm = getCheckpointLowWaterMark();
        PBFTFetch f = new PBFTFetch(lpart, ipart, cpart, lcwm, replierID, getLocalServerID());

        return f;

    }

    public void handle(PBFTData d){
        
        JDSUtility.debug(
         "[PBFTServer:handle(data)] s" + getLocalServerID() + ", " +
         "at time " + getClockValue() + ", " + "received " + d + "."
        );

        if(canProceed(d)){
                        
            try{
                int  ORDER = rStateManager.getParttreeOrder();
                int LEVELS = rStateManager.getParttreeLevels();

                IPage page = d.getPage();
                long ipage = d.getPageIndex();
                long ipart = ipage;

                long recid = PartTree.getRecordindex(ORDER, LEVELS, ipart);

                PartEntry part = transferring.get(recid);

                long lpart = part.getPartLevel();
                long cpart = part.getPartCheckpoint();

                String cdigest = PartTree.leafPartDigest(ipart, cpart , page);

                /**
                 * if the partDigest of the partition match with the data partDigest,
                 * the it will processing the recovery of the page.
                 */
                if(part.getDigest().equals(cdigest)){

                    IMemory mem = rStateManager.getObjecStorage();                                        
                    mem.writePage(page);
                    mem.release();
                    doStepNextStateTransfer(d.getReplicaID(), recid);
                }else{ 
                    /**
                     * else it will select another replier and fetch the missed 
                     * page data.
                     */
                    emitFetch(lpart, ipart, cpart, getReplierID());
                }
                                             
            }catch(Exception except){
                except.printStackTrace();
            }            
        }
    }

    public boolean canProceed(PBFTData d){
        int  ORDER = rStateManager.getParttreeOrder();
        int LEVELS = rStateManager.getParttreeLevels();
        
        long ipage = d.getPageIndex();
        long recid = PartTree.getRecordindex(ORDER, LEVELS, ipage);

        if(!transferring.containsKey(recid)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(data)] s"   + getLocalServerID()   +
              ", at time " + getClockValue() + ", discarded " + d +
              " because it there isn't in temporary part table."
            );

            return false;
        }

        return true;
    }


    public boolean canProceed(PBFTMetaData md){
        if(!wasSentByAGroupMember(md)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(metadata)] s"   + getLocalServerID()   +
              ", at time " + getClockValue() + ", discarded " + md      +
              " because it wasn't sent by a member of the group " + getLocalGroup()
            );

            return false;
        }
        
        long lcwm = getCheckpointLowWaterMark();
        long rcmd = md.getCheckpoint();

        if(rcmd <  lcwm){
            JDSUtility.debug(
              "[PBFTServer:canProceed(metadata)] s" + getLocalServerID() + ", at " +
              "time " + getClockValue() + ", discarded " + md + " because it has " +
              "a LCWM (" + rcmd + ")" + "< s" + getLocalServerID() +":LCWM(" + lcwm + ")."
            );

            return false;
        }

        return true;

    }

    public boolean updateState(PBFTMetaData md){

//        synchronized(this){
            /**
             * If the preprepare is null then do nothing.
             */
            if(md == null) return false;

            /*
             * Get composite key of the prepare.
             */

            Long entryKey = md.getSequenceNumber();

            /**
             * If the entry key is diferent of null then update state. Otherwise do
             * nothing.
             */
            if(entryKey != null) {

                Quorum quorum = getStateLog().getQuorum(METADATAQUORUMSTORE, entryKey.toString());

                if(quorum == null){

                    int f = getServiceBFTResilience();

                    quorum = new Quorum(2 * f + 1);

                    getStateLog().getQuorumTable(METADATAQUORUMSTORE).put(entryKey.toString(), quorum);
                }

                for(IMessage m : quorum){

                    PBFTMetaData certifiedMD = (PBFTMetaData) m;
                    if(!(
                        certifiedMD.getCheckpoint() == md.getCheckpoint() &&
                        certifiedMD.getSubparts().equals(md.getSubparts())
                    )){
                        JDSUtility.debug(
                          "[PBFTServer:updateState(metadata)] s"  + getLocalServerID() +
                          ", at time " + getClock().value() + ", couldn't use " + md +
                          " because it doesn't match with " + certifiedMD +  "."
                        );

                        return false;
                    }

                    if(certifiedMD.getReplicaID().equals(md.getReplicaID())){
                        JDSUtility.debug(
                          "[PBFTServer:updateState(metadata)] s"  + getLocalServerID() +
                          ", at time " + getClockValue() + ", can't use " + md +
                          " to update its log because it's a duplicated metada."
                        );
                        return false;
                    }
                }

                quorum.add(md);

                JDSUtility.debug(
                  "[PBFTServer:updateState(metadata)] s"  + getLocalServerID() +
                  ", at time " + getClockValue() + ", updated a entry in "
                + "its log for " + md
                );

                if(quorum.complete()){

                    JDSUtility.debug(
                      "[PBFTServer:updateState(metadata)] s" + getLocalServerID()     +
                      ", at time " + getClockValue() + ", has already complete  a quorum for " +
                      " metada with LCWM (" + md.getSequenceNumber() + ")."
                    );

                    return true;
                }

            }
  //      }
        return false;
    }

    public PBFTData createDataMessage(long ipage, IPage page){
        return new PBFTData(ipage, page, getLocalServerID());
    }

   /*########################################################################
     # 7. Methods for handling bag of messages.
     #########################################################################*/

    public void handle(PBFTBag bag){

        JDSUtility.debug(
           "[PBFTServer:handle(bag)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + bag
        );
        if(canProceed(bag)){
            if(updateState(bag)){

                Long entryKey = bag.getSequenceNumber();

                Quorum q = getStateLog().getQuorum(BAGQUORUMSTORE, entryKey.toString());
                MessageCollection preprepareList = new MessageCollection();
                MessageCollection prepareList = new MessageCollection();
                MessageCollection commitList = new MessageCollection();
                MessageCollection checkpointList = new MessageCollection();

                for(IMessage m : q){
                    PBFTBag b1 = (PBFTBag)m;
                    for(IMessage m1 : b1.getMessages()){
                        if(m1 instanceof PBFTPrePrepare) preprepareList.add(m1);

                        if(m1 instanceof PBFTPrepare)  prepareList.add(m1);

                        if(m1 instanceof PBFTCommit) commitList.add(m1);

                        if(m1 instanceof PBFTCheckpoint) checkpointList.add(m1);
                    }
                }
                
                PBFTServerMessageSequenceNumberComparator comparator =
                        new PBFTServerMessageSequenceNumberComparator();

                Collections.sort(preprepareList, comparator);
                Collections.sort(prepareList, comparator);
                Collections.sort(commitList, comparator);
                Collections.sort(checkpointList, comparator);

                for(IMessage m : preprepareList){
                    PBFTPrePrepare m1 = (PBFTPrePrepare)m;
                    long nextSEQ = getStateLog().getNextPrePrepareSEQ();
                    long currSEQ = m1.getSequenceNumber();
                    if(currSEQ == nextSEQ) handle(m1);
                }
                
                for(IMessage m : prepareList){
                    PBFTPrepare m1 = (PBFTPrepare)m;
                    long nextSEQ = getStateLog().getNextPrePrepareSEQ();
                    long currSEQ = m1.getSequenceNumber();
                    if(currSEQ <= nextSEQ) handle(m1);
                }
                for(IMessage m : commitList){
                    PBFTCommit m1 = (PBFTCommit)m;
                    long nextSEQ = getStateLog().getNextPrepareSEQ();
                    long currSEQ = m1.getSequenceNumber();
                    if(currSEQ <= nextSEQ) handle(m1);
                }
                
                for(IMessage m : checkpointList){
                    PBFTCheckpoint m1 = (PBFTCheckpoint)m;
                    long lastSEQ = getStateLog().getCheckpointLowWaterMark();
                    long currSEQ = m1.getSequenceNumber();
                    if(currSEQ > lastSEQ) handle(m1);
                }                
            }
        }
    }

    public boolean canProceed(PBFTBag bag){
        /**
         * If the preprepare message wasn't sent by a group member then it will
         * be discarded.
         */
        if(!wasSentByAGroupMember(bag)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(bag)] s"   + getLocalServerID() +  ", at " +
              "time " + getClockValue() + ", discarded " + bag +" because it " +
              "wasn't sent by a member of the group " + getLocalGroup()
            );

            return false;
        }

        return true;
    }

    /**
     * Update the state of the PBFT. Insert the pre-prepare message in
     * the log entry.
     * @param m
     */
    public boolean updateState(PBFTBag bag){
        /* If the preprepare is null then do nothing.*/
        if(bag == null) return false;

        /* Get composite key of the prepare. */
        Long entryKey = bag.getSequenceNumber();

        /* If the entry key is not null then it'll update state. */
        if(entryKey != null) {
            Quorum quorum = getStateLog().getQuorum(BAGQUORUMSTORE, entryKey.toString());
            if(quorum == null){
                int f = getServiceBFTResilience();
                quorum = new Quorum(2 * f + 1);
                getStateLog().getQuorumTable(BAGQUORUMSTORE).put(entryKey.toString(), quorum);
            }

            for(IMessage m : quorum){
                PBFTBag certifiedBAG = (PBFTBag) m;
                if(certifiedBAG.getReplicaID().equals(bag.getReplicaID())){
                    JDSUtility.debug(
                      "[PBFTServer:updateState(bag)] s"  + getLocalServerID() +
                      ", at time " + getClockValue() + ", cann't use " + bag +
                      " to update its log because it's a duplicated bag."
                    );
                    return false;
                }
            }

            quorum.add(bag);

            JDSUtility.debug(
              "[PBFTServer:updateState(bag)] s"  + getLocalServerID() +
              ", at time " + getClockValue() + ", updated a entry in "
            + "its log for " + bag
            );

            if(quorum.complete()){

                JDSUtility.debug(
                  "[PBFTServer:updateState(bag] s" + getLocalServerID()     +
                  ", at time " + getClockValue() + ", has already complete  a quorum for " +
                  " bag with EXEC-SEQ (" + bag.getSequenceNumber() + ")."
                );

                return true;
            }
        }
        return false;
    }

   /*########################################################################
     # 8. Methods for handling status-active messages.
     #########################################################################*/

    public void handle(PBFTStatusActive sa){
        long lexcSEQ = getStateLog().getNextExecuteSEQ()    -1;
        long lcmtSEQ = getStateLog().getNextCommitSEQ()     -1;
        long lpreSEQ = getStateLog().getNextPrepareSEQ()    -1;
        long lpprSEQ = getStateLog().getNextPrePrepareSEQ() -1;
        long llcwSEQ = getCheckpointLowWaterMark();

        JDSUtility.debug(
           "[PBFTServer:handle(statusactive)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + sa + " current " + 
           "(LCWM = " + llcwSEQ + "; PPSEQ = " + lpprSEQ + "; PSEQ = " + lpreSEQ + "; " +
           "CSEQ = " + lcmtSEQ + "; ESEQ = " + lexcSEQ + ")"
        );

        if(canProceed(sa)){
            
            Long maxSEQ = getStateLog().getNextPrePrepareSEQ();
            Long minSEQ = getStateLog().getNextExecuteSEQ()-1;
            
            //remote active state variables
            long rexcSEQ = sa.getLastExecutedSEQ();         
            long rcmtSEQ = sa.getLastCommittedSEQ();        
            long rpreSEQ = sa.getLastPreparedSEQ();         
            long rpprSEQ = sa.getLastPrePreparedSEQ();      
            long rlcwSEQ = sa.getLastStableCheckpointSEQ();

            if(minSEQ > rexcSEQ) minSEQ = rexcSEQ; if(maxSEQ < rexcSEQ) maxSEQ = rexcSEQ;
            if(minSEQ > rcmtSEQ) minSEQ = rcmtSEQ; if(maxSEQ < rcmtSEQ) maxSEQ = rcmtSEQ;
            if(minSEQ > rpreSEQ) minSEQ = rpreSEQ; if(maxSEQ < rpreSEQ) maxSEQ = rpreSEQ;
            if(minSEQ > rpprSEQ) minSEQ = rpprSEQ; if(maxSEQ < rpprSEQ) maxSEQ = rpprSEQ;
            if(minSEQ > rlcwSEQ) minSEQ = rlcwSEQ; if(maxSEQ < rlcwSEQ) maxSEQ = rlcwSEQ;
            
            if(maxSEQ < 0) maxSEQ = 0L;

            long lhcwSEQ = getCheckpointHighWaterMark();
            
            PBFTBag bag = new PBFTBag(getLocalServerID());

            bag.setSequenceNumber(rexcSEQ);
            
            if(llcwSEQ <= rpprSEQ ){
                for(long i = minSEQ; i < maxSEQ; i++){

                    PBFTLogEntry entry = getStateLog().get(i);
                    if(entry != null){
                        Quorum pq = entry.getPrepareQuorum();
                        Quorum cq = entry.getCommitQuorum();


                        if(rpreSEQ < i && rpprSEQ < i && isPrimary() && entry.getPrePrepare() != null && lpprSEQ >= i){
                            PBFTPrePrepare pp = entry.getPrePrepare();
                            bag.addMessage(pp);
                            //emit(pp);
    //                        sent = sent || true;
                        }

                        if(rpreSEQ < i && pq != null && lpprSEQ >= i){
                            Quorum q = new Quorum();
                            q.addAll(pq);
                            if(!q.isEmpty()){

                                for(IMessage m : q){

                                    PBFTPrepare p = (PBFTPrepare)m;

                                    if(p.getReplicaID().equals(getLocalServerID())){
                                        bag.addMessage(p);
                                        //emit(p);
    //                                    sent = sent || true;
                                    }
                                }
                            }
                        }

                        if(rlcwSEQ < i && rcmtSEQ < i && cq != null && lcmtSEQ >=i){

                            Quorum q = new Quorum();
                            q.addAll(cq);

                            if(!q.isEmpty()){
                                for(IMessage m : q){

                                    PBFTCommit c = (PBFTCommit)m;

                                    if(c.getReplicaID().equals(getLocalServerID())){
                                        bag.addMessage(c);
                                        //emit(c);
    //                                    sent = sent || true;
                                    }
                                }
                            }
                        }
                    }//end if entry
                }//end for
            }
            
            long currSEQ = rlcwSEQ + 1;
//            IStore store = getCheckpointStore();
            
            if(currSEQ < llcwSEQ){
                try {
                    CheckpointLogEntry clogEntry = rStateManager.getBiggestLogEntry();
                    if(clogEntry != null){
                        long seqn = clogEntry.getCheckpointID();
                        String digest = clogEntry.getDigest();
                        PBFTCheckpoint checkpoint = new PBFTCheckpoint(seqn, digest, getLocalServerID());
                        bag.addMessage(checkpoint);
                    }

                } catch (Exception ex) {
                    Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                }
            }//end while currSEQ < _lwSEQ, if currSEQ_0 = lwSEQ
            
            if(!bag.isEmpty()){
                emit(bag, new BaseProcess(sa.getReplicaID()));
            }            
        }
    }

    public boolean canProceed(PBFTStatusActive sa){
        IProcess rServer = new BaseProcess(sa.getReplicaID());
        if(getLocalServerID().equals(rServer.getID())){
            JDSUtility.debug(
              "[PBFTServer:canProceed(activeStatus)] s" + getLocalServerID() + ", at time " + getClockValue() + ", " +
              "discarded " + sa + " because it was sent by the local replica."
            );
            return false;
        }

        if(!checkViewNumber(sa)){
            long nxPP = getStateLog().getNextPrePrepareSEQ();
            long nxPR  = getStateLog().getNextPrepareSEQ();
            long nxCM  = getStateLog().getNextCommitSEQ();
            long nxEX  = getStateLog().getNextExecuteSEQ();
            long lcwm  = getCheckpointLowWaterMark();

            JDSUtility.debug(
              "[PBFTServer:canProceed(activeStatus)] s" + getLocalServerID() + ", at " +
              "time " + getClockValue() + ", " + "discarded " + sa + " because it " +
              "hasn't a valid view number. (viewn = " + getCurrentViewNumber() + ") " +
              "[PP = " + nxPP + ", PR = " + nxPR + ", CM =" + nxCM + " , EX = " + nxEX + ", LCWM = " + lcwm + "]"
            );
            
            return false;
        }

        if(!wasSentByAGroupMember(sa)){
            JDSUtility.debug(
              "[PBFTServer:canProceed(activeStatus)] s" + getLocalServerID() + ", at time " + getClockValue() + ", " +
              "discarded " + sa + " because it wasn't sent by a member of the group " + getLocalGroup() + "."
            );

            return false;
        }

        return getStateLog().getNextPrepareSEQ() >=0;
    }

    PBFTTimeoutDetector periodStatusTimer = null;

   public void schedulePeriodicStatusSend() {

       if(periodStatusTimer == null){
            periodStatusTimer = new PBFTTimeoutDetector() {

                    @Override
                    public void onTimeout() {
//                        long nextPP = getStateLog().getNextPrePrepareSEQ();
//                        long nextP  = getStateLog().getNextPrepareSEQ();
//                        long nextC  = getStateLog().getNextCommitSEQ();

//                        if(nextP < nextPP || nextC < nextP){

                            emit(
                                    createStatusActiveMessage(),
                                    getLocalGroup().minus(getLocalProcess())
                            );
  //                      }

                        schedulePeriodicStatusSend();

                    }
            };

//            if(!getLocalGroup().getMembers().isEmpty()){
//                emit(
//                        createStatusActiveMessage(),
//                        getLocalGroup().minus(getLocalProcess())
//                );
//           }

        }

        long timestamp = getClockValue();
        long period = getSendStatusPeriod();
        long timeout = timestamp + period;

        getScheduler().schedule(periodStatusTimer, timeout);

   }

    public PBFTStatusActive createStatusActiveMessage(){

        long prepreparedSEQ = getStateLog().getNextPrePrepareSEQ() - 1L;
        long preparedSEQ = getStateLog().getNextPrepareSEQ() - 1L;
        long committedSEQ = getStateLog().getNextCommitSEQ()  - 1L;
        long executedSEQ = getStateLog().getNextExecuteSEQ() - 1L;
        long lowCheckpointWaterMark = getCheckpointLowWaterMark();

        return new PBFTStatusActive(
                        getLocalServerID(),
                        getCurrentViewNumber(),
                        prepreparedSEQ,
                        preparedSEQ,
                        committedSEQ,
                        executedSEQ,
                        lowCheckpointWaterMark
        );

    }

   /*########################################################################
     # 9. Execute sequence number.
     #########################################################################*/

    public void handle(PBFTProcessingToken proctoken){
        JDSUtility.debug(
           "[PBFTSever:handle(token)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + proctoken
        );

        if(canProceed(proctoken)){
            long startSEQ = getStateLog().getNextExecuteSEQ();
            long finalSEQ = proctoken.getSequenceNumber();

            for(long currSEQ = startSEQ; currSEQ <= finalSEQ; currSEQ ++){
                Quorum cq = getStateLog().getCommitQuorum(currSEQ);

                if(!(cq != null && cq.complete())){
                    return;
                }
                
                IRecoverableServer lServer = (IRecoverableServer)getServer();

//            currSEQ = proctoken.getSequenceNumber();
                PBFTPrePrepare preprepare = getStateLog().getPrePrepare(currSEQ);

                for(String digest : preprepare.getDigests()){

                    StatedPBFTRequestMessage statedReq = getStateLog().getStatedRequest(digest);

                    PBFTRequest request = statedReq.getRequest();
                    
                    IPayload result = lServer.executeCommand(request.getPayload());

                    PBFTReply reply = createReplyMessage(request, result);

                    statedReq.setState(StatedPBFTRequestMessage.RequestState.SERVED);
                    statedReq.setReply(reply);

                    JDSUtility.debug(
                      "[PBFTServer:handle(token)] s"  + getLocalServerID() + ", " +
                      "at time " + getClockValue() + ", executed " + request +
                      " (currView = " + getCurrentViewNumber() + " / SEQ = " + currSEQ + ")."
                    );
                    
                    JDSUtility.debug(
                      "[PBFTServer:handle(token)] s"  + getLocalServerID() + ", " +
                      "at time " + getClockValue() + ", has the following state " + lServer.getCurrentState()
                    );

                    if(getStateLog().getCommitQuorum(currSEQ).isNOP(getLocalServerID())){
                        if(!getStateLog().get(currSEQ).isNOP()){
                            //if(!getStateLog().getCommitQuorum(currSEQ).isNOP()){
                                IProcess client = new BaseProcess(reply.getClientID());
                                emit(reply, client);
                            //}
                        }
                    }

                }//end for each leafPartDigest (handle and reply)

                JDSUtility.debug(
                  "[PBFTServer:handle(token)] s"  + getLocalServerID() + ", " +
                  "at time " + getClockValue() + ", after execute SEQN = "  + currSEQ + " " +
                  "has the following state " + lServer.getCurrentState()
                );

                getStateLog().updateNextExecuteSEQ(currSEQ);

                long execSEQ = getStateLog().getNextExecuteSEQ() -1;
                long chkPeriod = getCheckpointPeriod();

                if(execSEQ > 0 && ((execSEQ % chkPeriod) == 0)){
                    PBFTCheckpoint checkpoint;
                    try {
                        checkpoint = createCheckpointMessage(execSEQ);
                        updateState(checkpoint);
                        rStateManager.addLogEntry(
                           new CheckpointLogEntry(
                                    checkpoint.getSequenceNumber(),
                                    rStateManager.byteArray(),
                                    checkpoint.getDigest()
                               )
                         );
                        emit(checkpoint, getLocalGroup().minus(getLocalProcess()));
                    } catch (Exception ex) {
                        Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                getStateLog().get(currSEQ).setNOP(getLocalServerID());
            }
        }
    }

    public boolean canProceed(PBFTProcessingToken token){
        Long currSEQ = token.getSequenceNumber();

        PBFTLogEntry entry = getStateLog().get(currSEQ);

        /* If it isn't preprepared then it won't proceed */
        if(!(entry != null && entry.getPrePrepare() != null)){
            return false;
        }

        Quorum pq = entry.getPrepareQuorum();
        Quorum cq = entry.getCommitQuorum();

        /* If it isn't prepared and committed then it won't proceed */
        if(pq != null && pq.complete() && cq != null & cq.complete()){

            /*if it was served then it wouldn't proceed */
            if(!getStateLog().wasServed(currSEQ)){
                return true;
            }
        }

        return false;
    }

    public  PBFTReply createReplyMessage(PBFTRequest r, IPayload result){

        return createReplyMessage(r, result, getCurrentViewNumber());

    }

    public PBFTReply createReplyMessage(PBFTRequest r, IPayload result, Integer viewNumber){

  //      synchronized(this){
            PBFTReply reply = new PBFTReply(
                r, result, getLocalServerID(), viewNumber
            );

            return reply;
    //    }

    }

    public PBFTCheckpoint createCheckpointMessage(long seqn) throws Exception{

      //  synchronized(this){
            //IRecoverableServer lserver = (IRecoverableServer) getServer();

            byte[] state = rStateManager.byteArray();

            //IState state = null;//lserver.getCurrentState().copy();

            String digest = getAuthenticator().getDigest(state); //computeStateDigest(state, seqn);

            PBFTCheckpoint c = new PBFTCheckpoint(seqn, digest, getLocalServerID());

//            getStateLog().doCacheServerState(seqn, leafPartDigest, state);

            return c;
        //}
    }

    /**
     * Send a message to a remote object (a process or a group). All emitted
     * message are signed using the defined authenticator. After the protocol
     * signing a message it creates a PDU a use it to carry out the signed message.
     * @param msg -- the message that has to be sent.
     * @param remote -- Can be a instance of a process or a instance of a group.
     */
    public void emit(IMessage msg, ISystemEntity remote){
        //synchronized(this){
        SignedMessage m;

        try {

            m = getAuthenticator().encrypt(msg);

            PDU pdu = new PDU();

            pdu.setDestination(remote);
            pdu.setSource(getLocalProcess());
            pdu.setPayload(m);

            String sent = "sent";
            if(remote instanceof IProcess)
                getCommunicator().unicast(pdu, (IProcess)remote);
            else if(remote instanceof IGroup){
                getCommunicator().multicast(pdu, (IGroup)remote);
                sent = "multicast";
            }

            JDSUtility.debug(
              "[PBFTServer:emit(msg, dest)]s" + getLocalServerID() + " " + sent + " " +
              "" + msg + " at timestamp " + getClockValue() + " to " + remote + "."
            );

        } catch (Exception ex) {
            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        //}
    }

    public PBFTReply createNullReplyMessage(PBFTRequest request){

        //synchronized(this){
        /**
         * It must be corrected.
         */
            return  new PBFTReply(request, null, getLocalServerID(), getCurrentViewNumber());
       // }

    }

  /*########################################################################
   # 11. Methods for handling change-view procedure.
   #########################################################################*/

    public void handle(PBFTChangeView cv) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void handle(PBFTChangeViewACK cva) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void handle(PBFTNewView nwv) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void emitChangeView() {

        int viewn = getCurrentViewNumber();
        
        JDSUtility.debug(
            "[PBFTServer:emitChangeView()] s" + getLocalServerID() + ", at " +
            "time " + getClockValue() + ", is going to emit a change view " +
            "message for (v + 1 = "  +  (viewn + 1) + ")."
        );

        
    }

    public void installNewView() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

  /*########################################################################
   # 11. Utility methods.
   #########################################################################*/
    Hashtable<String, MessageQueue> queuetable = new Hashtable<String, MessageQueue>();
    public MessageQueue getQueue(String name){
        MessageQueue queue = queuetable.get(name);
        if(queue == null){
            queue = new MessageQueue();
            queuetable.put(name, queue);
        }
        return queue;
    }
    public long getSendStatusPeriod() {return sendStatusPeriod;}

    public void setSendStatusPeriod(long sendStatusPeriod) {this.sendStatusPeriod = sendStatusPeriod;}

    protected  Long primaryFaultTimeout = null;

    public Long getPrimaryFaultTimeout(){return primaryFaultTimeout;}

    public void setPrimaryFaultTimeout(Long timeout){primaryFaultTimeout = timeout;}

    public long getChangeViewRetransmissionTimeout() {return changeViewRetransmissionTimeout;}

    public void setChangeViewRetransmissionTimeout(long changeViewRetransmissionTimeout) {
        this.changeViewRetransmissionTimeout = changeViewRetransmissionTimeout;
    }

    public long getCheckpointPeriod() {return checkpointPeriod;}

    public  void setCheckpointPeriod(long checkpointPeriod) {this.checkpointPeriod = checkpointPeriod;}

    public Integer getCurrentViewNumber() {return currentViewNumber;}

    public void setCurrentViewNumber(Integer currentViewNumber) {this.currentViewNumber = currentViewNumber;}

    public boolean isLocalServer(IProcess process){
        return process != null && process.getID().equals(getLocalServerID());
    }
    public boolean isPrimary(){return isPrimary(getLocalProcess());}

    public boolean isPrimary(IProcess p){return isPrimary(p.getID());}

    public boolean isPrimary(Object serverID){return getCurrentPrimaryID().equals(serverID);}

    public Object getLocalServerID(){return getLocalProcessID();}

    public Object getCurrentPrimaryID(){return currentPrimaryID;}

    public  void setCurrentPrimaryID(Object serverID){currentPrimaryID = serverID;}
    
    public synchronized static long newSequenceNumber(){return ++SEQ;}

    public static long getCurrentSequenceNumber(){return SEQ;}

    public synchronized static void updateCurrentSequenceNumber(long sqn){SEQ = sqn;}

    public Long getBatchingTimeout(){return batchTimeout;}

    public void setBatchTimeout(Long timeout){this.batchTimeout = timeout;}

    public int getBatchSize() {return batchSize;}

    public void setBatchSize(int batchSize) {this.batchSize = batchSize;}

    protected  long rejuvenationWindow;

    public void setRejuvenationWindow(long timeout) {this.rejuvenationWindow  = timeout;}

    public long getRejuvenationWindow() {return this.rejuvenationWindow;}

    public void setServer(IServer server) {this.server = server;}

    public IServer getServer(){return this.server;}

    protected  PBFTClientSessionTable clientSessionTable = new PBFTClientSessionTable();

    public boolean isNextRequest(PBFTRequest current){return clientSessionTable.isNext(current);}

    public void updateClientSession(PBFTRequest r){clientSessionTable.updateClientSession(r);}

    /**
     * Checks if a message has a valid sequence number, this is: the sequence
     * number doesn't has holes and is in a valid range.
     * @param m -- the message.
     * @return -- true if the message has a valid sequence number.
     */
    public boolean checkSequenceNumber(PBFTServerMessage m) {

        return isOrdered(m) && inAValidSequenceRange(m);

    }

    /**
     * Check if a message insert a hole in the sequence numbers.
     * @param m -- the message.
     * @return -- true if the message inserts a hole in the sequence numbers.
     */
    protected boolean isOrdered(PBFTServerMessage m){

        long nextPrePrepareSEQ = getStateLog().getNextPrePrepareSEQ();
        long nextPrepareSEQ = getStateLog().getNextPrepareSEQ();
        long nextCommitSEQ = getStateLog().getNextCommitSEQ();

        if(m != null && m.getSequenceNumber() != null){

            long seqn = m.getSequenceNumber();

            if(m instanceof PBFTPrePrepare){
                return seqn == nextPrePrepareSEQ;
            }

            if(m instanceof PBFTPrepare){
                return seqn == nextPrepareSEQ;
            }

            if(m instanceof PBFTCommit){
                return seqn <= nextPrepareSEQ; //nextCommitSEQ;
            }
        }

        return false;

    }

    /**
     * Check if the view number of the message is equal to the current view
     * number.
     * @param m -- the message.
     * @return -- true if the message belongs to the current view.
     */
    public boolean checkViewNumber(PBFTServerMessage m) {

        Object view = m.getViewNumber();

        return getCurrentViewNumber().equals(view);

    }


    /**
     * Check if a message has a sequence number between the low and high water
     * marks defined by the checkpoint.
     * @param m -- the message.
     * @return -- true if the sequence number of the message is in the valid
     * range.
     */
    public boolean inAValidSequenceRange(PBFTServerMessage m){

        long seqn = m.getSequenceNumber();
        long low  = getCheckpointLowWaterMark();
        long high = getCheckpointHighWaterMark();

        return seqn > low && seqn <= high;


    }

    public Long getCheckpointLowWaterMark(){return getStateLog().getCheckpointLowWaterMark();}

     Long checkpointFactor;

    public void setCheckpointFactor(Long factor){checkpointFactor = factor;}
    public long getCheckpointFactor(){return checkpointFactor;}

    public Long getCheckpointHighWaterMark(){
        return getStateLog().getCheckpointHighWaterMark(getCheckpointPeriod(), getCheckpointFactor());
    }

    /**
     * Check if a message was sent by the primary.
     * @param m -- the message.
     * @return true if was sent by the primary.
     */
    public boolean wasSentByPrimary(PBFTServerMessage m){ return isPrimary(m.getReplicaID());}

    public boolean wasSentByAGroupMember(PBFTServerMessage m){
        Object senderID = m.getReplicaID();

        for(int i = 0 ; i < group.getMembers().size(); i ++){

            IProcess p = (IProcess) group.getMembers().get(i);

            if(p.getID().equals(senderID)){
                return true;
            }

        }

        return false;
    }

    AfterEventtable  afterEventtable  = new AfterEventtable();
    BeforeEventtable beforeEventtable = new BeforeEventtable();

    public void addListener(IEventListener listener, Method m){
        
        if(listener instanceof IAfterEventListener){
            IAfterEventListener after = (IAfterEventListener)listener;
            ArrayList<IAfterEventListener> afters = afterEventtable.get(m);
            if(afters == null){
                afters = new ArrayList<IAfterEventListener>();
            }
            afters.add(after);
            afterEventtable.put(m, afters);
        }

        if(listener instanceof IBeforeEventListener){
            IBeforeEventListener before = (IBeforeEventListener)listener;
            ArrayList<IBeforeEventListener> befores = beforeEventtable.get(m);
            if(befores == null){
                befores = new ArrayList<IBeforeEventListener>();
            }
            befores.add(before);
            beforeEventtable.put(m, befores);
        }

    }

    public static IPBFTServer create(){
            PBFTServer pbft = new PBFTServer();
            return (IPBFTServer)EventHandler.newInstance(pbft, pbft.beforeEventtable, pbft.afterEventtable);
    }

    private PBFTServer(){}

    public long getCurrentPrePrepareSEQ() {return getStateLog().getNextPrePrepareSEQ() -1;}

    public long getCurrentExecuteSEQ() { return getStateLog().getNextExecuteSEQ() - 1;}

    public long getCurrentPrepareSEQ() { return getStateLog().getNextPrepareSEQ() - 1;}

    public long getCurrentCommitSEQ() { return getStateLog().getNextCommitSEQ() - 1;}


}