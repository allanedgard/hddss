/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.jbft.pbft;

import trash.br.ufba.lasid.jds.IData;
import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.Quorum;
import trash.br.ufba.lasid.jds.comm.QuorumTable;
import br.ufba.lasid.jds.cs.IClient;
import trash.br.ufba.lasid.jds.cs.ClientServerProtocol;
import br.ufba.lasid.jds.cs.IServer;
import trash.br.ufba.lasid.jds.util.Wrapper;
import trash.br.ufba.lasid.jds.factories.PBFTActionFactory;
import br.ufba.lasid.jds.group.IGroup;
import trash.br.ufba.lasid.jds.jbft.pbft.comm.PBFTBatchMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTNewView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import trash.br.ufba.lasid.jds.jbft.pbft.comm.AbstractPBFTServerMessage;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTTimeoutDetector;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTLogEntry;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTRequestMessageList;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTStateLog;
import br.ufba.lasid.jds.util.TaskTable;
import br.ufba.lasid.jds.security.IAuthenticator;
import br.ufba.lasid.jds.util.IPayload;
import trash.br.ufba.lasid.jds.util.Buffer;
import br.ufba.lasid.jds.util.IClock;
import trash.br.ufba.lasid.jds.util.IDebugger;
import br.ufba.lasid.jds.util.IScheduler;
import java.util.Collection;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.BlockingBuffer;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;

/**
 * Pratical Byzantine Fault Tolerant Protocol (Castro and Liskov, 1999) <br>
 * @author Alirio Sá (aliriosa at ufba dot br)
 */
public class PBFT2 extends ClientServerProtocol{

    public static String DEBUGGER = "__DEBUGGER";
    public static String LOCALGROUP = "__LOCALGROUP";
    public static String GROUPLEADER = "__GROUPLEADER";
    public static String SCHEDULER  = "__SCHEDULER";
    public static String PREPREPARETIMEOUT = "__PREPREPARETIMEOUT";
    public static String LATEPRIMARYTIMEOUT = "__LATEPRIMAYTIMEOUT";
    public static String CLIENTRETRANSMISSIONTIMEOUT = "__CLIENTRETRANSMISSIONTIMEOUT";

    public static String CLIENT2SERVERAUTHENTICATOR = "__CLIENT2SERVERAUTHENTICATOR";
    public static String SERVER2CLIENTATHENTICATOR = "__SERVER2CLIENTATHENTICATOR";
    public static String SERVER2SERVERAUTHENTICATOR = "__SERVER2SERVERAUTHENTICATOR";
    
    public static String CURRENTVIEW = "__CURRENTVIEW";
    public static String ALLOWABLENUMBEROFFAULTREPLICAS = "__ALLOWABLENUMBEROFFAULTREPLICAS";
    public static String CLOCKSYSTEM = "__CLOCKSYSTEM";
    public static String REQUESTBUFFER = "__REQUESTBUFFER";
    public static String PREPREPAREBUFFER = "__PREPREPAREBUFFER";
    public static String PREPAREBUFFER = "__PREPAREBUFFER";
    public static String COMMITBUFFER = "__COMMITBUFFER";
    public static String CHANGEVIEWBUFFER = "__CHANGEVIEWBUFFER";
    public static String CHANGEVIEWACKBUFFER = "__CHANGEVIEWACKBUFFER";
    public static String COMMITTEDBUFFER = "__COMMITTEDBUFFER";
    public static String REPLYBUFFER = "__REPLYBUFFER";
    public static String CHECKPOINTBUFFER = "__CHECKPOINTBUFFER";
    public static String CLIENTAUTHENTICATOR = "__CLIENTAUTHENTICATOR";
    public static String PRIMARYFAULTTIMEOUT = "__PRIMARYFAULTYTIMEOUT";
    public static String BATCHINGSIZE        = "__BATCHINGSIZE";
    public static String BATCHINGTIMEOUT = "__BATCHINGTIMEOUT";
    //public static String BATCHSCHEDULER = "__BATCHSCHEDULER";
    //public static String REJUVENATIONSCHEDULER = "__REJUVENATIONSCHEDULER";
    public static String CHECKPOINTPERIOD = "__CHECKPOINTPERIOD";
    public static String CHECKPOINTFACTOR = "__CHECKPOINTFACTOR";
    public static String CHECKPOINTNUMBER = "__CHECKPOINTNUMBER";
    public static String LASTCHECKPOINT  = "__LASTCHECKPOINT";
    public static String REJUVENATIONWINDOW  = "__REJUVENATIONWINDOW";
    public static String PREPARESTATEINFORMATION = "__PREPARESTATEINFORMATION";
    public static String PREPREPARESTATEINFORMATION = "__PREPREPARESTATEINFORMATION";
    public static String CHANGEVIEWCERTIFICATEBUFFER = "__CHANGEVIEWCERTIFICATEBUFFER";
    public static String CHECKPOINTSTATEINFORMATION = "__CHECKPOINTSTATEINFORMATION";
    //public static String CHANGEVIEWRETRANSMITIONSCHEDULER = "__CHANGEVIEWRETRANSMITIONSCHEDULER";

    public static String VIEWCHANGERETRANSMITIONTIMEOUT = "__VIEWCHANGERETRANSMITIONTIMEOUT";

    public static String PREPREPAREQUORUMSTORE = "__PREPREPAREQUORUMSTORE";
    public static String PREPAREQUORUMSTORE = "__PREPAREQUORUMSTORE";
    public static String COMMITQUORUMSTORE = "__COMMITQUORUMSTORE";
    public static String REPLYQUORUMSTORE = "__REPLYQUORUMSTORE";
    public static String CHECKPOINTQUORUMSTORE = "__CHECKPOINTQUORUMSTORE";
    
    protected boolean lock = false;
    
    protected int viewChangeAttemps = 0;

    protected PBFTStateLog state = new PBFTStateLog();

    protected TaskTable timeoutTasks = new TaskTable();

    public PBFTStateLog getState(){
        return state;
    }

    public void initViewChangeAttemps(){

        this.viewChangeAttemps = 0;
        
    }

    public int getViewChangeAttemps(){
        return this.viewChangeAttemps;
    }

    public int incViewChangeAttemps(){
        return ++ this.viewChangeAttemps ;
    }



    public void lock(){
        this.lock = true;
    }

    public void unlock(){
        this.lock = false;
    }

    public boolean isLooked(){
        return this.lock;
    }

    public boolean isUnlooked(){
        return (!isLooked());
    }
/*
    public PBFTTuple getPrepareStateInformation(){
        return (PBFTTuple)getContext().get(PREPARESTATEINFORMATION);
    }

    public PBFTTuple getPrePrepareStateInformation(){
        return (PBFTTuple)getContext().get(PREPREPARESTATEINFORMATION);
    }

    public void setPrepareStateInformation(PBFTTuple tuple){
        getContext().put(PREPARESTATEINFORMATION, tuple);
    }

    public void setPrePrepareStateInformation(PBFTTuple tuple){
        getContext().put(PREPREPARESTATEINFORMATION, tuple);
    }

    public void initPrePrepareStateInformation(){
        PBFTTuple tuple = getPrePrepareStateInformation();
        if(tuple == null){
            tuple = new PBFTTuple();
        }

        tuple.clear();

        setPrePrepareStateInformation(tuple);
    }

    public void initPrepareStateInformation(){
        PBFTTuple tuple = getPrepareStateInformation();
        if(tuple == null){
            tuple = new PBFTTuple();
        }

        tuple.clear();

        setPrepareStateInformation(tuple);
    }

    public void initCheckpointStateInformation(){
        PBFTTuple tuple = getCheckpointedStateInformation();
        if(tuple == null){
            tuple = new PBFTTuple();
        }

        tuple.clear();

        setCheckpointStateInformation(tuple);
    }

    public boolean hasViewConsistentInformation(Buffer state){
        
        for(Object item : state){

            String ID = (String) item;
            
            try{

                String viewString = (ID.split(":"))[2];

                Integer view = new Integer(viewString);

                if(view.compareTo(getCurrentView()) > 0){

                    return false;

                }
                
            }catch(Exception e){

                return false;
                
            }
        }

        return true;
    }
    
    public PBFTTuple updateStateInformation(PBFTTuple state, PBFTMessage m, Integer view, Long seqn){

        if(state == null){

            state = new PBFTTuple();

        }
        
        PBFTMessage batch = (PBFTMessage) m.get(PBFTMessage.REQUESTFIELD);

        if(m instanceof PBFTBatchMessage){
            batch = m;
        }
        
        PBFTTuple tuple = new PBFTTuple();
        
        tuple.put(PBFTMessage.SEQUENCENUMBERFIELD, seqn);
        tuple.put(PBFTMessage.VIEWFIELD, view);
        tuple.put(PBFTMessage.DIGESTFIELD, batch.get(PBFTMessage.DIGESTFIELD));
        
        String tupleID = getLocalProcess().getID().toString() + ":" + batch.getID();

        state.put(tupleID, tuple);

        return state;
    }
    
    public void updatePrepareStateInformation(PBFTMessage prepare, Integer view, Long seqn){
        
        setPrepareStateInformation(
            updateStateInformation(
              getPrepareStateInformation(), prepare, view, seqn
            )
        );
        
    }

    public void updatePrePrepareStateInformation(PBFTMessage preprepare, Integer view, Long seqn){
        setPrePrepareStateInformation(
            updateStateInformation(
              getPrePrepareStateInformation(), preprepare, view, seqn
            )
        );



    }

    public void setLastCheckpoint(PBFTMessage checkpoint){
        getContext().put(LASTCHECKPOINT, checkpoint);
    }

    public PBFTMessage getLastCheckpoint(){
        return (PBFTMessage)(getContext().get(LASTCHECKPOINT));
    }

    

    public Long getLastCheckpointSequenceNumber(){

        Buffer buffer = getCommittedBuffer();

        long chk = -1;

        for(Object item : buffer){

            AbstractPBFTServerMessage m = (AbstractPBFTServerMessage) item;

            long seq = (Long)m.getSequenceNumber();

            if(seq > chk) chk = seq;

        }

        if(chk < 0){

            AbstractPBFTServerMessage checkpoint = (AbstractPBFTServerMessage)getLastCheckpoint();

            if(checkpoint != null){

                chk =  (Long)checkpoint.getSequenceNumber();
                
            }

        }

        return new Long(chk);
    }
*/
    public Long getCheckpointLowWaterMark(){
        return new Long(getState().getCheckpointLowWaterMark());

    }

