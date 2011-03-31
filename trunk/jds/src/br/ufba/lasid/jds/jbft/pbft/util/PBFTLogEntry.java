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
