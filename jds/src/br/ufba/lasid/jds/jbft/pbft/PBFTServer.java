/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.hdf.ISupplier;
import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.ISystemEntity;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.cs.IServer;
import br.ufba.lasid.jds.group.IGroup;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBag;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTFetch;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTProcessingToken;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTServerMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import br.ufba.lasid.jds.util.DigestList;
import java.io.IOException;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTLogEntry;
import br.ufba.lasid.jds.util.Debugger;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.jbft.pbft.handlers.PBFTProcessingHandler;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTCheckpointStorage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTCheckpointTable;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTCheckpointTuple;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTClientSessionTable;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTTimeoutDetector;
import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IRecoverableServer;
import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IState;
import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IStore;
import br.ufba.lasid.jds.util.IPayload;
import br.ufba.lasid.jds.util.ITask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdbm.helper.Tuple;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;
import trash.br.ufba.lasid.jds.comm.QuorumTable;

/**
 *
 * @author aliriosa
 */
public class PBFTServer extends PBFT{

    protected long checkpointPeriod  = 256;
    protected long batchTimeout = 0;
    protected long sendStatusPeriod = 1000;
    protected int  batchSize = 50;
    protected long changeViewRetransmissionTimeout = 0;
    protected int currentViewNumber = 0;
    protected Object currentPrimaryID = null;
    protected static long SEQ = -1;
    protected  IServer server;

    public long getSendStatusPeriod() {
        return sendStatusPeriod;
    }

    public void setSendStatusPeriod(long sendStatusPeriod) {
        this.sendStatusPeriod = sendStatusPeriod;
    }

    protected  Long primaryFaultTimeout = null;
    
    public Long getPrimaryFaultTimeout(){
        return primaryFaultTimeout;
    }

    public void setPrimaryFaultTimeout(Long timeout){
        primaryFaultTimeout = timeout;
    }

    public long getChangeViewRetransmissionTimeout() {
        return changeViewRetransmissionTimeout;
    }

    public void setChangeViewRetransmissionTimeout(long changeViewRetransmissionTimeout) {
        this.changeViewRetransmissionTimeout = changeViewRetransmissionTimeout;
    }

    public long getCheckpointPeriod() {
        return checkpointPeriod;
    }

    public  void setCheckpointPeriod(long checkpointPeriod) {
        this.checkpointPeriod = checkpointPeriod;
    }


    /**
     * Get the current view number
     * @return
     */
    public Integer getCurrentViewNumber() {
        return currentViewNumber;
    }

    public void setCurrentViewNumber(Integer currentViewNumber) {
        this.currentViewNumber = currentViewNumber;
    }

    public boolean isLocalServer(IProcess process){
        return process != null && process.getID().equals(getLocalServerID());
    }
    public boolean isPrimary(){
        return isPrimary(getLocalProcess());
    }

    public boolean isPrimary(IProcess p){
        return isPrimary(p.getID());
    }

    public boolean isPrimary(Object serverID){
        return getCurrentPrimaryID().equals(serverID);
    }

    public Object getLocalServerID(){
        return getLocalProcessID();
    }

    public Object getCurrentPrimaryID(){
        return currentPrimaryID;
    }

    public  void setCurrentPrimaryID(Object serverID){
        currentPrimaryID = serverID;
    }


    public synchronized static long newSequenceNumber(){
            return ++SEQ;
    }

    public static long getCurrentSequenceNumber(){
        return SEQ;
    }

    public synchronized static void updateCurrentSequenceNumber(long sqn){
        SEQ = sqn;
    }

    public Long getBatchingTimeout(){
        return batchTimeout;
    }