    public byte getCheckPointFactor(){
        return (Byte)getContext().get(CHECKPOINTFACTOR);
    }
    
    public Long getCheckpointHighWaterMark(){
        return new Long(getState().getCheckpointHighWaterMark(getCheckPointPeriod(), getCheckPointFactor()));
    }
    
    /*
        
        
    public  boolean isValidSequenceNumber(PBFTMessage m) {
        long low  = getCheckpointLowWaterMark().longValue();
        long high = getCheckpointHighWaterMark().longValue();

        long SEQMessage = (Long)m.get(PBFTMessage.SEQUENCENUMBERFIELD);

        return ((SEQMessage >= low) && (SEQMessage <= high));

    }
*/

/*
    public Buffer getCheckpointBuffer() {
        return (Buffer)getContext().get(PBFT2.CHECKPOINTBUFFER);
    }

    public Buffer getCommittedBuffer() {
        return (Buffer)getContext().get(PBFT2.COMMITTEDBUFFER);
    }

    public Buffer getReplyBuffer() {
        return (Buffer)getContext().get(PBFT2.REPLYBUFFER);
    }

    public boolean existsPrePrepareForRequest(PBFTMessage m) {
        Buffer buffer = getPrePrepareBuffer();

        for(Object item : buffer){

            PBFTMessage preprepare = (PBFTMessage) item;

            PBFTMessage batch =
                (PBFTMessage) preprepare.get(PBFTMessage.REQUESTFIELD);

            int batchSize = (Integer)batch.get(PBFTMessage.BATCHSIZEFIELD);

            for(int r = 0; r < batchSize; r++){

                String requestField = getRequestField(r);
                
                PBFTMessage request = (PBFTMessage)batch.get(requestField);

                if(request.getID().equals(m.getID())){

                    return true;
                    
                }
            }
        }

        return false;
    }

    public Buffer getChangeViewAckBuffer() {

        return (Buffer)getContext().get(PBFT2.CHANGEVIEWACKBUFFER);
        
    }

    public PBFTMessage getChangeViewSentFromSelectedPrimary(PBFTMessage m) {

        Buffer buffer = getChangeViewBuffer();
        Integer theView  = (Integer)m.get(PBFTMessage.VIEWFIELD);

        Integer ID = new Integer(Integer.MAX_VALUE);
        
        PBFTMessage result = null;

        for(Object item : buffer){

            PBFTMessage cv  = (PBFTMessage) item;
            Integer myView = (Integer) cv.get(PBFTMessage.VIEWFIELD);

            if(myView.equals(theView)){

                if((((Integer)cv.get(PBFTMessage.REPLICAIDFIELD)).compareTo(ID)) < 0){

                    result = cv;

                    ID = ((Integer)cv.get(PBFTMessage.REPLICAIDFIELD));

                }

            }

        }

        return result;
        
    }

    public PBFTTuple getCheckpointedStateInformation() {
        return (PBFTTuple)getContext().get(CHECKPOINTSTATEINFORMATION);
    }

    public PBFTTuple selectHighestCheckpointTupleFromChangeView() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void updateCheckpointStateInformation(PBFTMessage checkpoint, Integer view, Long seqn) {
        setCheckpointStateInformation(
            updateStateInformation(
                getCheckpointedStateInformation(), checkpoint, view, seqn
            )
        );

    }

    public void setCheckpointStateInformation(PBFTTuple tuple) {
        getContext().put(CHECKPOINTSTATEINFORMATION, tuple);
    }

    public enum BATCHSTATE{
        NOBATCH, INBATCH, BATCHED
    }


    public int BATCHINGCOUNT = 0;
    public Long lastCommitedSequenceNumber = new Long(0);
*/
    protected static long SEQ = 0;

    public static long newSequenceNumber(){
        return ++SEQ;
    }

    public static long getCurrentSequenceNumber(){
        return SEQ;
    }

    public static void updateCurrentSequenceNumber(long sqn){
        SEQ = sqn;
    }
    /*
    public String getRequestField(){
        return getRequestField(BATCHINGCOUNT);
    }

    public long getLastStableStateSequenceNumber(){
        Buffer buffer = getCommittedBuffer();
        
        if(buffer.isEmpty()){

            return -1;

        }


        PBFTMessage m = (PBFTMessage) buffer.get(buffer.size()-1);
        return (Long)m.get(PBFTMessage.SEQUENCENUMBERFIELD);
        
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
*/
    public int getMaxBatchSize(){
        return (((Integer)getContext().get(PBFT2.BATCHINGSIZE)).intValue());
    }
/*
    public boolean batchIsNotComplete(){
        return (getBatchingCount() < getMaxBatchSize());
    }

    public void initBatching(){
        BATCHINGCOUNT = 0;
    }
     * 
     */
    @Override
    public void doAction(Wrapper w){
       //System.out.println("[Protocol] call Protocol.perform");
       perform(PBFTActionFactory.create(w));
    }

    public Long getBatchingTimeout(){
        return (Long)getContext().get(PBFT2.BATCHINGTIMEOUT);
    }

    public Long getChangeViewRetransmissionTimeout(){
        return (Long)getContext().get(PBFT2.VIEWCHANGERETRANSMITIONTIMEOUT);
    }


    public Long getRetransmissionTimeout(){
        return (Long)getContext().get(PBFT2.CLIENTRETRANSMISSIONTIMEOUT);
    }

    public Long getPrimaryFaultyTimeout(){
        return (Long)getContext().get(PBFT2.PRIMARYFAULTTIMEOUT);
    }

    public void setPrimaryFaultTimeout(Long timeout){
        getContext().put(PBFT2.PRIMARYFAULTTIMEOUT, timeout);
    }

    public IDebugger getDebugger(){
        return (IDebugger) getContext().get(PBFT2.DEBUGGER);
    }

    public IScheduler getScheduler(){
        return (IScheduler)(getContext().get(PBFT2.SCHEDULER));
    }

/*
    public IScheduler getChangeViewRetransmittionScheduler(){
        return (IScheduler)(getContext().get(PBFT2.CHANGEVIEWRETRANSMITIONSCHEDULER));
    }


    public IScheduler getClientScheduler(){
        return (IScheduler)(getContext().get(PBFT2.CLIENTSCHEDULER));
    }

    public IScheduler getServerScheduler(){
        return (IScheduler)(getContext().get(PBFT2.SERVERSCHEDULER));
    }

    public IScheduler getBatchingScheduler(){
        return (IScheduler)(getContext().get(PBFT2.BATCHSCHEDULER));
    }

    public IScheduler getRejuvenationScheduler(){
        return (IScheduler)(getContext().get(PBFT2.REJUVENATIONSCHEDULER));
    }

    public IScheduler getPrimaryFDScheduler(){
        return (IScheduler)(getContext().get(PBFT2.PRIMARYFDSCHEDULER));
    }
*/
    public IAuthenticator getServerToServerAuthenticator(){
        return (IAuthenticator)(getContext().get(PBFT2.SERVER2SERVERAUTHENTICATOR));
    }

    public IAuthenticator getClientToServerAuthenticator(){
        return (IAuthenticator)(getContext().get(PBFT2.CLIENT2SERVERAUTHENTICATOR));
    }

    public IAuthenticator getServerToClientAuthenticator(){
        return (IAuthenticator)(getContext().get(PBFT2.SERVER2CLIENTATHENTICATOR));
    }
    public synchronized Buffer getRequestBuffer(){
        return ((Buffer)(getContext().get(PBFT2.REQUESTBUFFER)));
    }
    
    public void setGroupLeader(Object ID){
        getContext().put(PBFT2.GROUPLEADER, ID);
    }

    public Integer getCurrentView(){
        return (Integer)getContext().get(PBFT2.CURRENTVIEW);
    }

    public void setCurrentView(Integer v){
        getContext().put(PBFT2.CURRENTVIEW, v);
    }

