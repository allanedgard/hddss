/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.cs.IServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTServerMessage;
import br.ufba.lasid.jds.util.DigestList;
import java.util.Hashtable;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTLogEntry;
import br.ufba.lasid.jds.util.Debugger;
import br.ufba.lasid.jds.util.StatedPBFTRequestMessage;

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

    protected volatile long nextPrePrepareSEQ =  0L;
    protected volatile long nextPrepareSEQ    =  0L;
    protected volatile long nextCommitSEQ     =  0L;
    protected volatile long nextExecuteSEQ    =  0L;

    public synchronized long getSendStatusPeriod() {
        return sendStatusPeriod;
    }

    public synchronized void setSendStatusPeriod(long sendStatusPeriod) {
        this.sendStatusPeriod = sendStatusPeriod;
    }

    public synchronized void updateNextPrePrepareSEQ(PBFTPrePrepare m){
        if(m != null && m.getSequenceNumber() != null){
            long seqn = m.getSequenceNumber();
            if(seqn == nextPrePrepareSEQ){
                nextPrePrepareSEQ++;
            }
        }
    }

    public synchronized void updateNextPrepareSEQ(PBFTPrepare m){
        if(m != null && m.getSequenceNumber() != null){
            long seqn = m.getSequenceNumber();
            if(seqn < nextPrePrepareSEQ && seqn == nextPrepareSEQ){
                nextPrepareSEQ++;
            }
        }
    }

    public synchronized void updateNextCommitSEQ(PBFTCommit m){
        if(m != null && m.getSequenceNumber() != null){
            long seqn = m.getSequenceNumber();
            if(seqn < nextPrePrepareSEQ && seqn < nextPrepareSEQ && seqn == nextCommitSEQ){
                nextCommitSEQ++;
            }
        }
    }

    public synchronized void updateNextExecuteSEQ(Long theSEQ){
        if(theSEQ != null){

            long seqn = theSEQ;
            
            if(seqn < nextPrePrepareSEQ && seqn < nextPrepareSEQ && seqn < nextCommitSEQ && seqn == nextExecuteSEQ){
                nextExecuteSEQ++;
            }

            //System.out.print("seqn = " + seqn + "; nextExec =" + nextExecuteSEQ);
        }
    }


    public synchronized long getNextCommitSEQ() {
        return nextCommitSEQ;
    }

    public synchronized long getNextPrePrepareSEQ() {
        return nextPrePrepareSEQ;
    }

    public synchronized long getNextPrepareSEQ() {
        return nextPrepareSEQ;
    }

    public synchronized long getNextExecuteSEQ() {
        return nextExecuteSEQ;
    }

    public synchronized boolean isTheNextToExecute(long seqn){
        return seqn == nextExecuteSEQ;
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

        return seqn >= low && seqn <= high;


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

                updateNextExecuteSEQ(preprepare.getSequenceNumber());
                return true;
            }

        }
        
        return false;

    }

    @Override
    public void startup() {
        super.startup();
    }
    
}