    public void setBatchTimeout(Long timeout){
        this.batchTimeout = timeout;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    protected  long rejuvenationWindow;

    public void setRejuvenationWindow(long timeout) {
        this.rejuvenationWindow  = timeout;
    }

    public long getRejuvenationWindow() {
        return this.rejuvenationWindow;
    }

    public void setServer(IServer server) {
        this.server = server;
    }

    public IServer getServer(){
        return this.server;
    }

    protected  PBFTClientSessionTable clientSessionTable = new PBFTClientSessionTable();
    
    public boolean isTheNext(PBFTRequest current){
//        synchronized(this){
            return clientSessionTable.isNext(current);
//        }
    }

    public void updateClientSession(PBFTRequest r){
        clientSessionTable.updateClientSession(r);
    }

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
                return seqn == nextCommitSEQ;//nextPrepareSEQ;//nextCommitSEQ;
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

    public Long getCheckpointLowWaterMark(){
        return getStateLog().getCheckpointLowWaterMark();
    }

     Long checkpointFactor;

    public void setCheckpointFactor(Long factor){
        checkpointFactor = factor;
    }
    public long getCheckpointFactor(){
        return checkpointFactor;
    }

    public Long getCheckpointHighWaterMark(){
        return getStateLog().getCheckpointHighWaterMark(getCheckpointPeriod(), getCheckpointFactor());
    }

    /**
     * Check if a message was sent by the primary.
     * @param m -- the message.
     * @return true if was sent by the primary.
     */
    public boolean wasSentByPrimary(PBFTServerMessage m){
        return isPrimary(m.getReplicaID());
    }

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

    /**
     * Update the state of the PBFT. Insert the pre-prepare message in
     * the log entry.
     * @param m
     */
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
                        Debugger.debug(
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

                    Debugger.debug(
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
                            doRevokeViewChange(digest);
                        }

    //                    Debugger.debug(
    //                      "[PBFTServer:handle(preprepare)] s"  + getLocalServerID() +
    //                      ", at time " + getClockValue() + ", revoked the timeout "
    //                    + "for pre-prepare of " + statedRequest.getRequest()
    //                    );

                    }

                    getStateLog().updateNextPrePrepareSEQ(preprepare);

                    return true;
                }

            }
  //      }
            return false;
    

    }

    /**
     * Update the state of the PBFT. Insert the pre-prepare message in
     * the log entry.
     * @param m
     */
    public boolean updateState(PBFTPrepare prepare){
    //    synchronized(this){
            /**
             * If the preprepare is null then do nothing.
             */
            if(prepare == null){

                return false;

            }

            /*
             * Get composite key of the prepare.
             */

            Long entryKey = prepare.getSequenceNumber();

            /**
             * If the entry key is diferent of null then update state. Otherwise do
             * nothing.
             */
            if(entryKey != null) {

                /**
                 * Get the batch in the preprepare.
                 */

                 DigestList digests = prepare.getDigests();

                /**
                 * For each request in batch, check if such request was received.
                 */
                for(String digest : digests){

                    /**
                     * If some request in the bacth wasn't received then the log
                     * state won't be able to be updated.
                     */
                    if(!getStateLog().hasARequestPrePrepared(digest)){

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
                if(entry != null){

                    Quorum q = entry.getPrepareQuorum();

                    if(q == null){

                        int f = getServiceBFTResilience();

                        q = new Quorum(2 * f);

                        entry.setPrepareQuorum(q);

                        Debugger.debug(
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
                            Debugger.debug(
                              "[PBFTServer:updateState(prepare)] s"  + getLocalServerID() +
                              ", at time " + getClockValue() + ", cann't use " + prepare +
                              " to update its log because it's a duplicated prepare."
                            );
                            return false;
                        }
                    }

                    q.add(prepare);

                    Debugger.debug(
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
                        Debugger.debug(
                          "[PBFTServer:accept(prepare)] s" + getLocalServerID()     +
                          ", at time " + getClockValue() + ", has just complete " +
                          "the prepare phase for sequence number (" + seqn + ") and "     +
                          "view number (" + prepare.getViewNumber() + ")."
                        );

                        getStateLog().updateNextPrepareSEQ(prepare);

                        /**
                         * Update the entry in log.
                         */
                        getStateLog().put(entryKey, entry);

                        Debugger.debug(
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

    /**
     * Update the state of the PBFT. Insert the pre-prepare message in
     * the log entry.
     * @param m
     */
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

                        Quorum q = entry.getCommitQuorum();

                        if(q == null){

                            int f = getServiceBFTResilience();

                            q = new Quorum(2 * f + 1);

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
                                Debugger.debug(
                                  "[PBFTServer:updateState(commit)] s"  + getLocalServerID() +
                                  ", at time " + getClockValue() + ", cann't use " + commit +
                                  " to update its log because it's a duplicated commit."
                                );

                                return true;
                            }
                        }

                        q.add(commit);

                        Debugger.debug(
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


                                Debugger.debug(
                                  "[PBFTServer:updateState(commit)] s" + getLocalServerID()     +
                                  ", at time " + getClockValue() + ", committed " + statedReq.getRequest() +
                                  " for processing in view number (" + commit.getViewNumber() + ")."
                                );

                            }

                            getStateLog().updateNextCommitSEQ(commit);

                            Debugger.debug(
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

    /**
     * Update the state of the PBFT. Insert the pre-prepare message in
     * the log entry.
     * @param m
     */
    public boolean updateState(PBFTCheckpoint checkpoint){

//        synchronized(this){
            /**
             * If the preprepare is null then do nothing.
             */
            if(checkpoint == null){

                return false;

            }

            /*
             * Get composite key of the prepare.
             */

            Long entryKey = checkpoint.getSequenceNumber();

            /**
             * If the entry key is diferent of null then update state. Otherwise do
             * nothing.
             */
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
                        return false;
                    }

                    if(c1.getReplicaID().equals(checkpoint.getReplicaID())){
                    Debugger.debug(
                      "[PBFTServer:updateState(checkpoint)] s"  + getLocalProcess().getID() +
                      ", at time " + getClock().value() + ", cann't use " + checkpoint +
                      " to update its log because it's a duplicated prepare."
                    );
                        return false;
                    }
                }

                q.add(checkpoint);

                Debugger.debug(
                  "[PBFTServer:updateState(checkpoint)] s"  + getLocalServerID() +
                  ", at time " + getClockValue() + ", updated a entry in "
                + "its log for " + checkpoint
                );

                if(q.complete()){

                    Debugger.debug(
                      "[PBFTServer:updateState(checkpoint)] s" + getLocalServerID()     +
                      ", at time " + getClockValue() + ", has already complete  a quorum for " +
                      " checkpoint with sequence number (" + checkpoint.getSequenceNumber() + ")."
                    );


                    return true;
                }

            }
  //      }
        return false;

    }



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

                    Debugger.debug(
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

    @Override
    public void startup() {
        try {
            chkStore = new PBFTCheckpointStorage(getLocalServerID());
            Tuple tuple = chkStore.getLast();
            if(tuple != null){
                String index = (String)tuple.getKey();
                String pair[] = index.split(";");

                long seqn = Long.valueOf(pair[0]);

                getStateLog().setNextPrePrepareSEQ(seqn+1);
                getStateLog().setNextPrepareSEQ(seqn+1);
                getStateLog().setNextCommitSEQ(seqn+1);
                getStateLog().setNextExecuteSEQ(seqn+1);

                getStateLog().setCheckpointLowWaterMark(seqn);

                IRecoverableServer srv = (IRecoverableServer)getServer();
                srv.setCurrentState((IState)tuple.getValue());

                updateCurrentSequenceNumber(seqn);
            }
        } catch (IOException ex) {
            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        
        super.startup();

    }

     IStore<String, IState> chkStore;

    public IStore<String, IState> getCheckpointStore() {
        return chkStore;
    }

    public void setCheckpointStore(IStore<String, IState> chkStore) {
        this.chkStore = chkStore;
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
                        PBFTCheckpointTuple ctuple = getStateLog().getCachedState().get(checkpoint.getSequenceNumber());

                        if(!(ctuple != null && ctuple.getDigest().equals(checkpoint.getDigest()))){
                            break;
                        }

                        if(!ctuple.isUpdated()){
                            try {
                                chkStore.write(checkpoint.getEntryKey(), ctuple.getCurrentState(), true);
                                chkStore.commit();
                                lowWater = currSEQ;

                            } catch (IOException ex) {
                                Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                                ex.printStackTrace();
                            }
                            ctuple.setUpdated(true);

                        }
                    }
                }
            }

            if(lowWater > getStateLog().getCheckpointLowWaterMark()){
                getStateLog().setCheckpointLowWaterMark(lowWater);

                Debugger.debug("[PBFTServer:doCheckpoint(seqn)] s" + getLocalServerID() + ", at time " + getClockValue() + ", starts the garbage collection procedure with LCWM = " + lowWater +"...");

                getStateLog().doGarbage();

                Debugger.debug("[PBFTServer:doCheckpoint(seqn)] s" + getLocalServerID() + " complete the garbage collection procedure for LCWM = " + lowWater +"!");
            }else{
                Debugger.debug("[PBFTServer:doCheckpoint(seqn)] s" + getLocalServerID() + ", at time " + getClockValue() + ", does not have cached stated matching LCWM = " + seqn +"...");
            }
        //}
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
    /**
     * Perform the procedures to handle a client request, this is: <BR>
     * <DL>
     *  <LI> check if the request is a valid object.
     *  <LI> check if the request is ordered.
     *  <LI> check if the request has been already accepted.
     *  <LI> check if the request has been already accepted and hasn't in cache
     * anymore.
     *  <LI> check if is a new request and must be handled by current replica
     * (batched by the primary or buffered by the other replicas).
     * </DL>
     * @param request - the client request.
     * @return true if the request was accepted, otherwise false.
     * @see PBFTRequest
     */
    public void handle(PBFTRequest request){
        
        Debugger.debug(
           "[PBFTServer:handle(request)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + request
        );

        if(canProceed(request)){
            Debugger.debug(
               "[PBFTServer:handle(request)] s" +getLocalServerID() + ", "  +
               "at time " + getClockValue() + ", accepted " + request + " " +
               "as a new request."
            );

            String digest = null;

            try{
                digest = getAuthenticator().getDigest(request);

            } catch (Exception ex) {

                Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                return;

            }

            /**
             * If a request is new then it will be accepted and put it in back log
             * state.
             */
            getStateLog().insertRequestInTables(
                digest, request, StatedPBFTRequestMessage.RequestState.WAITING
            );

            Debugger.debug(
              "[PBFTServer:handle(request)] s" + getLocalServerID() + " " +
              "inserted " + request + " in waiting state."
            );

            /**
             * Perform the batch procedure if the server is the primary replica.
             */
            if(isPrimary()){
                Debugger.debug(
                  "[PBFTServer:handle(request)] s" + getLocalServerID() + " (primary)" +
                  " is executing the batch procedure for " + request + "."
                );

                batch(digest);
                return;

            }

            /**
             * Schedule a timeout for the arriving of the pre-prepare message if
             * the server is a secundary replica.
             */
            doScheduleViewChange(digest);

        }
    }

    public boolean canProceed(PBFTRequest request){
//        synchronized(this){
            if(!isTheNext(request)){
                Debugger.debug(
                  "[PBFTServer:canProceed(request)] s" + getLocalServerID() +
                  " didn't accept " + request + " because this is out of order."
                );

                return false;
            }

            IProcess client = new br.ufba.lasid.jds.Process(request.getClientID());

            /**
             * Check if request was already accepted.
             */
            if(getStateLog().wasAccepted(request)){

                /**
                 * Check if request was already served.
                 */
                if(getStateLog().wasServed(request)){
                    Debugger.debug(
                      "[PBFTServer:canProceed(request)] s" + getLocalServerID() +
                      " has already served " + request + "."
                    );

                    /**
                     * Retransmite the reply for a request that had been already
                     * served.
                     */
                    emit(getStateLog().getReplyInRequestTable(request), client);

                    return false;

                }

                Debugger.debug(
                  "[PBFTServer:canProceed(request)] s" + getLocalServerID() +
                  " has already accepted " + request + " so this was discarded."
                );

                return false;

            }

            /**
             * If the reply there is no more in the current state then PBFT2'll send
             * a reply if null payload.
             */
            if(getStateLog().noMore(request)){
                Debugger.debug(
                  "[PBFTServer:canProceed(request)] s" + getLocalServerID() +
                  " hasn't a response for  " + request + " any more."
                );

                emit(createNullReplyMessage(request), client);
                return false;

            }

            updateClientSession(request);
  //      }
        return true;
    }


    protected  DigestList batch = new DigestList();

    protected boolean hasBatch(){
        return batch.size() > 0;
    }
    
    public void batch(String digest){
    //    synchronized(this){
            if(isPrimary()){

                batch.add(digest);

                doScheduleSendBatch(digest);

                if(isACompleteBatch()){
                    emitBatch();
                    return;
                }
            }
      //  }
    }

    protected boolean isACompleteBatch(){
        return batch.size() >= getBatchSize();
    }

    protected void emitBatch(){
        synchronized(this){
            /**
             * If there was batch request.
             */
            if(!batch.isEmpty()){

                /**
                 * Create a new pre-prepare message
                 */
                PBFTPrePrepare preprepare = new PBFTPrePrepare
                (
                        getCurrentViewNumber(),
                        newSequenceNumber(),
                        getLocalServerID()
                );

                preprepare.getDigests().addAll(batch);

                for(String digest: preprepare.getDigests()){
                    doRevokeSendBatch(digest);
                    batch.remove(digest);
                }

                emit(preprepare, getLocalGroup().minus(getLocalProcess()));

                updateState(preprepare);

            }
        }
    }

    public void doRevokeSendBatch(String digest){
        //cancel the timeout task
//        synchronized(this){
            PBFTTimeoutDetector ttask = (PBFTTimeoutDetector)getTaskTable(PBFT.BATCHTASKS).get(digest);

            if(ttask != null){
                getScheduler().cancel(ttask);
                getTaskTable(PBFT.BATCHTASKS).remove(digest);
                Debugger.debug(
                  "[PBFTServer:doRevoke(digest)] s" + getLocalServerID() +
                  " revoked a batch timeout for ("+digest+")."
                );

            }
  //      }
    }
    public void doScheduleSendBatch(String digest){

    //    synchronized(this){
        
            PBFTTimeoutDetector ttask = new PBFTTimeoutDetector() {
                @Override
                public void onTimeout() {

                    Debugger.debug(
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

            Debugger.debug(
              "[PBFTServer:doSchedule(digest)] s" + getLocalServerID() +
              " scheduled a batch timeout for " + timestamp + "("+digest+")."
            );
      //  }
    }
    
    public void doScheduleViewChange(String digest){
        //synchronized(this){
            doScheduleViewChange(digest, getPrimaryFaultTimeout());
        //}

    }

    /**
     * Schedule a view change in case of late response for the primary.
     * @param request - the client request.
     * @param timeout - the view change timeout.
     */
    public void doScheduleViewChange(String digest, long timeout){
        //synchronized(this){
            /**
             * A new timeout task is created.
             */
            PBFTTimeoutDetector ttask = new PBFTTimeoutDetector()
            {
                /**
                 * On the expiration of the timeout, perform a change view.
                 */
                @Override
                public void onTimeout() {

                    long timeout = (Long) get("TIMEOUT");
                    String digest = (String) get("DIGEST");

                    //doRevokeViewChange(digest);

                    StatedPBFTRequestMessage statedRequest = getStateLog().getStatedRequest(digest);

                    if(statedRequest != null){
                        if(statedRequest.getState().equals(StatedPBFTRequestMessage.RequestState.WAITING)){
                            Debugger.debug(
                              "[PBFTServer:doScheduleChangeView::PBFTTimeoutDetector:onTimeout()] s" + getLocalServerID() + " " +
                              ", at time " + getClockValue() + ", is starting a change view (" + statedRequest.getDigest() +")."
                            );
                            //doNewView();
                            //doScheduleViewChange(digest, 2 * timeout);
                        }//endif request is waiting
                    }

                }

            };

            ttask.put("TIMEOUT", timeout);
            ttask.put("DIGEST", digest);

            getTaskTable(PBFT.REQUESTTASKS).put(digest, ttask);

            long timestamp = getClockValue() + timeout;

            getScheduler().schedule(ttask, timestamp);

            Debugger.debug(
              "[PBFTServer:doScheduleChangeView(digest, timeout)] s" + getLocalServerID() + ", " +
              "at time " + getClockValue() + ", scheduled a new view procedure for " +
              "timestamp " + timestamp + "(" + digest + ")."
            );
        //}
    }

    /**
     * revoke the timer assigned to a client request (i.e. the change view timer).
     * @param digest
     */
    public void doRevokeViewChange(String digest){

//        synchronized(this){
            PBFTTimeoutDetector timeoutTask = (PBFTTimeoutDetector)getTaskTable(PBFT.REQUESTTASKS).get(digest);

            if(timeoutTask != null){
                getScheduler().cancel(timeoutTask);
                getTaskTable(PBFT.REQUESTTASKS).remove(digest);

                Debugger.debug(
                  "[PBFTServer:doRevokeViewChange()] s" + getLocalServerID() + ", at " +
                  "time " + getClockValue() +", revoked a change view timeout (" + digest + ")."
                );

            }
  //      }
    }

    /*########################################################################
     # 2. Methods for handling pre-prepare messages.
     #########################################################################*/

    /**
     * Perform the procedures to handle a pre-prepare message, this is: <BR>
     * <DL>
     *  <LI> check if the pre-prepare has a valid sequence/view number.
     *  <LI> check if the pre-prepare was sent by the primary replica.
     *  <LI> if is a valid request then the server state will be updated and a
     * prepare message will be sent (if the replica isn't the primary).
     * @param preprepare - the preprepare message.
     * @return true if the preprepare was accepted, otherwise false.
     * @see PBFTPrePrepare
     */
    public void handle(PBFTPrePrepare preprepare){
        Debugger.debug(
           "[PBFTServer:handle(preprepare)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + preprepare
        );

        if(canProceed(preprepare)){

            if(updateState(preprepare)){
                if(!isPrimary()){

                    PBFTPrepare prepare = createPrepareMessage(preprepare);

                    emit(prepare, getLocalGroup().minus(getLocalProcess()));

                    updateState(prepare);
                }
            }
        }

    }

    public boolean canProceed(PBFTPrePrepare preprepare){
    //    synchronized(this){
            /**
             * If the preprepare hasn't a valid sequence or view number then force a
             * change view.
             */
            if(!(checkSequenceNumber(preprepare) && checkViewNumber(preprepare))){
                long nextPP = getStateLog().getNextPrePrepareSEQ();
                long nextP  = getStateLog().getNextPrepareSEQ();
                long nextC  = getStateLog().getNextCommitSEQ();
                long nextE  = getStateLog().getNextExecuteSEQ();

                Debugger.debug(
                  "[PBFTServer:canProceed(preprepare)] s"  + getLocalServerID() +
                  ", at time " + getClockValue() + ", discarded " + preprepare +
                  " because it hasn't a valid sequence/view number. "
                  + "(currView = " + getCurrentViewNumber() + ")"
                  + "[nextPP = " + nextPP + ", nextP = "
                  + nextP + ", nextC =" + nextC
                  + " , nextE = " + nextE + "]"
                );

                return false;

            }

            /**
             * If the preprepare message wasn't sent by the primary replica then
             * it will be discarded.
             */
            if(!wasSentByPrimary(preprepare)){
                Debugger.debug(
                  "[PBFTServer:canProceed(preprepare)] s"   + getLocalServerID()   +
                  ", at time " + getClockValue() + ", discarded " + preprepare      +
                  " because it wasn't sent by primary server s" + getCurrentPrimaryID()
                );

                return false;
            }
      //  }
        return true;
    }

    /**
     * Create a new Prepare Message from a pre-prepare message.
     * @param pp - the pre-prepare message.
     * @return the created prepare message.
     */
    public PBFTPrepare createPrepareMessage(PBFTPrePrepare pp){

        //synchronized(this){
            PBFTPrepare p = new PBFTPrepare(pp, getLocalServerID());

            return p;
        //}
    }

    /*########################################################################
     # 3. Methods for handling prepare messages.
     #########################################################################*/
    /**
     * Perform the procedures to handle a prepare message, this is: <BR>
     * <DL>
     *  <LI> check if the prepare has a valid sequence/view number.
     *  <LI> check if the prepare was sent by the primary replica and this case
     * the prepare is discarded.
     *  <LI> check if the prepare was sent a group member.
     *  <LI> if is a valid prepare then the server state will be updated and, when
     * a prepare certificate is obtained, a commit will be sent.
     * @param prepare - the prepare message.
     * @return true if the prepare was accepted, otherwise false.
     * @see PBFTPrepare
     */
    public void handle(PBFTPrepare prepare){
        Debugger.debug(
           "[PBFTServer:handle(prepare)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + prepare
        );

        if(canProceed(prepare)){
            if(updateState(prepare)){

                PBFTCommit commit = createCommitMessage(prepare);

                emit(commit, getLocalGroup().minus(getLocalProcess()));

                updateState(commit);
            }
        }

    }

    public boolean canProceed(PBFTPrepare prepare){
//        synchronized(this){
            /**
             * If the preprepare hasn't a valid sequence or view number then force a
             * change view.
             */
            if(!(checkSequenceNumber(prepare) && checkViewNumber(prepare))){
                long nextPP = getStateLog().getNextPrePrepareSEQ();
                long nextP  = getStateLog().getNextPrepareSEQ();
                long nextC  = getStateLog().getNextCommitSEQ();
                long nextE  = getStateLog().getNextExecuteSEQ();

                Debugger.debug(
                  "[PBFTServer:canProceed(prepare)] s"  + getLocalServerID() +
                  ", at time " + getClockValue() + ", discarded " + prepare +
                  " because it hasn't a valid sequence/view number. "
                  + "(currView = " + getCurrentViewNumber() + ")"
                  + "[nextPP = " + nextPP + ", nextP = " + nextP + ", nextC =" + nextC + " , nextE = " + nextE + "]"
                );

                return false;

            }

            /**
             * If the preprepare message wasn't sent by a group member then it will
             * be discarded.
             */
            if(!wasSentByAGroupMember(prepare)){
                Debugger.debug(
                  "[PBFTServer:canProceed(prepare)] s"   + getLocalServerID()   +
                  ", at time " + getClockValue() + ", discarded " + prepare      +
                  " because it wasn't sent by a member of the group " + getLocalGroup()
                );

                return false;
            }

            /**
             * If the prepare message was sent by the primary then it will
             * be discarded.
             */
            if(wasSentByPrimary(prepare)){
                Debugger.debug(
                  "[PBFTServer:canProceed(prepare)] s"   + getLocalServerID()   +
                  ", at time " + getClockValue() + ", discarded " + prepare      +
                  " because it was sent by the primary " + getCurrentPrimaryID()
                );

                return false;
            }


            if(!getStateLog().wasPrePrepared(prepare)){
                Debugger.debug(
                  "[PBFTServer:canProceed(prepare)] s"   + getLocalServerID()   +
                  ", at time " + getClockValue() + ", discarded " + prepare      +
                  " because it hasn't received a related pre-prepare."
                );

                return false;
            }

  //      }
        return true;
    }

    public PBFTCommit createCommitMessage(PBFTPrepare p){

    //    synchronized(this){
            PBFTCommit c = new PBFTCommit(p, getLocalServerID());

            return c;
      //  }

    }

   /*########################################################################
     # 4. Methods for handling commit messages.
     #########################################################################*/
    /**
     * Collect the request sent by the client.
     * @param commit -- the client request.
     */
    public void handle(PBFTCommit commit){

        Debugger.debug(
           "[PBFTServer:handle(commit)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + commit
        );

        if(canProceed(commit)){
            if(updateState(commit)){
                handle(new PBFTProcessingToken(commit.getViewNumber(), commit.getSequenceNumber()));
//                PBFTProcessingHandler handler = new PBFTProcessingHandler(this);
//                handler.input(new PBFTProcessingToken(commit.getViewNumber(), commit.getSequenceNumber()));
//                handler.handle();
            }
        }
    }

    public boolean canProceed(PBFTCommit commit){
        //synchronized(this){

            /**
             * If the preprepare hasn't a valid sequence or view number then force a
             * change view.
             */
            if(!(checkSequenceNumber(commit) && checkViewNumber(commit))){

                long nextPP = getStateLog().getNextPrePrepareSEQ();
                long nextP  = getStateLog().getNextPrepareSEQ();
                long nextC  = getStateLog().getNextCommitSEQ();
                long nextE  = getStateLog().getNextExecuteSEQ();
                Debugger.debug(
                  "[PBFTServer:canProceed(commit)] s"  + getLocalServerID() +
                  ", at time " + getClockValue() + ", discarded " + commit +
                  " because it hasn't a valid sequence/view number. "
                  + "(currView = " + getCurrentViewNumber() + ")"
                  + "[nextPP = " + nextPP + ", nextP = " + nextP + ", nextC =" + nextC
                  + " , nextE = " + nextE + "]"
                );

                return false;

            }

            /**
             * If the preprepare message wasn't sent by a group member then it will
             * be discarded.
             */
            if(!wasSentByAGroupMember(commit)){
                Debugger.debug(
                  "[PBFTServer:canProceed(commit)] s"   + getLocalServerID()   +
                  ", at time " + getClockValue() + ", discarded " + commit      +
                  " because it wasn't sent by a member of the group " + getLocalGroup()
                );

                return false;
            }

            if(!getStateLog().wasPrepared(commit)){
                Debugger.debug(
                  "[PBFTServer:canProceed(commit)] s"   + getLocalServerID()   +
                  ", at time " + getClockValue() + ", discarded " + commit      +
                  " because it hasn't received a related pre-prepare."
                );

                return false;
            }
        //}
        return true;


    }

   /*########################################################################
     # 5. Methods for handling checkpoint messages.
     #########################################################################*/
    /**
     * Collect the request sent by the client.
     * @param checkpoint -- the client request.
     */
    public void handle(PBFTCheckpoint checkpoint){

        Debugger.debug(
           "[PBFTServer:handle(checkpoint)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + checkpoint
        );

        if(canProceed(checkpoint)){

            if(updateState(checkpoint)){

                long hcwm = getCheckpointHighWaterMark();
                long seqn = checkpoint.getSequenceNumber();

                if(seqn > hcwm){
                    Debugger.debug(
                      "[PBFTServer:handle(checkpoint)] s" + getLocalServerID()     +
                      ", at time " + getClockValue() + ", detected a stable " +
                      "checkpoint certificate with sequence number (" + seqn + ") " +
                      "greater than its high checkpoint water mark (HCWM = " + hcwm + ")."
                    );
                    Debugger.debug(
                      "[PBFTServer:handle(checkpoint)] s" + getLocalServerID()     +
                      ", at time " + getClockValue() + ", is going to start " +
                      " a start transfer procedure."
                    );

                    emit(createFetchMessage(),getLocalGroup().minus(getLocalProcess()));
                    return;
                }

                doCheckpoint(seqn);
            }
        }
    }

    public boolean canProceed(PBFTCheckpoint checkpoint){
//        synchronized(this){
            /**
             * If the preprepare message wasn't sent by a group member then it will
             * be discarded.
             */
            if(!wasSentByAGroupMember(checkpoint)){
                Debugger.debug(
                  "[PBFTServer:canProceed(checkpoint)] s"   + getLocalServerID()   +
                  ", at time " + getClockValue() + ", discarded " + checkpoint      +
                  " because it wasn't sent by a member of the group " + getLocalGroup()
                );

                return false;
            }

            /**
             * If the preprepare message wasn't sent by a group member then it will
             * be discarded.
             */
            long lowWaterMark = getCheckpointLowWaterMark();
            //long highWaterMark = getCheckpointHighWaterMark();
            long seqn = checkpoint.getSequenceNumber();

            if(lowWaterMark > seqn){
                Debugger.debug(
                  "[PBFTServer:canProceed(checkpoint)] s"   + getLocalServerID()     +
                  ", at time " + getClockValue() + ", discarded " + checkpoint +
                  " because it has sequence number lower than current low water mark " +
                  "(LCWM = " + lowWaterMark + "). "
                );

                return false;
            }
  //      }
        return true;
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
    public PBFTFetch createFetchMessage(){

        PBFTFetch f = new PBFTFetch(getCheckpointLowWaterMark(), getReplierID(), getLocalServerID());

        return f;

    }
   /*########################################################################
     # 6. Methods for handling bag of messages.
     #########################################################################*/

    public void handle(PBFTBag bag, ISupplier supplier){

        for(IMessage m : bag.getMessages()){

            if(m instanceof PBFTPrePrepare) {
                handle((PBFTPrePrepare)m);
            }

            if(m instanceof PBFTPrepare) {
                handle((PBFTPrepare)m);
            }

            if(m instanceof PBFTCommit) {
                handle((PBFTCommit)m);
            }

            if(m instanceof PBFTCheckpoint){
                handle((PBFTCheckpoint)m);
            }
        }

    }

   /*########################################################################
     # 7. Methods for handling status-active messages.
     #########################################################################*/

    public boolean accept(PBFTStatusActive sa){
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
            if(minSEQ > rlcwSEQ) minSEQ = rlcwSEQ; if(maxSEQ > rlcwSEQ) maxSEQ = rlcwSEQ;
            
            if(maxSEQ < 0) maxSEQ = 0L;

            //local active state variables
            long lexcSEQ = getStateLog().getNextExecuteSEQ()    -1;
            long lcmtSEQ = getStateLog().getNextCommitSEQ()     -1;
            long lpreSEQ = getStateLog().getNextPrepareSEQ()    -1;
            long lpprSEQ = getStateLog().getNextPrePrepareSEQ() -1;
            long llcwSEQ = getStateLog().getCheckpointLowWaterMark();
            
        }

//        IProcess rServer = new br.ufba.lasid.jds.Process(statusActive.getReplicaID());
//
//        if(statusActive != null && !getLocalServerID().equals(rServer.getID())){
//
//            /**
//             * If the preprepare hasn't a valid sequence or view number then force a
//             * change view.
//             */
//            if(!checkViewNumber(statusActive)){
//                long nextPP = getStateLog().getNextPrePrepareSEQ();
//                long nextP  = getStateLog().getNextPrepareSEQ();
//                long nextC  = getStateLog().getNextCommitSEQ();
//                long nextE  = getStateLog().getNextExecuteSEQ();
//                long lwSEQ  = getCheckpointLowWaterMark();
//
//                Debugger.debug(
//                  "[PBFTServer:handle(activeStatus)] s"  + getLocalServerID() +
//                  ", at time " + getClockValue() + ", discarded " + statusActive +
//                  " because it hasn't a valid view number. "
//                  + "(currView = " + getCurrentViewNumber() + ")"
//                  + "[nextPP = " + nextPP + ", nextP = "
//                  + nextP + ", nextC =" + nextC
//                  + " , nextE = " + nextE + ", lowWaterMark = " + lwSEQ + "]"
//                );
//
//                return false;
//
//            }
//
//            /**
//             * If the preprepare message wasn't sent by a group member then it will
//             * be discarded.
//             */
//            if(!wasSentByAGroupMember(statusActive)){
//                Debugger.debug(
//                  "[PBFTServer:handle(activeStatus)] s"   + getLocalServerID()   +
//                  ", at time " + getClockValue() + ", discarded " + statusActive      +
//                  " because it wasn't sent by a member of the group " + getLocalGroup()
//                );
//
//                return false;
//            }
//
//            /**
//             * Send the executed request.
//             */
//            if(getStateLog().getNextPrepareSEQ() >= 0){
//                Long maxSEQ = getStateLog().getNextPrePrepareSEQ();
//                Long minSEQ = getStateLog().getNextExecuteSEQ()-1;
//
//                long eSEQ  = statusActive.getLastExecutedSEQ();
//                long cSEQ  = statusActive.getLastCommittedSEQ();
//                long pSEQ  = statusActive.getLastPreparedSEQ();
//                long ppSEQ = statusActive.getLastPrePreparedSEQ();
//                long lwSEQ   = statusActive.getLastStableCheckpointSEQ();
//
//                if(minSEQ > eSEQ ) minSEQ = eSEQ;
//                if(minSEQ > cSEQ ) minSEQ = cSEQ;
//                if(minSEQ > pSEQ ) minSEQ = pSEQ;
//                if(minSEQ > ppSEQ) minSEQ = ppSEQ;
//                if(minSEQ < 0L)    minSEQ = 0L;
//
//                if(maxSEQ < eSEQ ) maxSEQ = eSEQ;
//                if(maxSEQ < cSEQ ) maxSEQ = cSEQ;
//                if(maxSEQ < pSEQ ) maxSEQ = pSEQ;
//                if(maxSEQ < ppSEQ) maxSEQ = ppSEQ;
//
//                if(maxSEQ < 0L) maxSEQ = 0L;
//
//                long _eSEQ  = getStateLog().getNextExecuteSEQ() - 1L;
//                long _cSEQ  = getStateLog().getNextCommitSEQ()  - 1L;
//                long _pSEQ  = getStateLog().getNextPrepareSEQ() - 1L;
//                long _ppSEQ = getStateLog().getNextPrePrepareSEQ() - 1L;
//                long _lwSEQ = getCheckpointLowWaterMark();
//
//                QuorumTable qtable = getStateLog().getQuorumTable(PBFT.CHECKPOINTQUORUMSTORE);
//
//                PBFTBag bag = new PBFTBag(getLocalServerID());
//
//                boolean sent = false;
//                try{
//                    for(long i = minSEQ; i < maxSEQ; i++){
//
//                        PBFTLogEntry entry = getStateLog().get(i);
//
//                        if(entry != null){
//                            Quorum pq = entry.getPrepareQuorum();
//                            Quorum cq = entry.getCommitQuorum();
//
//
//                            if(cSEQ < i && pSEQ < i && ppSEQ <= i && isPrimary() && entry.getPrePrepare() != null && _ppSEQ >= i){
//                                PBFTPrePrepare pp = entry.getPrePrepare();
//                                bag.addMessage(pp);
//                                //emit(pp);
//                                sent = sent || true;
//                            }
//
//                            if(cSEQ < i && pSEQ <= i && pq != null/*&& _pSEQ >= i*/){
//                                Quorum q = new Quorum();
//                                q.addAll(pq);
//                                if(!q.isEmpty()){
//
//                                    for(IMessage m : q){
//
//                                        PBFTPrepare p = (PBFTPrepare)m;
//
//                                        if(p.getReplicaID().equals(getLocalServerID())){
//                                            bag.addMessage(p);
//                                            //emit(p);
//                                            sent = sent || true;
//                                        }
//                                    }
//                                }
//                            }
//
//                            if(cSEQ <= i && cq != null/*&& _cSEQ >=i*/){
//
//                                Quorum q = new Quorum();
//                                q.addAll(cq);
//
//                                if(!q.isEmpty()){
//                                    for(IMessage m : q){
//
//                                        PBFTCommit c = (PBFTCommit)m;
//
//                                        if(c.getReplicaID().equals(getLocalServerID())){
//                                            bag.addMessage(c);
//                                            //emit(c);
//                                            sent = sent || true;
//                                        }
//                                    }
//                                }
//                            }
//                        }//end if entry
//                    }//end for seq
//
//                    long currSEQ = lwSEQ + 1;
//                    IStore store = getCheckpointStore();
//
//                    while(currSEQ < _lwSEQ){
//                        try {
//                            String index = String.valueOf(currSEQ) + ";" + "null";
//
//                            Tuple tuple = store.findGreaterOrEqual(index);
//
//                            if(tuple == null){
//                                break;
//                            }
//
//                            String key = (String)tuple.getKey();
//                            String[] pair = key.split(";");
//
//                            Long seqn = Long.valueOf(pair[0]);
//
//                            String digest = pair[1];
//
//                            PBFTCheckpoint checkpoint = new PBFTCheckpoint(seqn, digest, getLocalServerID());
//
//                            //emit(checkpoint);
//                            bag.addMessage(checkpoint);
//
//                            currSEQ = seqn + 1;
//
//                        } catch (Exception ex) {
//                            Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
//                            ex.printStackTrace();
//                        }
//                    }//end while currSEQ < _lwSEQ, if currSEQ_0 = lwSEQ
//                }catch(Exception ex){
//                    Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
//                    ex.printStackTrace();
//                }
//                if(!bag.isEmpty()){
//                    emit(bag, rServer);
//                }
//            }
//        }

        return true;

    }

    public boolean canProceed(PBFTStatusActive sa){
        IProcess rServer = new br.ufba.lasid.jds.Process(sa.getReplicaID());
        if(getLocalServerID().equals(rServer.getID())){
            Debugger.debug(
              "[PBFTServer:canProceed] s" + getLocalServerID() + ", at time " + getClockValue() + ", " +
              "discarded " + sa + " because it was sent by the local replica."
            );
            return false;
        }

        if(!checkViewNumber(sa)){
            long nxtPP = getStateLog().getNextPrePrepareSEQ();
            long nxtP  = getStateLog().getNextPrepareSEQ();
            long nxtC  = getStateLog().getNextCommitSEQ();
            long nxtE  = getStateLog().getNextExecuteSEQ();
            long lcwm  = getCheckpointLowWaterMark();

            Debugger.debug(
              "[PBFTServer:canProceed(activeStatus)] s"  + getLocalServerID() + ", at time " + getClockValue() + ", "   +
              "discarded " + sa + " because it hasn't a valid view number. (currView = " + getCurrentViewNumber() + ") " +
              "[nxtPP = " + nxtPP + ", nxtP = " + nxtP + ", nxtC =" + nxtC + " , nxtE = " + nxtE + ", LCWM = " + lcwm + "]"
            );
            
            return false;
        }

        if(!wasSentByAGroupMember(sa)){
            Debugger.debug(
              "[PBFTServer:canProceed(activeStatus)] s" + getLocalServerID() + ", at time " + getClockValue() + ", " +
              "discarded " + sa + " because it wasn't sent by a member of the group " + getLocalGroup() + "."
            );

            return false;
        }

        return getStateLog().getNextPrepareSEQ() >=0;
    }
   /*########################################################################
     # 8. Execute sequence number.
     #########################################################################*/

    public void handle(PBFTProcessingToken proctoken){
        Debugger.debug(
           "[PBFTSever:handle(token)] s" + getLocalServerID() + ", " +
           "at time " + getClockValue() + ", received " + proctoken
        );

        if(canProceed(proctoken)){
            Long currSEQ = proctoken.getSequenceNumber();
            PBFTPrePrepare preprepare = getStateLog().getPrePrepare(currSEQ);

            for(String digest : preprepare.getDigests()){

                StatedPBFTRequestMessage statedReq = getStateLog().getStatedRequest(digest);

                PBFTRequest request = statedReq.getRequest();

                IServer lServer = getServer();

                IPayload result = lServer.executeCommand(request.getPayload());

                PBFTReply reply = createReplyMessage(request, result);

                statedReq.setState(StatedPBFTRequestMessage.RequestState.SERVED);
                statedReq.setReply(reply);

                Debugger.debug(
                  "[PBFTServer:handle(token)] s"  + getLocalServerID() + ", " +
                  "at time " + getClockValue() + ", executed " + request +
                  " (currView = " + getCurrentViewNumber() + ")"
                );

                IProcess client = new br.ufba.lasid.jds.Process(reply.getClientID());
                emit(reply, client);

            }//end for each digest (handle and reply)

            getStateLog().updateNextExecuteSEQ(currSEQ);

            long execSEQ = getStateLog().getNextExecuteSEQ() -1;
            long chkpSEQ = getCheckpointPeriod();

            if(execSEQ > 0 && ((execSEQ % chkpSEQ) == 0)){
                emit(createCheckpointMessage(execSEQ),getLocalGroup().minus(getLocalProcess()));
            }

        }
    }

    public boolean canProceed(PBFTProcessingToken token){
        Long currSEQ = token.getSequenceNumber();

        PBFTLogEntry entry = getStateLog().get(currSEQ);

        if(!(entry != null && entry.getPrePrepare() != null)){
            return false;
        }

        Quorum pq = entry.getPrepareQuorum();
        Quorum cq = entry.getCommitQuorum();

        return (pq != null && pq.complete() && cq != null & cq.complete());
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

    public PBFTCheckpoint createCheckpointMessage(long seqn){

      //  synchronized(this){
            IRecoverableServer lserver = (IRecoverableServer) getServer();

            IState state = lserver.getCurrentState();

            String digest = computeStateDigest(state, seqn);

            PBFTCheckpoint c = new PBFTCheckpoint(seqn, digest, getLocalServerID());

            getStateLog().doCacheServerState(seqn, digest, state);

            return c;
        //}
    }

    public String computeStateDigest(IState state, long seqn){
//        synchronized(this){
            String digest = "";
            String more = "";

            PBFTCheckpointTable ctable = getStateLog().getCachedState();

            ArrayList<Long> seqns = new ArrayList<Long>(ctable.keySet());

            Collections.sort(seqns);

            long maxSEQ = -1;

            if(!seqns.isEmpty()){
                maxSEQ = seqns.get(seqns.size()-1);
                digest = ctable.get(maxSEQ).getDigest();
                more = "|";
            }

            try {

                digest = getAuthenticator().getDigest(
                    "<" + seqn + "|" + getAuthenticator().getDigest(state) + more + digest +">"
                );

            } catch (Exception ex) {
                Logger.getLogger(PBFTServer.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                return null;
            }

            return digest;
  //      }
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

                Debugger.debug(
                  "[PBFTServer:emit(msg, dest)]s" +  getLocalServerID() +
                  " " + sent + " " + msg + " at timestamp " + getClockValue() +
                  " to " + remote + "."
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

}