/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.SingleProcess;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTTuple;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferCommittedRequestAction;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferPrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteStartNewRoundPhaseTwoAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBatchMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommitMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepareMessage;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveNewViewExecutor extends PBFTServerExecutor{

    public PBFTReceiveNewViewExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    /**
     * [TODO]
     * @param act
     */
    @Override
    public synchronized void execute(Action act) {

        ((PBFT)getProtocol()).getChangeViewRetransmittionScheduler().cancelAll();
        
        PBFTMessage m = (PBFTMessage) act.getWrapper();

        System.out.println(
            "server [p" + getProtocol().getLocalProcess().getID()+"] "
          + "has received a new view message at "
          + ((PBFT)getProtocol()).getTimestamp()
        );


        Buffer C = (Buffer) m.get(PBFTMessage.SETCHECKPOINTEDINFORMATIONFIELD);
        Buffer P = (Buffer) m.get(PBFTMessage.SETPREPAREINFORMATIONFIELD);
        Buffer Q = (Buffer) m.get(PBFTMessage.SETPREPREPAREINFORMATIONFIELD);

        int newView = (Integer) m.get(PBFTMessage.VIEWFIELD);

        long checkpointLowWaterMark = (Long) m.get(PBFTMessage.CHECKPOINTLOWWATERMARK);
        
        int f = ((PBFT)getProtocol()).getServiceBFTResilience();

        PBFTTuple cquorum = computeQuorum(new PBFTTuple(), C);
        PBFTTuple pquorum = computeQuorum(new PBFTTuple(), P);
        PBFTTuple qquorum = computeQuorum(new PBFTTuple(), Q);

        int currentSEQ = -1;

        /* create null prepare for messages in P quorum */
        for(Object item : C){

            String ID = (String) item;
            String mID = getItemID(ID);

            int quorum = (Integer) cquorum.get(mID);

            if(quorum >= f + 1){

                int seqn = getSequence(ID);

                if(seqn > checkpointLowWaterMark){

                    Buffer checkpoints = new Buffer();

                    checkpoints.addAll(((PBFT)getProtocol()).getCommittedBuffer());

                    boolean hasCheckpoint = false;

                    for(Object cm : checkpoints){

                        PBFTMessage checkpoint = (PBFTMessage) cm;

                        int seqn1 = (Integer) checkpoint.get(PBFTMessage.SEQUENCENUMBERFIELD);
                        int view  = (Integer) checkpoint.get(PBFTMessage.VIEWFIELD);

                        if(seqn1 == seqn && view < newView){

                            PBFTMessage checkpoint1 = new PBFTCommitMessage();

                            checkpoint1.putAll(checkpoint);
                            checkpoint1.put(PBFTMessage.VIEWFIELD, newView);

                            getProtocol().perform(new BufferCommittedRequestAction(checkpoint1));

                            hasCheckpoint = true;

                            break;
                        }
                    }

                    if(!hasCheckpoint){

                        /**
                         * create a list for missed checkpoints and fetch from
                         * other replicas
                         */

                    }
                }

            }

        }

        /* update checkpoints */
        SingleProcess client = new SingleProcess();
        client.setID(-1);

        for(Object item : P){

            String ID = (String) item;
            String mID = getItemID(ID);

            int quorum = (Integer) cquorum.get(mID);

            if(quorum >= 2 * f + 1){

                int seqn = getSequence(ID);

                if(seqn > checkpointLowWaterMark){

                    if(currentSEQ < seqn){
                        currentSEQ = seqn;
                    }

                    PBFTMessage batch = new PBFTBatchMessage();

                    batch.put(PBFTMessage.BATCHSIZEFIELD, 0);
                    batch.put(PBFTMessage.SEQUENCENUMBERFIELD, seqn);
                    batch.put(PBFTMessage.VIEWFIELD, newView);
                    batch.put(PBFTMessage.CLIENTFIELD, client.getID());
                    batch.put(PBFTMessage.TIMESTAMPFIELD, ((PBFT)getProtocol()).getTimestamp());

                    PBFTMessage pp = new PBFTPrePrepareMessage();

                    pp.put(PBFTMessage.TYPEFIELD, PBFTMessage.TYPE.RECEIVEPREPREPARE);
                    pp.put(PBFTMessage.REQUESTFIELD, batch);
                    pp.put(PBFTMessage.VIEWFIELD, newView);
                    pp.put(PBFTMessage.SEQUENCENUMBERFIELD, seqn);
                    pp.put(PBFTMessage.SOURCEFIELD, getProtocol().getLocalProcess());
                    pp.put(PBFTMessage.REPLICAIDFIELD, getProtocol().getLocalProcess().getID());
                    pp = (PBFTMessage)encrypt(pp);
                    pp = (PBFTMessage)makeDisgest(pp);

                    /* check if preprepare exists in the buffer */

                    if(!PBFT.isABufferedMessage(((PBFT)getProtocol()).getPrePrepareBuffer(), pp)){
                        ((PBFT)getProtocol()).getPrePrepareBuffer().add(pp);
                    }

                }

            }

        }

        long seqn = PBFT.getCurrentSequenceNumber();

        if(currentSEQ < seqn){
            PBFT.updateCurrentSequenceNumber(currentSEQ);
        }

        ((PBFT)getProtocol()).setCurrentView(newView);
        ((PBFT)getProtocol()).setGroupLeader(m.get(PBFTMessage.REPLICAIDFIELD));
        ((PBFT)getProtocol()).unlock();

        Buffer replys  = ((PBFT)getProtocol()).getReplyBuffer();
        Buffer requests = ((PBFT)getProtocol()).getRequestBuffer();

        if(((PBFT)getProtocol()).isPrimary()){
            for(Object item : requests){
                PBFTMessage request = (PBFTMessage) item;
                if(!PBFT.isABufferedMessage(replys, request)){
                    getProtocol().perform(new ExecuteStartNewRoundPhaseTwoAction(request));
                }
            }
        }
        
    }


    public PBFTTuple computeQuorum(PBFTTuple quorum, Buffer buffer){
        
        for(Object item : buffer){
            
            String ID = (String) item;

            String mID = getItemID(ID);
            
            if(!quorum.containsKey(mID)){
                quorum.put(mID, 0);
            }

            int count = (Integer)quorum.get(mID);
            
            quorum.put(mID, count + 1);

            
        }
        
        return quorum;
    }

    private String getItemID(String item){
        return (item.split(":", 2))[1];
    }

    private int getSequence(String ID){
        return Integer.parseInt((ID.split(":"))[1]);
    }
    private String getServerID(String item){
        return (item.split(":", 2))[0];
    }
}