    public IGroup getLocalGroup(){
        return (IGroup)getContext().get(PBFT2.LOCALGROUP);
    }
/*
    public Buffer getPrePrepareBuffer() {
        return (Buffer)getContext().get(PBFT2.PREPREPAREBUFFER);
    }

    public Buffer getPrepareBuffer() {
        return (Buffer)getContext().get(PBFT2.PREPAREBUFFER);
    }

    public Buffer getCommitBuffer() {
        return (Buffer)getContext().get(PBFT2.COMMITBUFFER);
    }

    public Buffer getChangeViewBuffer() {
        return (Buffer)getContext().get(PBFT2.CHANGEVIEWBUFFER);
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
        return exists(m, getPrePrepareBuffer());
    }

    public boolean existsPrepare(PBFTMessage m) {
        return exists(m, getPrepareBuffer());
    }

    public boolean existsRequest(PBFTMessage m){
        Buffer buffer = getRequestBuffer();
        
        for(Object item : buffer){
            PBFTMessage m1 = (PBFTMessage) item;

            boolean clientCheck   = m1.get(PBFTMessage.CLIENTIDFIELD).equals(m.get(PBFTMessage.CLIENTIDFIELD));
            boolean timestamptCheck   = m1.get(PBFTMessage.TIMESTAMPFIELD).equals(m.get(PBFTMessage.TIMESTAMPFIELD));

            if(clientCheck && timestamptCheck){
                return true;
            }

        }

        return false;
        
    }
 * 
 */
    public int getServiceBFTResilience(){
        return (int)(Math.floor(getLocalGroup().getGroupSize()/3));
    }
    /*
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

        if(isChangeViewAck(m)){
            return gotChangeViewAckQuorum(m);
        }

        if(isCheckpoint(m)){
            return gotCheckpointQuorum(m);
        }


        return false;
    }

    public synchronized void garbage(long seq){
        Buffer buffer = getCommittedBuffer();
        int size = buffer.size();
        
        for(int i = size-1; i >= 0; i--){

            PBFTMessage batch = (PBFTMessage) buffer.get(i);
            
            long seqn = (Long)batch.get(PBFTMessage.SEQUENCENUMBERFIELD);

            if(seqn <= seq){

                int batchSize =  (Integer)batch.get(PBFTMessage.BATCHSIZEFIELD);

                for(int j = 0; j < batchSize; j++){

                    String reqField = getRequestField(j);

                    PBFTMessage req = (PBFTMessage)batch.get(reqField);
                    PBFTMessage rep = req;

                    req = getBufferedMessage(getRequestBuffer(), req);
                    rep = getBufferedMessage(getReplyBuffer(), rep);

                    getRequestBuffer().remove(req);
                    getReplyBuffer().remove(rep);
                }
                
                buffer.remove(batch);
                
            }

            System.gc();
            
        }

        int size2 = getPrePrepareBuffer().size();

        for(int j = size2-1; j >=0; j --){
            PBFTMessage pp = (PBFTMessage)getPrePrepareBuffer().get(j);
            long SEQ2 = (Long)pp.get(PBFTMessage.SEQUENCENUMBERFIELD);
            if(SEQ2 <= seq){
                getPrePrepareBuffer().remove(pp);
            }
        }

        size2 = getPrepareBuffer().size();

        for(int j = size2-1; j >=0; j --){
            PBFTMessage pp = (PBFTMessage)getPrepareBuffer().get(j);
            long SEQ2 = (Long)pp.get(PBFTMessage.SEQUENCENUMBERFIELD);
            if(SEQ2 <= seq){
                getPrepareBuffer().remove(pp);
            }
        }

        size2 = getCommitBuffer().size();

        for(int j = size2-1; j >=0; j --){
            PBFTMessage pp = (PBFTMessage)getCommitBuffer().get(j);
            long SEQ2 = (Long)pp.get(PBFTMessage.SEQUENCENUMBERFIELD);
            if(SEQ2 <= seq){
                getCommitBuffer().remove(pp);
            }
        }

        size2 = getCheckpointBuffer().size();
        
        for(int j = size2-1; j >=0; j --){
            PBFTMessage pp = (PBFTMessage)getCheckpointBuffer().get(j);
            long SEQ2 = (Long)pp.get(PBFTMessage.SEQUENCENUMBERFIELD);
            if(SEQ2 < seq){
                getCheckpointBuffer().remove(pp);
            }
        }

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

        Buffer buffer = getReplyBuffer();
        Buffer replicas = new Buffer();
        
        for(Object item : buffer){

            PBFTMessage p = (PBFTMessage) item;
            
            boolean clientCheck   = p.get(PBFTMessage.CLIENTIDFIELD).equals(m.get(PBFTMessage.CLIENTIDFIELD));
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

    public boolean gotCheckpointQuorum(PBFTMessage m){
        int f      = getServiceBFTResilience();
        return gotQuorum(m, getCheckpointBuffer(), 2 * f + 1, false);
    }
    
    public boolean gotCommitQuorum(PBFTMessage m){
        
        int f      = getServiceBFTResilience();

        return gotQuorum(m, getCommitBuffer(), 2 * f + 1, true);

    }

    public boolean gotChangeViewQuorum(PBFTMessage m){

        int f      = getServiceBFTResilience();
        int minQuorum = 2 * f + 1;
        int quorum = 0;
        Buffer buffer = getChangeViewBuffer();

        for(Object item : buffer){

            PBFTMessage p = (PBFTMessage) item;
            
            boolean viewCheck   = p.get(PBFTMessage.VIEWFIELD).equals(m.get(PBFTMessage.VIEWFIELD));
            //boolean digestCheck = p.get(PBFTMessage.DIGESTFIELD).equals(m.get(PBFTMessage.DIGESTFIELD));
            //boolean chknumber = p.get(PBFTMessage.CHECKPOINTLOWWATERMARK).equals(m.get(PBFTMessage.CHECKPOINTLOWWATERMARK));

            if(viewCheck){// && digestCheck && chknumber){
                
                quorum++;
            }

        }

        return (quorum >= minQuorum);


//        return gotQuorum(m, getChangeViewBuffer(), 2 * f - 1, false);

    }

    public boolean gotChangeViewAckQuorum(PBFTMessage m){

        int f      = getServiceBFTResilience();
        int minQuorum = 2 * f;
        int quorum = 0;
        Buffer buffer = getChangeViewAckBuffer();

        for(Object item : buffer){

            PBFTMessage p = (PBFTMessage) item;

            boolean viewCheck   = p.get(PBFTMessage.VIEWFIELD).equals(m.get(PBFTMessage.VIEWFIELD));
            //boolean digestCheck = p.get(PBFTMessage.DIGESTFIELD).equals(m.get(PBFTMessage.DIGESTFIELD));
            boolean primary = p.get(PBFTMessage.REPLICAIDRECEIVERFIELD).equals(m.get(PBFTMessage.REPLICAIDRECEIVERFIELD));


            if(viewCheck &&  primary){
                quorum++;
            }

        }

        return (quorum >= minQuorum);

    }

    public boolean isCheckpoint(PBFTMessage m){
        return m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.RECEIVECHECKPOINT);
    }

    public boolean isPrepare(PBFTMessage m){
        return m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.RECEIVEPREPARE);
    }

    public boolean isCommit(PBFTMessage m){
        return m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.RECEIVECOMMIT);
    }

    public boolean isChangeView(PBFTMessage m){
        return m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.RECEIVECHANGEVIEW);
    }

    public boolean isChangeViewAck(PBFTMessage m){
        return m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.RECEIVECHANGEVIEWACK);
    }


    public boolean isReceivedReply(PBFTMessage m){
        return m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.RECEIVEREPLY);
    }
*
     * 
     */
    public synchronized Long getCheckPointPeriod() {
        return ((Long)getContext().get(PBFT2.CHECKPOINTPERIOD));
    }
    
    public synchronized void setCheckPointPeriod(Long period) {
        getContext().put(PBFT2.CHECKPOINTPERIOD, period);
    }
/*
    public static String __getRequestID(PBFTMessage m){
        
        String client =                
            ((br.ufba.lasid.jds.IProcess)m.get(PBFTMessage.CLIENTIDFIELD)).getID().toString();

        String timestamp = ((Long)m.get(PBFTMessage.TIMESTAMPFIELD)).toString();

        String payload   = m.get(PBFTMessage.PAYLOADFIELD).toString();

        return client + "." + timestamp + "." + payload;
        
    }

    public static boolean isABufferedMessage(Buffer buffer, PBFTMessage m){
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

    public Buffer getChangeViewCertificate(){
        return (Buffer) getContext().get(PBFT2.CHANGEVIEWCERTIFICATEBUFFER);
    }

    public void setChangeViewCertificate(Buffer buffer){
        getContext().put(PBFT2.CHANGEVIEWCERTIFICATEBUFFER, buffer);
    }


 * 
 */
    /**************************************************************************
     * Constructors.
     **************************************************************************/
    public PBFT2(){
        init();
    }

    /**
     * Initialize the context variables.
     */
    protected void init(){

        /**
         * Initialize the input buffer.
         */
        getContext().put(
          INBUFFER, BufferUtils.blockingBuffer(new UnboundedFifoBuffer())
        );

        /**
         * Initialize the output buffer
         */
        getContext().put(
          OUTBUFFER, BufferUtils.blockingBuffer(new UnboundedFifoBuffer())
        );

        (new EmitterTask()).start();
        (new AccepterTask()).start();
        
    }

    /**************************************************************************
     * PBFT2' IClient related actions.
     * 1. acceptPayload payload, createRequestMessage and buffer request.
     * [acceptPayload(payload):void]
     *
     * 2. perform the client request for all buffered requests which has not
     * been sent yet. [emitRequest(): void]
     *  -> it multicast the request to the server group and schedule the request
     *     retransmission.
     *
     * 3. acceptPayload responses from server replicas.
     * [acceptPayload(reply):void]
     *
     * 4. deliver correct results to the aplication. [emitDeliver():void]
     **************************************************************************/

    
    /**
     * Collect the payload from the application.
     * @param payload -- the application payload.
     */
    protected boolean acceptPayload(IPayload payload){
        
        PBFTRequest request = createRequestMessage(payload);

        return false; //addToBuffer(request, getBuffer(OUTBUFFER));
        
        
    }

