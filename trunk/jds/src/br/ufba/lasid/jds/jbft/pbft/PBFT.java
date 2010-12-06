/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.cs.ClientServerProtocol;
import br.ufba.lasid.jds.util.Wrapper;
import br.ufba.lasid.jds.factories.PBFTActionFactory;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.security.Authenticator;
import br.ufba.lasid.jds.util.Buffer;
import br.ufba.lasid.jds.util.Clock;
import br.ufba.lasid.jds.util.Debugger;
import br.ufba.lasid.jds.util.Scheduler;

/**
 * Pratical Byzantine Fault Tolerant Protocol (Castro and Liskov, 1999)
 * @author aliriosa
 */
public class PBFT extends ClientServerProtocol{

    public static String DEBUGGER = "__DEBUGGER";
    public static String LOCALGROUP = "__LOCALGROUP";
    public static String GROUPLEADER = "__GROUPLEADER";
    public static String CLIENTSCHEDULER  = "__CLIENTSCHEDULER";
    public static String PRIMARYFDSCHEDULER = "__PRIMARYFDSCHEDULER";
    public static String PREPREPARETIMEOUT = "__PREPREPARETIMEOUT";
    public static String LATEPRIMARYTIMEOUT = "__LATEPRIMAYTIMEOUT";
    public static String CLIENTRETRANSMISSIONTIMEOUT = "__CLIENTRETRANSMISSIONTIMEOUT";
    public static String CLIENTMSGAUTHENTICATOR = "CLIENTMSGAUTHENTICATOR";
    public static String CURRENTVIEW = "__CURRENTVIEW";
    public static String ALLOWABLENUMBEROFFAULTREPLICAS = "__ALLOWABLENUMBEROFFAULTREPLICAS";
    public static String CLOCKSYSTEM = "__CLOCKSYSTEM";
    public static String REQUESTBUFFER = "__REQUESTBUFFER";
    public static String PREPREPAREBUFFER = "__PREPREPAREBUFFER";
    public static String PREPAREBUFFER = "__PREPAREBUFFER";
    public static String COMMITBUFFER = "__COMMITBUFFER";
    public static String CHANGEVIEWBUFFER = "__CHANGEVIEWBUFFER";
    public static String  COMMITTEDBUFFER = "__COMMITTEDBUFFER";
    public static String CLIENTAUTHENTICATOR = "__CLIENTAUTHENTICATOR";
    public static String SERVERAUTHENTICATOR = "__SERVERAUTHENTICATOR";
    public static String PRIMARYFAULTTIMEOUT = "__PRIMARYFAULTYTIMEOUT";
    public static String BATCHINGSIZE        = "__BATCHINGSIZE";
    public static String BATCHINGTIMEOUT = "__BATCHINGTIMEOUT";
    public static String BATCHSCHEDULER = "__BATCHSCHEDULER";
    public static String CHECKPOINTPERIOD = "__CHECKPOINTPERIOD";
    public static String CHECKPOINTNUMBER = "__CHECKPOINTNUMBER";

    /*
        [TODO] verify if sequence number of the message achieves the criteries.
     */
    public static boolean isValidSequenceNumber(PBFTMessage m) {
        return true;
    }

    public Buffer getCommittedBuffer() {
        return (Buffer)getContext().get(PBFT.COMMITTEDBUFFER);
    }

    public enum BATCHSTATE{
        NOBATCH, INBATCH, BATCHED
    }


    public int BATCHINGCOUNT = 0;
    public Long lastCommitedSequenceNumber = new Long(0);

    protected static long SEQ = 0;

    public static long newSequenceNumber(){
        return ++SEQ;
    }

    public String getRequestField(){
        return getRequestField(BATCHINGCOUNT);
    }

    public void setLastCommitedSequenceNumber(Long n){
        lastCommitedSequenceNumber = n;
    }

    public Long getLastCommitedSequenceNumber(){
        return lastCommitedSequenceNumber;
    }
    public String getRequestField(int i){
        return (PBFTMessage.REQUESTFIELD +  i);
    }

    public int getBatchingCount(){
        return BATCHINGCOUNT;
    }

    public void increaseBatch() {
        BATCHINGCOUNT++;
    }

    public int getMaxBatchSize(){
        return (((Integer)getContext().get(PBFT.BATCHINGSIZE)).intValue());
    }
    public boolean batchIsNotComplete(){
        return (getBatchingCount() < getMaxBatchSize());
    }

    public void initBatching(){
        BATCHINGCOUNT = 0;
    }
    @Override
    public void doAction(Wrapper w){
       //System.out.println("[Protocol] call Protocol.perform");
       perform(PBFTActionFactory.create(w));
    }

    public Long getBatchingTimeout(){
        return (Long)getContext().get(PBFT.BATCHINGTIMEOUT);
    }

    public Long getRetransmissionTimeout(){
        return (Long)getContext().get(PBFT.CLIENTRETRANSMISSIONTIMEOUT);
    }

