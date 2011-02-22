/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;

/**
 *
 * @author aliriosa
 */
public class PBFTLogEntry {

    volatile PBFTPrePrepare preprepare = null;
    volatile Quorum prq = null;
    volatile Quorum cmq = null;
    
    public PBFTLogEntry(PBFTPrePrepare preprepare) {
        
            this.preprepare = preprepare;
        
    }

    public synchronized void setQuorum(Quorum q){

//        if(check(q)){
            if(q.get(0) instanceof PBFTPrepare){
                setPrepareQuorum(q);
            }

            if(q.get(0) instanceof PBFTCommit){
                setCommitQuorum(q);
            }
  //      }
    }

    public synchronized Quorum getCommitQuorum() {
        return cmq;
    }

    public synchronized void setCommitQuorum(Quorum q) {
            this.cmq = q;
    }

    public synchronized Quorum getPrepareQuorum() {
        return prq;
    }

    public synchronized void setPrepareQuorum(Quorum q) {
            this.prq = q;
    }

    public synchronized PBFTPrePrepare getPrePrepare() {
        return preprepare;
    }
  
    public synchronized Long getSequenceNumber(){

        return getPrePrepare().getSequenceNumber();
    }

    public synchronized Integer getViewNumber(){
        return getPrePrepare().getViewNumber();
    }
    
}