    /**
     * Keep the timestamp of the previous request sent.
     */
    private Long lastClientTimestamp = new Long(-1);//null;

    /**
     * Keep the timestamp of the current request sent.
     */
    private Long currClientTimestamp = new Long(-1);//null;
    
    /**
     * Create a new PBFT2' IClient request message.
     * @param payload -- the application payload.
     * @return -- a new pbft request message.
     */
    protected PBFTRequest createRequestMessage(IPayload payload){

        IProcess client = getLocalProcess();

        PBFTRequest r = new PBFTRequest(payload, getTimestamp(), client.getID());

//        r.setSourceID(client);
  //      r.setDestinationID(getRemoteProcess());
        r.setSent(false);

        IAuthenticator authenticator =
                getClientToServerAuthenticator();

        //r = (PBFTRequest) authenticator.encrypt(r);

        return r;

    }


    /**
     * do a request to server group and schedule the request retransmission.
     * @param request -- the client request.
     */
    protected synchronized void emitRequest(PBFTRequest request){
        
        boolean SENT = request.wasSent();

        if(!SENT){
            
            lastClientTimestamp = currClientTimestamp;
            currClientTimestamp = request.getTimestamp();
//             request.setLastTimestamp(lastClientTimestamp);
            //request.put("LASTTIMESTAMP", lastClientTimestamp);

            multicast(request);
            clientSchedule(request);
            request.setSent(true);
            //request.put(PBFTMessage.SENDSTATUS, true);

        }
        
    }

    /**
     * schedule the retransmition of a request.
     * @param request
     */
    protected void clientSchedule(PBFTRequest request){

        PBFTTimeoutDetector timeoutTask =
                new PBFTTimeoutDetector()
        {

            @Override
            public void onTimeout() {
                
                PBFTRequest r = (PBFTRequest) get("REQUEST");

                r.setSent(false);
                //r.put(PBFTMessage.SENDSTATUS, false);

                emitRequest(r);
                
            }

            @Override
            public void cancel(){
                IScheduler scheduler = (IScheduler) get("SCHEDULER");
                scheduler.cancel(this);
            }
            
        };
        
        IScheduler scheduler = getScheduler();

        timeoutTask.put("REQUEST", request);
        timeoutTask.put("SCHEDULER", scheduler);

        //getTimeoutTasks().put(request.getID(), timeoutTask);


        scheduler.schedule(

           timeoutTask, getTimestamp() + getRetransmissionTimeout()
           
        );
        
    }


    /**
     * Collect a reply message sent by a replica. It's also responsable by
     * building the reply quorum.
     */
    protected boolean acceptReply(PBFTReply m){
        
        /**
         * If m is a valid server message.
         */
//        if(!getServerToClientAuthenticator().check(m)){
//            return false;
//        }

        /**
         * If the related quorum wasn't complete then add the current
         * message to the related quorum.
         */
        
        Quorum q = null;//getQuorum(REPLYQUORUMSTORE, m);

        if(!(q != null && q.complete())){

            int f = getServiceBFTResilience();

            //q = addToQuorum(m, REPLYQUORUMSTORE, f + 1);


            /**
             * If the current quorum was complete then update the PBFT2'
             * Log state and add a reply message to output buffer.
             */
            if(q.complete()){
                
                /**
                 * When a valid server reply is accepted, the client request
                 * retransmition task timeout must be cancelled.
                 */

                //cancelTimers(m.getID());

                //IPayload result = (IPayload) m.getContent();


                return false; //addToBuffer(result, getBuffer(OUTBUFFER));
                
            }
        }

        return false;
    }
    

    /**
     * Deliver a server responses to from local client application and cancel
     * any retransmission timeout related to the respective client request.
     */
    protected void emitDeliver(IPayload result){


        IClient client = (IClient) getLocalProcess();

        client.receiveResult(result);

    }


    /**************************************************************************
     * PBFT2' IServer  actions in Pre-prepare phase.
     * 1a. the primary replica collects and batches the request messages sent by
     * the clients [acceptPayload(request):void]
     *      -> the batch procedure schedules batch timeout.
     *
     * 1b. the secundary replicas acceptPayload requests and schedule a view change
     * for the case a late pre-prepares. [acceptPayload(request):void]
     *
     * 2.  the primary replica creates and multicasts preprepare messages to the
     * secundary replicas. [emitPrePrepare():void]
     *
     * 3. the secundary replica collects the pre-prepare messages sent by the 
     * primary replica. [acceptPayload(pre-prepare):void]
     **************************************************************************/


    /**
     * Collect the request sent by the client.
     * @param request -- the client request.
     */
    protected boolean acceptRequest(PBFTRequest request){

        /**
         * If it isn't a valid request then it'll be discarded.
         */

//        if(!getClientToServerAuthenticator().check(request)){
//
//            return false;
//
//        }
        
        /**
         * Check if request was already accepted.
         */
        if(getState().wasAccepted(request)){

            /**
             * Check if request was already served.
             */
            if(getState().wasServed(request)){

                /**
                 * Retransmite the reply for a request that had been already
                 * served.
                 */

//                emitReply(getState().getReply(request));
                
            }
            
            return false;
            
        }

        /**
         * If the reply there is no more in the current state then PBFT2'll send
         * a reply if null payload.
         */
        if(getState().noMore(request)){

            emitReply(createReplyMessage(null, request));
            return false;

        }

        /**
         * If a request is new then it will be accepted and put it in back log
         * state.
         */
//        getState().putInBacklog(request);

        /**
         * Perform the batch procedure if the server is the primary replica.
         */
        if(isPrimary()){

            batch(request);
            
            return true;
            
        }

        /**
         * Schedule a timeout for the arriving of the pre-prepare message if 
         * the server is a secundary replica.
         */
        serverScheduleByRequest(request);
        return true;

        
    }


    /**
     * keep the current batch of requests.
     */
    protected PBFTBatchMessage currentBatch = null;

    /**
     * Get the current batch. If the current batch is null then a new batch'll
     * be created and the batch timeout will be scheduled. Otherwise, if the
     * current batch is complete then the current batch will be buffered and
     * a new batch will again be create and its timeout will be scheduled.
     * @return  -- the current batch;
     */
    protected synchronized PBFTBatchMessage getCurrentBatch() {

        /**
         * If the current batch is null then a new batch'll be created and the
         * batch timeout will be scheduled.
         */
        if(currentBatch == null){

            currentBatch = createBatchMessage();
            
            serverScheduleByBatch(currentBatch);
            
        }else{

            /**
             * If the current batch is complete then the current batch is
             * buffered and a new batch will be created and its respective
             * timeout will be scheduled.
             */
            if(isACompletedBatch(currentBatch)){

                addCurrentBatchToBuffer();

                currentBatch = createBatchMessage();

                serverScheduleByBatch(currentBatch);
                
            }
        }

        return currentBatch;
    }

    /**
     * Assign a new value to the current batch.
     * @param newBatch -- the new batch.
     */
    protected synchronized void setCurrentBatch(PBFTBatchMessage newBatch){
        this.currentBatch = newBatch;
    }

    /**
     * Force the current batch to be completed.
     */
    protected synchronized void setCurrentBatchComplete(){

        if(currentBatch != null && currentBatch.hasRequests()){

            this.currentBatch.setComplete();

        }
    }

    /**
     * Add the current batch to output buffer
     */
    protected synchronized void addCurrentBatchToBuffer(){

        /**
         * if the current batch is not null and is not empty then it'll be
         * forced to be complete and added to batch buffer.
         */
        if(currentBatch != null && currentBatch.hasRequests()){

            currentBatch.setComplete();

            getBuffer(OUTBUFFER).add(currentBatch);

            currentBatch = null;
            
        }
        
    }
    
    /**
     * Add a request to the current request batch. If there isn't current batch
     * avail or the current batch is already complete then a new request batch
     * is created.
     * @param request -- the client request.
     */
    protected void batch(PBFTRequest request){

        /**
         * If the server isn't the primary then it isn't able to execute the
         * batch procedure.
         */
        if(!isPrimary()){

            return;
            
        }

        /**
         * Get the current batch and add the request.
         */

        PBFTBatchMessage batch = getCurrentBatch();

        batch.addRequest(request);

        
    }    

    /**
     * Create a batch request.
     * @return -- the batch message.
     */
    protected PBFTBatchMessage createBatchMessage(){

        PBFTBatchMessage batch = new PBFTBatchMessage();
//        batch.put(PBFTMessage.TIMESTAMPFIELD, getTimestamp());
//        batch.put(PBFTMessage.REPLICAIDFIELD, getLocalProcess().getID());

        return batch;
        
    }
    /**
     * Schedule a view change in case of late response for the primary.
     * @param request -- the request.
     */
    
    public void serverScheduleByRequest(PBFTRequest request){
        
        serverScheduleByRequest(request, getPrimaryFaultyTimeout());
        
    }