    public Long getPrimaryFaultyTimeout(){
        return (Long)getContext().get(PBFT.PRIMARYFAULTTIMEOUT);
    }

    public void setPrimaryFaultTimeout(Long timeout){
        getContext().put(PBFT.PRIMARYFAULTTIMEOUT, timeout);
    }
    public Long getTimestamp(){
        return new Long(((Clock)getContext().get(PBFT.CLOCKSYSTEM)).value());
    }

    public Debugger getDebugger(){
        return (Debugger) getContext().get(PBFT.DEBUGGER);
    }

    public Scheduler getClientScheduler(){
        return (Scheduler)(getContext().get(PBFT.CLIENTSCHEDULER));
    }

    public Scheduler getBatchingScheduler(){
        return (Scheduler)(getContext().get(PBFT.BATCHSCHEDULER));
    }

    public Scheduler getPrimaryFDScheduler(){
        return (Scheduler)(getContext().get(PBFT.PRIMARYFDSCHEDULER));
    }

    public Authenticator getServerAuthenticator(){
        return (Authenticator)(getContext().get(PBFT.SERVERAUTHENTICATOR));
    }

    public Authenticator getClientMessageAuthenticator(){
        return (Authenticator)(getContext().get(PBFT.CLIENTMSGAUTHENTICATOR));
    }

    public synchronized Buffer getRequestBuffer(){
        return ((Buffer)(getContext().get(PBFT.REQUESTBUFFER)));
    }

    public boolean isPrimary(){
        return isPrimary(getLocalProcess());
    }
    public boolean isPrimary(br.ufba.lasid.jds.Process p){
        return (getContext().get(PBFT.GROUPLEADER)).equals(p.getID());
    }

    public Integer getCurrentView(){
        return (Integer)getContext().get(PBFT.CURRENTVIEW);
    }

    public void setCurrentView(Integer v){
        getContext().put(PBFT.CURRENTVIEW, v);
    }

    public Group getLocalGroup(){
        return (Group)getContext().get(PBFT.LOCALGROUP);
    }

    public Buffer getPreprepareBuffer() {
        return (Buffer)getContext().get(PBFT.PREPREPAREBUFFER);
    }

    public Buffer getPrepareBuffer() {
        return (Buffer)getContext().get(PBFT.PREPAREBUFFER);
    }

    public Buffer getCommitBuffer() {
        return (Buffer)getContext().get(PBFT.COMMITBUFFER);
    }

    public Buffer getChangeViewBuffer() {
        return (Buffer)getContext().get(PBFT.CHANGEVIEWBUFFER);
    }

    public boolean belongsToCurrentView(PBFTMessage m) {
        return getCurrentView().equals(m.get(PBFTMessage.VIEWFIELD));
    }

    public boolean exists(PBFTMessage m, Buffer buffer){

        for(Object item : buffer){
            PBFTMessage m1 = (PBFTMessage) item;

            boolean viewCheck   = m1.get(PBFTMessage.VIEWFIELD).equals(m.get(PBFTMessage.VIEWFIELD));
            boolean digestCheck = m1.get(PBFTMessage.DIGESTFIELD).equals(m.get(PBFTMessage.DIGESTFIELD));
            boolean sequenceCheck = m1.get(PBFTMessage.SEQUENCENUMBERFIELD).equals(m.get(PBFTMessage.SEQUENCENUMBERFIELD));

            if(viewCheck && digestCheck && sequenceCheck){
                return true;
            }

        }

        return false;

    }
    public boolean existsPrePrepare(PBFTMessage m) {
        return exists(m, getPreprepareBuffer());
    }

    public boolean existsPrepare(PBFTMessage m) {
        return exists(m, getPrepareBuffer());
    }

    public boolean existsRequest(PBFTMessage m){
        Buffer buffer = getRequestBuffer();
        
        for(Object item : buffer){
            PBFTMessage m1 = (PBFTMessage) item;

            boolean clientCheck   = m1.get(PBFTMessage.CLIENTFIELD).equals(m.get(PBFTMessage.CLIENTFIELD));            
            boolean timestamptCheck   = m1.get(PBFTMessage.TIMESTAMPFIELD).equals(m.get(PBFTMessage.TIMESTAMPFIELD));

            if(clientCheck && timestamptCheck){
                return true;
            }

        }

        return false;
        
    }
    public int getServiceBFTResilience(){
        return (int)(Math.floor(getLocalGroup().getGroupSize()/3));
    }
    public boolean gotQuorum(PBFTMessage m){

        if(isPrepare(m)){
            return gotPrepareQuorum(m);
        }

        if(isCommit(m)){
            return gotCommitQuorum(m);
        }

        if(isReceivedReply(m)){
            return gotReceiveReplyQuorum(m);
        }

        if(isChangeView(m)){
            return gotChangeViewQuorum(m);
        }


        return false;
    }

