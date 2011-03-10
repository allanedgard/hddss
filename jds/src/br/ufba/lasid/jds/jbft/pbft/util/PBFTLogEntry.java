/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
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

    public void setQuorum(Quorum q){
        synchronized(this){
//        if(check(q)){
            if(q.get(0) instanceof PBFTPrepare){
                setPrepareQuorum(q);
            }

            if(q.get(0) instanceof PBFTCommit){
                setCommitQuorum(q);
            }
  //      }
        }
    }

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
    
}