    /**
     * Schedule a view change in case of late response for the primary.
     * @param request -- the client request.
     * @param timeout -- the view change timeout.
     */
    public void serverScheduleByRequest(PBFTRequest request, long timeout){
        
        IScheduler scheduler = getScheduler();

        /**
         * A new timeout task is created.
         */
        PBFTTimeoutDetector timeoutTask =
                new PBFTTimeoutDetector()
        {
            /**
             * On the expiration of the timeout, perform a change view.
             */
            @Override
            public void onTimeout() {

                emitChangeView();

                Long timeout = (Long) get("TIMEOUT");
                PBFTRequest r = (PBFTRequest) get("REQUEST");

                serverScheduleByRequest(r, 2 * timeout);
            }

            @Override
            public void cancel(){
                IScheduler s = (IScheduler) get("SCHEDULER");
                s.cancel(this);
            }

        };

        timeoutTask.put("TIMEOUT", timeout);
        timeoutTask.put("REQUEST", request);
        timeoutTask.put("SCHEDULER", scheduler);

        //getTimeoutTasks().put(request.getID(), timeoutTask);

        scheduler.schedule(

           timeoutTask, getTimestamp() + timeout

        );
        
    }

    /**
     * Schedule the batch flush in case of short inter-request times.
     * @param batch -- the batch request.
     */
    public void serverScheduleByBatch(PBFTBatchMessage batch){

        IScheduler scheduler = getScheduler();
        
        PBFTTimeoutDetector timeoutTask =
                new PBFTTimeoutDetector()
        {

            /**
             * On expiration of the timeout, current batch it is buffered to be
             * pre-prepared.
             */
            @Override
            public void onTimeout() {

                addCurrentBatchToBuffer();
                
            }

            @Override
            public void cancel(){
                IScheduler scheduler = (IScheduler) get("SCHEDULER");
                scheduler.cancel(this);
            }

        };

        timeoutTask.put("SCHEDULER", scheduler);

        //batch.put("SERVERTIMEOUTTASK", timeoutTask);

        //getTimeoutTasks().put(batch.getID(), timeoutTask);
        
        scheduler.schedule(

           timeoutTask, getTimestamp() + getBatchingTimeout()

        );
        
    }


    protected void emitPrePrepare(PBFTBatchMessage batch){
        /**
         * If a batch has client requests then it is able to be pre-prepered.
         */
        if(batch.hasRequests()){

            /**
             * use the batch to createRequestMessage a pre-prepare message and multicast
             * the created pre-prepare message.
             */
            multicast(

                createPrePrepareMessage(batch)

            );

        }
            
    }
    

    /**
     * Create a pre-prepare message fro a client request.
     * @param request -- the client request.
     * @return -- a pre-prepare message.
     */
    protected PBFTPrePrepare createPrePrepareMessage(PBFTBatchMessage request){
        
        /*
         PBFTPrePrepare pp = new PBFTPrePrepare();
        
  //      pp.put(PBFTMessage.REQUESTFIELD, request);
        pp.setViewNumber(getCurrentView());
        pp.setSequenceNumber( newSequenceNumber());
//        pp.put(PBFTMessage.SOURCEFIELD, getLocalProcess());
//        pp.put(PBFTMessage.DESTINATIONFIELD, getLocalGroup());
//        pp.setReplicaID(getLocalProcess().getID());

    //    request.put(PBFTMessage.SEQUENCENUMBERFIELD, pp.getSequenceNumber());

        IAuthenticator authenticator = getServerToServerAuthenticator();
            
        pp = (PBFTPrePrepare) authenticator.makeDisgest(pp);

        return pp;
         *
         */
        return null;
    }

    /**
     * Collect a preprepare message sent by a primary replica. it's buffer the
     * message and update the protocol state
     * @param m -- the preprepare message.
     */
    protected boolean acceptPrePrepare(PBFTPrePrepare preprepare){


        /**
         * If the preprepare hasn't a valid sequence or view number then force a 
         * change view.
         */
//        if(!(hasAValidSequenceNumber(preprepare) && hasAValidViewNumber(preprepare))){
//
//            emitChangeView();
//            return false;
//
//        }
//
//        /**
//         * If the preprepare message wasn't sent by the primary replica then
//         * it will be discarded.
//         */
//        if(!wasSentByPrimary(preprepare)){
//            return false;
//        }
//
//
//        /**
//         * If was possible update the Log state then cancel all request assign
//         * to this preprepare. The Log state won't be update when:
//         *  a) the preprepare is null;
//         *  b) is a malformed preprepare;
//         *  c) there is another with the same sequence number and
//         * in the same view;
//         *  d) the requests related to this preprepare doesn't not received.
//         */
//
//        if(updateState(preprepare)){
//
//            /**
//             * When a pre-prepare message is received and authenticated, all timers
//             * related to primary server response has to be cancelled.
//             */
//            PBFTBatchMessage batch =
//                    (PBFTBatchMessage) preprepare.get(PBFTMessage.REQUESTFIELD);
//
//            for(PBFTRequest r : batch.getRequests()){
//
//                cancelTimers(r.getID());
//
//            }
//
//            /**
//             * Buffer the preprepare.
//             */
//
//            return addToBuffer(
//                preprepare, getBuffer(OUTBUFFER)
//             );
//
//        }
//
        return false;
        
    }

    /**
     * Update the state of the PBFT2. Insert the pre-prepare message in
     * the log entry.
     * @param m
     */
    public boolean updateState(PBFTPrePrepare preprepare){

        /**
         * If the preprepare is null then do nothing.
         */
        if(preprepare == null){

            return false;

        }

        /*
         * Get composite key of the prepare.
         */

        String entryKey =null; // preprepare.getRound();

        /**
         * If the entry key is diferent of null then update state. Otherwise do
         * nothing.
         */
        if(entryKey != null) {

            /**
             * Get the batch in the preprepare.
             */

            PBFTBatchMessage batch = null;
                    //(PBFTBatchMessage) preprepare.get(PBFTMessage.REQUESTFIELD);

            /**
             * For each request in batch, check if such request was received.
             */
            for(PBFTRequest r : batch.getRequests()){

                /**
                 * If some request in the bacth wasn't received then the log
                 * state won't be able to be updated.
//                 */
//                if(getState().isWaiting(r)){
//
//                   return false;
//
//                }
                
            }

            /**
             * get a log  entry for current preprepare.
             */
            PBFTLogEntry entry = getState().get(entryKey);

            /*
             * if there isn't a entry then create one.
             */
            if(entry == null){
                
                entry = new PBFTLogEntry(preprepare);
                
                /**
                 * Update the entry in log.
                 */
//                getState().put(entryKey, entry);

                return true;
            }

        }

        return false;

    }

    /**
     * Update the state of the PBFT2. Insert or update a quorum of message in
     * the log entry. The quorum can be a prepare or commit quorum of messages.
     * @param q -- the quorum of message.
     */
    public void updateState(Quorum q){

        /**
         * If the quorum is null then return.
         */
        if(q == null){
            
            return;

        }

        /**
         * If there isn't messages in the quorum then return.
         */
        if(q.size() <= 0){
            
            return;

        }

        AbstractPBFTServerMessage m1 = (AbstractPBFTServerMessage) q.get(0);
        
        String entryKey = m1.getRound();

        
        PBFTLogEntry entry = getState().get(entryKey);

        /**
         * If there isn't a entry for this quorum then return.
         */
        if(entry == null){
            
            return;

        }

        entry.setQuorum(q);
        
        for(IMessage m : q){

//            getState().changeRequestStatus(m);
            
        }

//        getState().put(entryKey, entry);
        
    }


    /**
     * Check if a request batch is complete. A batch is complete when its number
     * of requests is equals to the maximum specified for the protocol.
     * @param batch -- the batch that must be checked.
     * @return -- true if the batch is complete.
     */
    public boolean isACompletedBatch(PBFTBatchMessage batch) {
        return batch.complete(getMaxBatchSize());
    }

    /**************************************************************************
     * PBFT2' IServer  actions in Prepare phase.
     * 1. Create and multicasts a prepare message for each collected pre-prepare
     * message sent by the primary replica. [emitPrepare():void]
     * 2. Collect the prepare messagens sent by the active group members.
     *      -> collected prepare messagens are buffered and the quorums are com-
     *         puted.
     **************************************************************************/

    /**
     * create and multicast a prepare message to other members of the group.
     * @param pp -- the preprepare message.
     */
    protected void emitPrepare(PBFTPrePrepare pp){

        multicast(

           createPrepareMessage(pp)

        );

        
    }
    
    /**
     * Create a prepare message from a pre-prepare message received.
     * @param pp -- the pre-prepare message.
     * @return -- the new prepare message.
     */
    protected PBFTPrepare createPrepareMessage(PBFTPrePrepare pp){

        PBFTPrepare p = new PBFTPrepare(null, null, null, null);

        p.setViewNumber(pp.getViewNumber());
        p.setSequenceNumber(pp.getSequenceNumber());
//        p.put(PBFTMessage.DIGESTFIELD, pp.get(PBFTMessage.DIGESTFIELD));
        p.setReplicaID(getLocalProcess().getID());
//        p.put(PBFTMessage.REQUESTFIELD, pp.get(PBFTMessage.REQUESTFIELD));
//        p.put(PBFTMessage.DESTINATIONFIELD, getLocalGroup());

        IAuthenticator authenticator = getServerToServerAuthenticator();
        
        p = (PBFTPrepare) authenticator.makeDisgest(p);

        return p;

    }