    public boolean gotQuorum(PBFTMessage m, Buffer buffer, int minQuorum, boolean includeItsOwn){

        int quorum = 0;
        int f      = getServiceBFTResilience();

        for(Object item : buffer){

            PBFTMessage p = (PBFTMessage) item;
            boolean viewCheck   = p.get(PBFTMessage.VIEWFIELD).equals(m.get(PBFTMessage.VIEWFIELD));
            boolean digestCheck = p.get(PBFTMessage.DIGESTFIELD).equals(m.get(PBFTMessage.DIGESTFIELD));
            boolean sequenceCheck = p.get(PBFTMessage.SEQUENCENUMBERFIELD).equals(m.get(PBFTMessage.SEQUENCENUMBERFIELD));
            boolean replicaCheck = p.get(PBFTMessage.REPLICAIDFIELD).equals(m.get(PBFTMessage.REPLICAIDFIELD));

            if(!includeItsOwn){

                if(viewCheck && digestCheck && sequenceCheck && !replicaCheck){
                    quorum++;
                }

            }else{

                if(viewCheck && digestCheck && sequenceCheck){
                    quorum++;
                }
                
            }

        }

        return (quorum >= minQuorum);
    }

    public boolean gotReceiveReplyQuorum(PBFTMessage m){

        int f      = getServiceBFTResilience();

        int quorum = 0;        

        Buffer buffer = getRequestBuffer();
        Buffer replicas = new Buffer();
        
        for(Object item : buffer){

            PBFTMessage p = (PBFTMessage) item;
            boolean clientCheck   = p.get(PBFTMessage.CLIENTFIELD).equals(m.get(PBFTMessage.CLIENTFIELD));
            boolean digestCheck = p.get(PBFTMessage.DIGESTFIELD).equals(m.get(PBFTMessage.DIGESTFIELD));
            
            Object i  = p.get(PBFTMessage.REPLICAIDFIELD);
            
            if(!replicas.contains(i) && clientCheck && digestCheck){
                quorum ++;
                replicas.add(i);
            }

        }

        return (quorum >= f + 1);
    }

    public boolean gotPrepareQuorum(PBFTMessage m){

        int f      = getServiceBFTResilience();

        return gotQuorum(m, getPrepareBuffer(), 2 * f, false);
        
    }

    public boolean gotCommitQuorum(PBFTMessage m){
        
        int f      = getServiceBFTResilience();

        return gotQuorum(m, getCommitBuffer(), 2 * f + 1, true);

    }

    public boolean gotChangeViewQuorum(PBFTMessage m){

        int f      = getServiceBFTResilience();

        return gotQuorum(m, getChangeViewBuffer(), f + 1, true);

    }

    public boolean isPrepare(PBFTMessage m){
        return m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.RECEIVEPREPARE);
    }

    public boolean isCommit(PBFTMessage m){
        return m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.RECEIVECOMMIT);
    }

    public boolean isChangeView(PBFTMessage m){
        return m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.CHANGEVIEW);
    }


    public boolean isReceivedReply(PBFTMessage m){
        return m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.RECEIVEREPLY);
    }

    public Integer getCheckPointPeriod() {
        return ((Integer)getContext().get(PBFT.CHECKPOINTPERIOD));
    }
    
    public void setCheckPointPeriod(Integer period) {
        getContext().put(PBFT.CHECKPOINTPERIOD, period);
    }

    public static String __getRequestID(PBFTMessage m){
        
        String client =                
            ((br.ufba.lasid.jds.Process)m.get(PBFTMessage.CLIENTFIELD)).getID().toString();

        String timestamp = ((Long)m.get(PBFTMessage.TIMESTAMPFIELD)).toString();

        String payload   = m.get(PBFTMessage.PAYLOADFIELD).toString();

        return client + "." + timestamp + "." + payload;
        
    }

    public static boolean isABufferedMessage(Buffer buffer, PBFTMessage m){
        /* check if request exists in the buffer */
        PBFTMessage bm = getBufferedMessage(buffer, m);

        if(bm == null)
            return false;

        return bm.getID().equals(m.getID());//getRequestID(bRequest).equals(getRequestID((PBFTMessage)request));
        
    }

    public static PBFTMessage getBufferedMessage(Buffer buffer, PBFTMessage m){
        
        for(Object item : buffer){

            PBFTMessage bm = (PBFTMessage)item;

            if(bm.getID().equals(m.getID())){//getRequestID(bRequest).equals(getRequestID((PBFTMessage)request))){

                return bm;

            }
        }
    
        return null;
        
    }
    public static boolean hasBeenAlreadyServed(Buffer buffer, PBFTMessage request) {
        PBFTMessage bRequest = getBufferedMessage(buffer, request);

        if(bRequest == null)
            return false;

        Object done = bRequest.get(PBFTMessage.REQUESTDONEFIELD);
        
        if(done == null)
            return false;

        return (Boolean) done;

    }

}
