/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.cs.IServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTServerMessage;
import br.ufba.lasid.jds.util.DigestList;
import java.io.IOException;
import java.util.Hashtable;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTLogEntry;
import br.ufba.lasid.jds.util.Debugger;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTCheckpointStorage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTCheckpointTuple;
import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IRecoverableServer;
import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IState;
import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IStore;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdbm.helper.Tuple;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;

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
    protected volatile int currentViewNumber = 0;
    protected Object currentPrimaryID = null;
    protected PBFTPrePrepare currentPrePrepare = null;
    protected static long SEQ = -1;
    protected IServer server;

    public synchronized long getSendStatusPeriod() {
        return sendStatusPeriod;
    }

    public synchronized void setSendStatusPeriod(long sendStatusPeriod) {
        this.sendStatusPeriod = sendStatusPeriod;
    }

    protected Long primaryFaultTimeout = null;
    
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

    public void setCheckpointPeriod(long checkpointPeriod) {
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

    public synchronized boolean isPrimary(){
        return isPrimary(getLocalProcess());
    }

    public synchronized boolean isPrimary(IProcess p){
        return isPrimary(p.getID());
    }

    public synchronized boolean isPrimary(Object serverID){
        return getCurrentPrimaryID().equals(serverID);
    }

    public synchronized Object getLocalServerID(){
        return getLocalProcess().getID();
    }

    public Object getCurrentPrimaryID(){
        return currentPrimaryID;
    }

    public void setCurrentPrimaryID(Object serverID){
        currentPrimaryID = serverID;
    }


    public static long newSequenceNumber(){
        return ++SEQ;
    }

    public static long getCurrentSequenceNumber(){
        return SEQ;
    }

    public static void updateCurrentSequenceNumber(long sqn){
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

    protected long rejuvenationWindow;

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

    Hashtable<Object, PBFTRequest> currentRequestTable = new Hashtable<Object, PBFTRequest>();
    
    public boolean isTheNext(PBFTRequest currRequest){
        
        if(currRequest != null){
            PBFTRequest lastRequest = currentRequestTable.get(currRequest.getClientID());

            if(lastRequest == null){
                return true;
            }

            return (currRequest.getTimestamp().compareTo(lastRequest.getTimestamp()) > 0);

        }
        return false;
    }

    public void updateCurrentRequest(PBFTRequest r){
        if(r != null){
            
            if(r.getClientID() != null && r.getTimestamp() != null){
                currentRequestTable.put(r.getClientID(), r);
            }
        }
    }

    /**
     * Checks if a message has a valid sequence number, this is: the sequence
     * number doesn't has holes and is in a valid range.
     * @param m -- the message.
     * @return -- true if the message has a valid sequence number.
     */
    public boolean hasAValidSequenceNumber(PBFTServerMessage m) {

        return isOrdered(m) && inAValidSequenceRange(m);

    }

    /**
     * Check if a message insert a hole in the sequence numbers.
     * @param m -- the message.
     * @return -- true if the message inserts a hole in the sequence numbers.
     */
    protected synchronized boolean isOrdered(PBFTServerMessage m){
        
        long nextPrePrepareSEQ = getStateLog().getNextPrePrepareSEQ();
        long nextPrepareSEQ = getStateLog().getNextPrepareSEQ();

        if(m != null && m.getSequenceNumber() != null){
            
            long seqn = m.getSequenceNumber();

            if(m instanceof PBFTPrePrepare){
                return seqn == nextPrePrepareSEQ;
            }

            if(m instanceof PBFTPrepare){
                return seqn == nextPrepareSEQ;
            }

            if(m instanceof PBFTCommit){
                return seqn <= nextPrepareSEQ;
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
    public boolean hasAValidViewNumber(PBFTServerMessage m) {

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
    public synchronized boolean updateState(PBFTPrePrepare preprepare){

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
                  "[PBFTServer] s"  + getLocalProcess().getID() +
                  ", at time " + getClock().value() + ", created a new entry in "
                + "its log for " + preprepare
                );
                
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
    public synchronized boolean updateState(PBFTPrepare prepare){

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

                    q = new Quorum(2 * f + 1);

                    entry.setPrepareQuorum(q);
                }

                if(q.size() > 0){                   

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
                          "[PBFTServer] s"  + getLocalProcess().getID() +
                          ", at time " + getClock().value() + ", cann't use " + prepare +
                          " to update its log because it's a duplicated prepare."
                        );
                            return false;
                        }
                    }
                }

                q.add(prepare);
                
                
                /**
                 * Update the entry in log.
                 */
                getStateLog().put(entryKey, entry);

                Debugger.debug(
                  "[PBFTServer] s"  + getLocalProcess().getID() +
                  ", at time " + getClock().value() + ", update a entry in "
                + "its log for " + prepare
                );

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
    public synchronized boolean updateState(PBFTCommit commit){

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

                    if(q.size() > 0){


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
                              "[PBFTServer] s"  + getLocalProcess().getID() +
                              ", at time " + getClock().value() + ", cann't use " + commit +
                              " to update its log because it's a duplicated commit."
                            );

                                return true;
                            }
                        }
                    }

                    q.add(commit);


                    /**
                     * Update the entry in log.
                     */
                    getStateLog().put(entryKey, entry);

                    Debugger.debug(
                      "[PBFTServer] s"  + getLocalProcess().getID() +
                      ", at time " + getClock().value() + ", update a entry in "
                    + "its log for " + commit
                    );
                    
                    return true;
                }
            }
        }

        return false;

    }

    /**
     * Update the state of the PBFT. Insert the pre-prepare message in
     * the log entry.
     * @param m
     */
    public synchronized boolean updateState(PBFTCheckpoint checkpoint){

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

            if(q.size() > 0){

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
                      "[PBFTServer] s"  + getLocalProcess().getID() +
                      ", at time " + getClock().value() + ", cann't use " + checkpoint +
                      " to update its log because it's a duplicated prepare."
                    );
                        return false;
                    }
                }
            }

            q.add(checkpoint);

            Debugger.debug(
              "[PBFTServer] s"  + getLocalServerID() +
              ", at time " + getClock().value() + ", update a entry in "
            + "its log for " + checkpoint
            );

            return true;
        }

        return false;

    }



    /**
     * Update the state of the PBFT. Insert the pre-prepare message in
     * the log entry.
     * @param m
     */
    public synchronized boolean updateState(PBFTReply reply){

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

    

    public synchronized void doCheckpoint(long seqn) {

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

        getStateLog().setCheckpointLowWaterMark(lowWater);
        
        Debugger.debug("[PBFTServer] s" + getLocalServerID() + ", at time " + getClock().value() + ", starts the garbage collection procedure ...");
        
        getStateLog().doGarbage();
        
        Debugger.debug("[PBFTServer] s" + getLocalServerID() + " starts the garbage collection complete!");

    }

    protected long slidindWindowSize = 1;

    public void setSlidingWindowSize(Long size) {
        slidindWindowSize = size;
    }

    Buffer window = BufferUtils.blockingBuffer(new UnboundedFifoBuffer());
    
    public long getSlidingWindowSize(){
        return slidindWindowSize;
    }

    public  synchronized void waitWindow(){
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
}