    /**
     * Collect the prepare message sent by a replica. It's also responsable by
     * building the prepare quorum.
     * @param prepare -- the prepare message.
     */
    protected boolean acceptPrepare(PBFTPrepare prepare){


//        /**
//         * If the m hasn't a valid sequence or view number then force a
//         * change view.
//         */
//        if(hasAValidSequenceNumber(prepare) && hasAValidViewNumber(prepare)){
//
//            /**
//             * if there was a related preprepare message then insert the prepare
//             * in the prepare quorum.
//             */
//
//            if(wasPrePrepared(prepare)){
//
//                /**
//                 * If the related quorum wasn't complete then add the current
//                 * message to the related quorum.
//                 */
//                Quorum q = getQuorum(PREPAREQUORUMSTORE, prepare);
//
//                if(!(q != null && q.complete())){
//
//                    int f = getServiceBFTResilience();
//
//                    q = addToQuorum(prepare, PREPAREQUORUMSTORE, 2 * f + 1);
//
//                    /**
//                     * If the current quorum was complete then update the PBFT2'
//                     * Log state and add a prepare message to output buffer.
//                     */
//                    if(q.complete()){
//
//                        updateState(q);
//
//                        return addToBuffer(prepare, getBuffer(OUTBUFFER));
//
//                    }
//                }
//
//                return true;
//            }
//
//        }
        
        return false;
        
    }

    /**
     * Check if a prepare message has a respective pre-prepare.
     * @param p --the prepare message.
     * @return true if there is a prepare message for such prepare.
     */
    protected boolean wasPrePrepared(PBFTPrepare p){

        String entryKey = null; //p.getRound();

        /**
         * If has a valid key for an entry in the PBFT2' Log then retrieve the
         * respective Log entry.
         */
        if(entryKey != null){
        
            PBFTLogEntry entry = (PBFTLogEntry)getState().get(entryKey);

            /**
             * If there is a Log entry then there was preprepared if there is a
             * preprepare in the Log entry.
             */
            if(entry != null){

                return (entry.getPrePrepare() != null);
                
            }
        }

        return false;
        
    }
    
    /**************************************************************************
     * PBFT2' IServer  actions in Commit phase.
     * 
     * 1. Create and multicasts a commit message for each collected prepare
     * message sent by a replica. [emitCommit():void]
     * 
     * 2. Collect the commit messagens sent by the active group members.
     *      -> collected commit messagens are buffered and the quorums are com-
     *         puted.
     * 
     **************************************************************************/

    /**
     * Create and multicast commit messages.
     */
    protected void emitCommit(PBFTPrepare p){

        multicast(
            createCommitMessage(p)
        );

    }

    /**
     * Create a commit message from a prepare message.
     * @param p -- the prepare message.
     * @return - the created commit message.
     */
    protected PBFTCommit createCommitMessage(PBFTPrepare p){
        
        PBFTCommit c = null;//new PBFTCommit();
        
        c.setViewNumber(p.getViewNumber());
        c.setSequenceNumber(p.getSequenceNumber());
//        c.put(PBFTMessage.DIGESTFIELD, p.get(PBFTMessage.DIGESTFIELD));
        c.setReplicaID(getLocalProcess().getID());
//        c.put(PBFTMessage.REQUESTFIELD, p.get(PBFTMessage.REQUESTFIELD));
        //c.put(PBFTMessage.DESTINATIONFIELD, getLocalGroup());

        IAuthenticator authenticator = getServerToServerAuthenticator();

        c = (PBFTCommit) authenticator.makeDisgest(c);
        
        return c;
        
    }

    /**
     * Collect a commit message sent by a active replica. It's also responsable 
     * by building the commit quorum.
     * @param m -- the commit message.
     */
    protected boolean acceptCommit(PBFTCommit m){

        /**
         * If the m hasn't a valid sequence or view number then force a
         * change view.
         */
//        if(hasAValidSequenceNumber(m) && hasAValidViewNumber(m)){
//
//            /**
//             * if there was a related prepare message then insert the commit
//             * in the prepare quorum.
//             */
//
//            if(wasPrepared(m)){
//
//                /**
//                 * If the related quorum wasn't complete then add the current
//                 * message to the related quorum.
//                 */
//                Quorum q = getQuorum(COMMITQUORUMSTORE, m);
//
//                if(!(q != null && q.complete())){
//
//                    int f = getServiceBFTResilience();
//
//                    q = addToQuorum(m, COMMITQUORUMSTORE, 2 * f + 1);
//
//                    /**
//                     * If the current quorum was complete then update the PBFT2'
//                     * Log state and add a prepare message to output buffer.
//                     */
//                    if(q.complete()){
//
//                        updateState(q);
//
//                        return addToBuffer(m, getBuffer(OUTBUFFER));
//
//                    }
//                }
//            }
//        }

        return false;
        
    }


    /**
     * Check if a commit message has a respective prepare.
     * @param c --the prepare message.
     * @return true if there is a prepare message for such prepare.
     */
    protected boolean wasPrepared(PBFTCommit c){

        String entryKey = null; //c.getRound();

        /**
         * If has a valid key for an entry in the PBFT2' Log then retrieve the
         * respective Log entry.
         */
        if(entryKey != null){

            PBFTLogEntry entry = (PBFTLogEntry)getState().get(entryKey);

            /**
             * If there is a Log entry then there was preprepared if there is a
             * preprepare in the Log entry.
             */
            if(entry != null){
                
                Quorum q = entry.getPrepareQuorum();

                return (q != null && !q.isEmpty());

            }
        }

        return false;

    }


    /**************************************************************************
     * PBFT2' IServer  actions in Execute and Reply phase.
     * 1. Create and multicasts a commit message for each collected prepare
     * message sent by a replica. [emitCommit():void]
     * 2. Collect the commit messagens sent by the active group members.
     *      -> collected commit messagens are buffered and the quorums are com-
     *         puted.
     **************************************************************************/
    /**
     * Execute the committed requests.
     */
    protected boolean emitExecute(PBFTCommit commit){

        try{

            /**
             * Execute each request in request list.
             */
            
            PBFTRequestMessageList requests = null;//getState().getRequests(commit);

//            Collections.sort(requests);
            
            for(PBFTRequest request : requests){

                PBFTReply reply = emitExecute(request);

                if(reply != null){

//                    getState().changeRequestStatus(reply, request);
                    
                }
                
            }


            /**
             * Compact the current state. I'm not sure if this is the best place
             * to do it (maybe we can do it during the checkpoint procedure).
             * Alirio Sá (2011-02-10).
             */
//            getState().compact();;

            /**
             * incrementa o contador de execuções.
             */
            incExecutionCount();
            verifyCheckpoint();
            
            return true;

        }catch(Exception e){

            return false;
            
        }
        
    }

    /**
     * Execute a client request.
     * @param request -- the client request.
     */
    protected PBFTReply emitExecute(PBFTRequest request){

        /**
         * If the request hasn't been already served then it can be executed.
         */
        if(!getState().wasServed(request)){

            /**
             * Execute the request and get the response.
             */
            IPayload response = null;////emitExecute((IPayload)request.getContent());
            
            /**
             * Create a reply message from current request and response.
             */
            PBFTReply reply = createReplyMessage(response, request);

            //addToBuffer(reply, getBuffer(OUTBUFFER));

            return reply;
            
        }

        return null;

    }
    /**
     * Execute a operation from in the server.
     * @param operation -- the operation.
     * @return -- the result.
     */
    protected IPayload emitExecute(IPayload operation){

        IServer server = (IServer) getLocalProcess();

        return server.executeCommand(operation);


    }


    /**
     * Create a response from a response and a request.
     * @param result -- the response.
     * @param request -- the request.
     * @return -- a new reply message.
     */
    protected PBFTReply createReplyMessage(IPayload result, PBFTRequest request){
     
        PBFTReply reply = null; //new PBFTReply();

//        IProcess client = (IProcess) request.get(PBFTMessage.SOURCEFIELD);

  //      reply.put(PBFTMessage.PAYLOADFIELD, result);
  //      reply.put(PBFTMessage.SOURCEFIELD, getLocalProcess());
  //      reply.put(PBFTMessage.DESTINATIONFIELD, client);
  //      reply.put(PBFTMessage.CLIENTIDFIELD, client);
  //      reply.put(PBFTMessage.TIMESTAMPFIELD, request.get(PBFTMessage.TIMESTAMPFIELD));
  //      reply.put(PBFTMessage.VIEWFIELD, getCurrentView());
  //      reply.put(PBFTMessage.REPLICAIDFIELD, getLocalProcess().getID());

        IAuthenticator authenticator = getServerToClientAuthenticator();

        //reply = (PBFTReply) authenticator.encrypt(reply);

        return reply;


    }
    

    /**
     * Perform a reply for a specific client request.
     * @param reply
     */
    protected void emitReply(PBFTReply reply){
        unicast(reply);

    }

    /**************************************************************************
     * PBFT2' Server  actions in view change procedure.
     * 1. Create and multicasts a commit message for each collected prepare
     * message sent by a replica. [emitCommit():void]
     *
     * 2. Collect the commit messagens sent by the active group members.
     *      -> collected commit messagens are buffered and the quorums are com-
     *         puted.
     **************************************************************************/

    /**
     * Start a view change.
     */
    protected void emitChangeView() {

        /**
         * Execute only if another change view change has not been recently
         * executed.
         */
        if(!anotherChangeViewHasBeenRecentlyExecuted(getTimestamp())){

            PBFTNewView newView = createNewViewMessage(getState());
            
        }
        
    }

