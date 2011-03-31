/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.decision.Quorum;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import java.io.Serializable;

/**
 *
 * @author aliriosa
 */
public class PBFTLogEntry implements Serializable{

     PBFTPrePrepare preprepare = null;
     Quorum prq = null;
     Quorum cmq = null;

    
    public PBFTLogEntry(PBFTPrePrepare preprepare) {
        
            this.preprepare = preprepare;
        
    }

//    public void setQuorum(PBFTQuorum q){
//        synchronized(this){
////        if(check(q)){
//            if(q.get(0) instanceof PBFTPrepare){
//                setPrepareQuorum(q);
//            }
//
//            if(q.get(0) instanceof PBFTCommit){
//                setCommitQuorum(q);
//            }
//  //      }
//        }
//    }

    public Quorum getCommitQuorum() {
        return cmq;
    }

    public void setCommitQuorum(Quorum q) {
            this.cmq = q;
    }

    public Quorum getPrepareQuorum() {
        return prq;
    }

    public void setPrepareQuorum(Quorum q) {
            this.prq = q;
    }

    public PBFTPrePrepare getPrePrepare() {
        return preprepare;
    }
  
    public Long getSequenceNumber(){

        return getPrePrepare().getSequenceNumber();
    }

    public Integer getViewNumber(){
        return getPrePrepare().getViewNumber();
    }

//    public boolean isNOP(){
//
//        boolean nop = true;
//
//        if(getPrepareQuorum() == null){
//            return false;
//        }
//
//        for(IMessage m : getPrepareQuorum()){
//            PBFTServerMessage m1 = (PBFTServerMessage) m;
//            nop = nop && m1.isNop();
//        }
//
//        if(getCommitQuorum() == null){
//            return false;
//        }
//
//        for(IMessage m : getCommitQuorum()){
//            PBFTServerMessage m1 = (PBFTServerMessage) m;
//            nop = nop && m1.isNop();
//        }
//
//        return nop;
//    }
//    public void setNOP(){
//        getPrePrepare().setNop(true);
//        getPrepareQuorum().setNOP();
//        getCommitQuorum().setNOP();
//    }
//
//    public void setNOP(Object replicaID){
//
//        if(getPrePrepare().getReplicaID().equals(replicaID)){
//            getPrePrepare().setNop(true);
//        }
//
//        for(IMessage m : getPrepareQuorum()){
//            PBFTServerMessage m1 = (PBFTServerMessage) m;
//            if(m1.getReplicaID().equals(replicaID)){
//                m1.setNop(true);
//            }
//        }
//
//        for(IMessage m : getCommitQuorum()){
//            PBFTServerMessage m1 = (PBFTServerMessage) m;
//            if(m1.getReplicaID().equals(replicaID)){
//                m1.setNop(true);
//            }
//        }
//
//    }
}
