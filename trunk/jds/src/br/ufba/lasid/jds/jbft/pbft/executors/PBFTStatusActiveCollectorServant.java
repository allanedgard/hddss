/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTServerMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTPrepareCollectorServant;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTLogEntry;
import br.ufba.lasid.jds.util.Debugger;

/**
 *
 * @author aliriosa
 */
public class PBFTStatusActiveCollectorServant extends PBFTCollectorServant<PBFTStatusActive>{


    public PBFTStatusActiveCollectorServant(){

    }

    public PBFTStatusActiveCollectorServant(PBFT p){
        setProtocol(p);
    }

    protected volatile Object activeReplicaID;

    /**
     * Collect the request sent by the client.
     * @param statusActive -- the client request.
     */
    protected synchronized boolean accept(PBFTStatusActive statusActive){

        PBFTServer pbft = (PBFTServer)getProtocol();

        activeReplicaID = statusActive.getReplicaID();

        if(statusActive != null && !pbft.getLocalServerID().equals(activeReplicaID)){
        
            /**
             * If the preprepare hasn't a valid sequence or view number then force a
             * change view.
             */
            if(!pbft.hasAValidViewNumber(statusActive)){
                Debugger.debug(
                  "[PBFTStatusActiveCollectorServant] s"  + pbft.getLocalServerID() +
                  ", at time " + pbft.getClock().value() + ", discarded " + statusActive +
                  " because it hasn't a valid view number. "
                  + "(currView = " + pbft.getCurrentViewNumber() + ")"
                  + "[nextPP = " + pbft.getNextPrePrepareSEQ() + ", nextP = "
                  + pbft.getNextPrepareSEQ() + ", nextC =" + pbft.getNextCommitSEQ() 
                  + " , nextE = " + pbft.getNextExecuteSEQ() + "]"
                );

                return false;

            }

            /**
             * If the preprepare message wasn't sent by a group member then it will
             * be discarded.
             */
            if(!pbft.wasSentByAGroupMember(statusActive)){
                Debugger.debug(
                  "[PBFTPrepareCollectorServant] s"   + pbft.getLocalServerID()   +
                  ", at time " + pbft.getClock().value() + ", discarded " + statusActive      +
                  " because it wasn't sent by a member of the group " + pbft.getLocalGroup()
                );

                return false;
            }

            /**
             * Send the executed request.
             */
            if(pbft.getNextPrepareSEQ() >= 0){
                Long maxSEQ = pbft.getNextPrePrepareSEQ();
                Long minSEQ = pbft.getNextExecuteSEQ()-1;

                long eSEQ  = statusActive.getLastExecutedSEQ();
                long cSEQ  = statusActive.getLastCommittedSEQ();
                long pSEQ  = statusActive.getLastPreparedSEQ();
                long ppSEQ = statusActive.getLastPrePreparedSEQ();
                
                if(minSEQ > eSEQ ) minSEQ = eSEQ;
                if(minSEQ > cSEQ ) minSEQ = cSEQ;
                if(minSEQ > pSEQ ) minSEQ = pSEQ;
                if(minSEQ > ppSEQ) minSEQ = ppSEQ;
                if(minSEQ < 0L)    minSEQ = 0L;

                if(maxSEQ < eSEQ )
                    maxSEQ = eSEQ;
                if(maxSEQ < cSEQ )
                    maxSEQ = cSEQ;
                if(maxSEQ < pSEQ )
                    maxSEQ = pSEQ;
                if(maxSEQ < ppSEQ)
                    maxSEQ = ppSEQ;

                if(maxSEQ < 0L)
                    maxSEQ = 0L;

                long _eSEQ  = pbft.getNextExecuteSEQ() - 1L;
                long _cSEQ  = pbft.getNextCommitSEQ()  - 1L;
                long _pSEQ  = pbft.getNextPrepareSEQ() - 1L;
                long _ppSEQ = pbft.getNextPrePrepareSEQ() - 1L;
                
                
                boolean sent = false;
                
                for(long i = minSEQ; i < maxSEQ; i++){
                    
                    PBFTLogEntry entry = pbft.getStateLog().get(i);

                    if(entry != null){
                        Quorum pq = entry.getPrepareQuorum();
                        Quorum cq = entry.getCommitQuorum();

                        
                        if(cSEQ < i && pSEQ < i && ppSEQ <= i && pbft.isPrimary() && entry.getPrePrepare() != null && _ppSEQ >= i){
                            PBFTPrePrepare pp = entry.getPrePrepare();
                            emit(pp);
                            sent = sent || true;
                        }

                        if(cSEQ < i && pSEQ <= i && pq != null/*&& _pSEQ >= i*/){
                            Quorum q = new Quorum();
                            q.addAll(pq);
                            if(!q.isEmpty()){
                                
                                for(IMessage m : q){

                                    PBFTPrepare p = (PBFTPrepare)m;

                                    if(p.getReplicaID().equals(pbft.getLocalServerID())){
                                        emit(p);
                                        sent = sent || true;
                                    }
                                }
                            }
                        }

                        if(cSEQ <= i && cq != null/*&& _cSEQ >=i*/){

                            Quorum q = new Quorum();
                            q.addAll(cq);

                            if(!q.isEmpty()){
                                for(IMessage m : q){

                                    PBFTCommit c = (PBFTCommit)m;

                                    if(c.getReplicaID().equals(pbft.getLocalServerID())){
                                        emit(c);
                                        sent = sent || true;
                                    }
                                }
                            }
                        }
                    }

                    //if(sent) break;
                }

            }

        }

        return true;

    }


    public synchronized void emit(PBFTServerMessage message){
        PBFTServer pbft = (PBFTServer)getProtocol();

        SignedMessage m;

        try {

            m = pbft.getAuthenticator().encrypt(message);

            IProcess d = new br.ufba.lasid.jds.Process(activeReplicaID);
            IProcess s = pbft.getLocalProcess();

            PDU pdu = new PDU();
            pdu.setSource(s);
            pdu.setDestination(d);
            pdu.setPayload(m);

            pbft.getCommunicator().unicast(pdu, d);

            Debugger.debug(
              "[PBFTStatusActiveCollectorServant]s" +  pbft.getLocalProcess().getID() +
              " sent " + message + " at timestamp " + pbft.getClock().value() +
              " to  s" + activeReplicaID + "."
            );


        } catch (Exception ex) {
            Logger.getLogger(PBFTPrepareCollectorServant.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }


    public synchronized boolean canConsume(Object object) {

        if(object instanceof PBFTStatusActive)
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