    protected PBFTNewView createNewViewMessage(PBFTStateLog currentState){

        PBFTNewView nv = new PBFTNewView();
        
        nv.setViewNumber(getCurrentView() + 1);

//        nv.put(PBFTMessage.CHECKPOINTLOWWATERMARK, null);
 //       nv.put(PBFTMessage.REPLICASTATEFIELD, currentState);

        return nv;
    }

    /**************************************************************************
     * PBFT2' performance related methods.
     **************************************************************************/

    protected void emitCheckpoint(){

        emitCheckpoint(createCheckpointMessage());
        
    }

    protected void emitCheckpoint(PBFTCheckpoint cp){

        multicast(cp);
        
    }

    /**
     * Create a checkpoint message. It uses the last executed sequence number,
     * the digest of the current state and the replica ID.
     * @return a checkpoint message.
     */
    protected PBFTCheckpoint createCheckpointMessage(){
        PBFTCheckpoint checkpoint = new PBFTCheckpoint();

//        checkpoint.setSequenceNumber(getState().getLastExecutedSequenceNumber());

        //checkpoint.setDigest(getServerToServerAuthenticator().makeDisgest(getState()));

        checkpoint.setReplicaID(getLocalProcess().getID());


//        checkpoint.put(
//            PBFTMessage.SOURCEFIELD,
//            getLocalProcess()
//        );

//        checkpoint.put(
//            PBFTMessage.DESTINATIONFIELD,
 //           getLocalGroup()
 //       );

        return checkpoint;
    }

    /**
     * Collect a checkpoint message sent by a active replica. It's also responsable
     * by building the checkpoint quorum.
     * @param m -- the checkpoint message.
     */
    protected boolean acceptCheckpoint(PBFTCheckpoint m){

        /**
         * If the m hasn't a valid sequence.
         */
//        if(inAValidSequenceRange(m)){
//
//            /**
//             * If the related quorum wasn't complete then add the current
//             * message to the related quorum.
//             */
//            Quorum q = getQuorum(CHECKPOINTQUORUMSTORE, m);
//
//            if(!(q != null && q.complete())){
//
//                int f = getServiceBFTResilience();
//
//                q = addToQuorum(m, CHECKPOINTQUORUMSTORE, 2 * f + 1);
//
//                /**
//                 * If the current quorum was complete then update the PBFT2'
//                 * Log state and add a prepare message to output buffer.
//                 */
//                if(q.complete()){
//
//                    getState().gc(m);
//
//                }
//            }
//        }

        return false;

    }

    /**************************************************************************
     * PBFT2' utility methods.
     **************************************************************************/

    /**
     * Collect a message and update its respective quorum. A new quorum is
     * created if it doesn't exist.
     * @param msg -- the message.
     * @param qname -- the quorum name.
     * @param qsize -- the quorum size.
     */
    private Quorum addToQuorum(AbstractPBFTServerMessage msg, String qname, int qsize){
        
        QuorumTable qtable = getQuorumTable(qname);

        String qkey = msg.getRound();

        Quorum quorum = (Quorum) qtable.get(qkey);

        quorum = addToQuorum(msg, quorum, qsize);

        qtable.put(qkey, quorum);

        return quorum;

    }

    /**
     * Get a quorum from a the internal quorum table using the message composedkey.
     * @param qname -- quorum name;
     * @param msg -- msg
     * @return the quorum
     */
    public Quorum getQuorum(String qname, AbstractPBFTServerMessage msg){

        return getState().getQuorum(qname, msg.getRound());
        
    }

    /**
     * Get a quorum from a internal quorum table using a quorum name and
     * quorum key.
     * @param qname
     * @param qkey
     * @return
     */
    public Quorum getQuorum(String qname, String qkey){
        
        QuorumTable qtable = getQuorumTable(qname);       

        Quorum quorum = (Quorum) qtable.get(qkey);

        return quorum;
        
    }

    /**
     * Get a quorum store by name. A Quorum store relats a key to a quorum.
     * @param name -- the quorum store name.
     * @return -- a quorum store.
     */
    public QuorumTable getQuorumTable(String name){
        return getState().getQuorumTable(name);
        //return (QuorumTable)getContext().get(name);
    }

    /**
     * Add a message to a quorum if such message has not already added.
     * @param m -- the message.
     * @param quorum -- the quorum.
     * @param qsize -- the quorum size.
     * @return -- return the updated quorum.
     */
    protected Quorum addToQuorum(PBFTMessage m, Quorum quorum, int qsize){

        if(quorum == null){

            quorum = new Quorum(qsize);

        }

        if(!inQuorum(m, quorum)){

            quorum.add(m);

        }

        return quorum;

    }

    /**
     * Check if a message already exists in a quorum.
     * @param m -- the pbft message.
     * @param quorum -- the quorum;
     * @return -- true if the message belongs to the quorum.
     */
    protected boolean inQuorum(PBFTMessage m, Quorum quorum){

        if(m == null){

            return false;
        }

        return quorum.contains(m);

    }

    /**
     * Add a message to the specified buffer.
     * @param data -- the message.
     * @param buffer -- the buffer.
     * @retur true if the message was added to the buffer.
     */
    protected boolean addToBuffer(IData data, Collection buffer){

        if(data == null){

            return false;

        }//endif request is null

        if(!buffer.contains(data)){

            buffer.add(data);

            return true;
            
        }
       
        return false;

    }

    protected static String INBUFFER  = "__PBFT_INBUFFER";
    protected static String OUTBUFFER = "__PBFT_OUTBUFFER";

    /**
     * Get a internal buffer specified by the buffer name. Usually the buffer
     * name is the class name of the object that is stored in each specific
     * buffer.
     * @param buffername -- the name of the internal buffer.
     * @return
     */
    protected Collection getBuffer(String buffername){
        return (Collection) getContext().get(buffername);
    }

    /**
     * Check if a message belongs to a buffer.
     * @param m -- the message.
     * @param buffer -- the buffer.
     * @return -- true if the message belongs to the buffer.
     */
    protected boolean buffered(PBFTMessage m, Collection buffer){

//        for(Object item : buffer){
//
//            PBFTMessage m1 = (PBFTMessage) item;
//
//            if(m1.getID().equals(m.getID())){
//
//                return true;
//
//            }//endif m1 equals to request
//
//        }//endfor each item in request buffer

        return false;
    }

    /**
     * Check if a message insert a hole in the sequence numbers.
     * @param m -- the message.
     * @return -- true if the message inserts a hole in the sequence numbers.
     */
    protected boolean hasAHole(PBFTMessage m){
        return false;
//        return getState().checkHole(m);

    }

    /**
     * Checks if a message has a valid sequence number, this is: the sequence
     * number doesn't has holes and is in a valid range.
     * @param m -- the message.
     * @return -- true if the message has a valid sequence number.
     */
    protected boolean hasAValidSequenceNumber(AbstractPBFTServerMessage m) {

        return !hasAHole(m) && inAValidSequenceRange(m);
        
    }

    /**
     * Check if the view number of the message is equal to the current view
     * number.
     * @param m -- the message.
     * @return -- true if the message belongs to the current view.
     */
    protected boolean hasAValidViewNumber(AbstractPBFTServerMessage m) {

        Object view = m.getViewNumber();

        return getCurrentView().equals(view);
        
    }


    /**
     * Check if a message has a sequence number between the low and high water
     * marks defined by the checkpoint.
     * @param m -- the message.
     * @return -- true if the sequence number of the message is in the valid
     * range.
     */
    protected boolean inAValidSequenceRange(AbstractPBFTServerMessage m){

        Long seqn = (Long)m.getSequenceNumber();

        return (
           getCheckpointLowWaterMark().compareTo(seqn) >= 0 &&
           getCheckpointHighWaterMark().compareTo(seqn) <=0
        );
        
    }

    /**
     * Check the valid of the message (i.e. digest, sequence number and view
     * number).
     * @param m -- the message.
     * @return -- true if is a valid message.
     */
    protected boolean isAValidServerMessage(AbstractPBFTServerMessage m, IAuthenticator authenticator, boolean digest){
        
        /**
         * Check the authentication of the message.
         */
//        if(digest){
//            if(!authenticator.checkDisgest(m)){
//                return false;
//            }
//        }else{
//            if(!authenticator.check(m)){
//                return false;
//            }
//
//        }

        return hasAValidSequenceNumber(m) && hasAValidViewNumber(m);

    }


    /**
     * Multicast a message for a group of process.
     * @param m -- the message that has to be multicasted.
     */
    protected void multicast(PBFTMessage m){
        /*
            the remote procecess is a abstraction for the group address -- which
            can contain a single address or multiple address.
         */
        
//        IGroup g = (IGroup)m.get(PBFTMessage.DESTINATIONFIELD);

  //      getCommunicator().multicast(m, g);
    }    


    /**
     * Unicast a messge for a process.
     * @param m -- the message that has to be uniscat.
     */
    protected void unicast(PBFTMessage m){
        
//        IProcess destination = (IProcess) m.get(PBFTMessage.DESTINATIONFIELD);

  //      getCommunicator().unicast(m, destination);
        
    }

    /**
     * Check if another change view procedure has been recently executed.
     * @param timestamp -- the current timestamp.
     * @return -- true if another change view was recently executed.
     */
    protected boolean anotherChangeViewHasBeenRecentlyExecuted(Long timestamp) {
        return 2 * getState().getLastChangeViewTimestamp() >= timestamp;
    }

    /**
     * Get the List of the timers defined during the execution of the protocol
     * (e.g. client request retransmission, batch request timeout etc.).
     * @return
     */
    protected TaskTable getTimeoutTasks() {
        return timeoutTasks;
    }

    /**
     * Cancel a timer related to the specified task ID.
     * @param taskID -- the taskID.
     */
    protected void cancelTimers(String taskID) {

        PBFTTimeoutDetector timeoutTask =
                (PBFTTimeoutDetector) getTimeoutTasks().get(taskID);

        timeoutTask.cancel();

        getTimeoutTasks().remove(taskID);

    }

    /**
     * Allow the PBFT2 protocol to accept related data (e.g. PBFTMessage,
     * Application payload) from related enviroment (e.g. client, server and
     * network). This method forwards for the procedure responsable to perform
     * initial checks (i.e. security checks for all kind of data and specific
     * checks for specific data), store the data in its related buffer and
     * schedule the related timers.
     * 
     * @param data -- the data which has been collected.
     */
    public boolean accept(IData data){
        

        
        if(data instanceof IPayload && getLocalProcess() instanceof IClient){

            return acceptPayload((IPayload)data);
            
            
        }

        if(data instanceof PBFTPrePrepare && getLocalProcess() instanceof IServer){

            /**
             * If the prepreare wasn't authenticated then discard it.
             */
            if(!getServerToServerAuthenticator().checkDisgest((PBFTPrePrepare)data)){
                return false;
            }

            return acceptPrePrepare((PBFTPrePrepare)data);


        }

        if(data instanceof PBFTPrepare && getLocalProcess() instanceof IServer){

            /**
             * If the m wasn't authenticated then discard it.
             */
            if(!getServerToServerAuthenticator().checkDisgest((PBFTPrepare)data)){
                return false;
            }

            return acceptPrepare((PBFTPrepare)data);
            
        }

        if(data instanceof PBFTCommit && getLocalProcess() instanceof IServer){

            /**
             * If the m wasn't authenticated then discard it.
             */
            if(!getServerToServerAuthenticator().checkDisgest((PBFTCommit)data)){
                return false;
            }

            return acceptCommit((PBFTCommit)data);
            

        }

        if(data instanceof PBFTCheckpoint && getLocalProcess() instanceof IServer){

            /**
             * If the m wasn't authenticated then discard it.
             */
            if(!getServerToServerAuthenticator().checkDisgest((PBFTCheckpoint)data)){
                return false;
            }

            return acceptCheckpoint((PBFTCheckpoint)data);


        }

        if(data instanceof PBFTReply && getLocalProcess() instanceof IClient){

            return acceptReply((PBFTReply)data);
            
        }

        if(data instanceof PBFTRequest && getLocalProcess() instanceof IServer){

            return acceptRequest((PBFTRequest)data);


        }

        return false;
        
    }

    protected void emit(IData data){

        if(data instanceof PBFTBatchMessage && getLocalProcess() instanceof IServer){

            emitPrePrepare((PBFTBatchMessage)data);
            return;


        }
        
        if(data instanceof PBFTRequest && getLocalProcess() instanceof IClient){

            emitRequest((PBFTRequest)data);
            return;
        }

        if(data instanceof PBFTPrepare && getLocalProcess() instanceof IServer){

            emitCommit((PBFTPrepare)data);
            return;

        }

        if(data instanceof PBFTCommit && getLocalProcess() instanceof IServer){
            emitExecute((PBFTCommit)data);
            return;


        }
        
        if(data instanceof PBFTPrePrepare && getLocalProcess() instanceof IServer){

            emitPrepare((PBFTPrePrepare)data);
            return;

        }

        if(data instanceof PBFTCheckpoint && getLocalProcess() instanceof IServer){

            emitCheckpoint((PBFTCheckpoint)data);
            return;

        }

        if(data instanceof PBFTReply && getLocalProcess() instanceof IServer){

            emitReply((PBFTReply)data);
            return;

        }

        if(data instanceof IPayload && getLocalProcess() instanceof IClient){

            emitDeliver((IPayload)data);
            return;

        }
        
    }

    /**
     * Get the system clock value.
     * @return -- the current clock value.
     */
    public long getTimestamp(){
        return ((IClock)getContext().get(PBFT2.CLOCKSYSTEM)).value();
    }

    /**
     * Check if the local process is the primary replica.
     * @return true if the local process is the primary.
     */
    public boolean isPrimary(){
        /*CHANGE TO PROTECTED LATER.*/
        return isPrimary(getLocalProcess());
    }

    /**
     * Check if a process is the primary replica.
     * @param p -- the process.
     * @return true if the process is the primary.
     */
    protected boolean isPrimary(br.ufba.lasid.jds.IProcess p){
        return isPrimaryID(p.getID());
    }

    /**
     * Check if the process' ID belongs to the primary replica.
     * @param ID -- the id.
     * @return true if the process'ID belogns to the primary.
     */
    protected boolean isPrimaryID(Object ID){
        return (getContext().get(PBFT2.GROUPLEADER)).equals(ID);
    }

    /**
     * Check if a message was sent by the primary.
     * @param m -- the message.
     * @return true if was sent by the primary.
     */
    protected boolean wasSentByPrimary(AbstractPBFTServerMessage m){
        return isPrimaryID(m.getReplicaID());
    }

    /**
     * keep the count of the batch executions.
     */
    protected long executionCount = 0;

    /**
     * Get the count of batch executions.
     * @return the number of batch execution.
     */
    protected synchronized long getExecutionCount() {
        return executionCount;
    }

    /**
     * Redefine the number of batch executions.
     * @param executionCount
     */
    protected synchronized void setExecutionCount(long executionCount) {
        this.executionCount = executionCount;
    }

    /**
     * Increment the number of batch execution.
     * @return
     */
    protected synchronized long incExecutionCount() {
        this.executionCount ++;

        if(this.executionCount == Long.MIN_VALUE){
            this.executionCount = 0;
        }

        return this.executionCount;
    }

    /**
     * Check end emit a checkpoint notification when one checkpoint period has
     * been complete.
     */
    protected void verifyCheckpoint(){
        if(this.getExecutionCount() % getCheckPointPeriod() == 0){
            emitCheckpoint();
        }
    }

    protected IAuthenticator getAuthenticator(PBFTMessage m){

        IAuthenticator auth;

        if(m instanceof PBFTReply){

            auth = getServerToClientAuthenticator();

        }else{
            if(m instanceof PBFTRequest){

                auth = getClientToServerAuthenticator();

            }else{

                auth = getServerToServerAuthenticator();

            }
        }

        return auth;
    }

    protected PBFTMessage encrypt(){
        return null;
    }
    
    protected PBFTMessage makeDigest(PBFTMessage m){

///        m.put(PBFTMessage.DIGESTFIELD, getAuthenticator(m).getDigest(m));

        return m;
        
    }
    
    /**
     * IMPLEMENT A BETTER TIMESTAMP STRATEGY TO ADDRESS THE PROBLEM OF ASYNC
     * CLIENTS CONNECTED TO THE SERVERS BY UNRELIABLE CHANNELS.
     *
     * protected long timestampSEQ = 0;
     * protected PBFTRequestTable requestTable = new PBFTRequestTable();
     *
     * * Get the table of requests which are indexed by the timestamp.
     * * @return -- table of requests.
     *
     * protected PBFTRequestTable getRequestTable() {
     *
     *      return requestTable;
     *
     * }
     *
     * * Insert the request in the request table.
     * * @param request -- the request that is going to be added to
     * * the request table.
     *
     * private void addToRequestTable(PBFTRequest request){
     *
     *      Timestamp timestamp =
     *          (Timestamp) request.get(PBFTMessage.TIMESTAMPFIELD);
     * 
     *      getRequestTable().put(timestamp, request);
     *
     * }
     *
     * * Get the sequence number for timestamps of operations;
     * * @return
     * 
     * private long incTimestampSEQ() {
     *
     *      return timestampSEQ++;
     *
     * }
     *
     * * Create a new Timestamp.
     * * @return -- the timestamp.
     *
     * public Timestamp newTimestamp(){
     *
     *      return new Timestamp(getTimestamp(), incTimestampSEQ());
     * 
     * }
     *
     * protected String buffername(Class _type, String direction){
     *
     *      return buffername(_type.getName(), direction);
     *
     * }
     *
     * protected String buffername(String name, String direction){
     *
     *      return name + ":" + direction;
     *
     * }
     *
     * * Get a internal buffer specified by the class of the object which is
     * * stored in this buffer.
     * * @param _type
     * * @return
     *
     * protected Collection getBuffer(Class _type, String direction){
     *      return getBuffer(buffername(_type, direction));
     * }
     */

    private class AccepterTask extends Thread{
        
        @Override
        public void run() {

            /**
             * Get the request buffer.
             */
            IData data = null;

            BlockingBuffer buffer = (BlockingBuffer) getBuffer(INBUFFER);

            

            while(true){

                /**
                 * Get a data from the buffer.
                 */
                
                data = (IData) buffer.remove();
                                    
                /**
                 * perform
                 */
                accept(data);

            }

        }
        
    }//end thread ReceiverTask

    private class EmitterTask extends Thread{

        @Override
        public void run() {

            /**
             * Get the batch buffer.
             */
            BlockingBuffer buffer = (BlockingBuffer) getBuffer(OUTBUFFER);

            IData data = null;

            while(true){

                /**
                 * Get a batch from the buffer.
                 */

                data = (IData) buffer.remove();

                /**
                 * do send.
                 */
                emit(data);

            }

        }

    }//end thread SenderTask

}