/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public abstract class AbstractPBFTServerMessage extends PBFTMessage{

    private Long sequenceNumber = null;
    private Integer viewNumber = null;
    private Object replicaID = null;
    private String digest = null;
    
    public Long getSequenceNumber(){
        return sequenceNumber;
    }

    public void setSequenceNumber(Long seqn){
        this.sequenceNumber = seqn;
    }

    public Integer getViewNumber(){
        return viewNumber;
    }

    public void setViewNumber(Integer viewn){
        this.viewNumber = viewn;
    }

    public Object getReplicaID(){
        return replicaID;
    }

    public void setReplicaID(Object ID){
        replicaID = ID;
    }

    public String getDigest(){
        return digest;
    }

    public void setDigest(String digest){
        this.digest = digest;
    }

    public String getRound(){
        
        String seqn = "";

        if(getSequenceNumber() != null){
            seqn = getSequenceNumber().toString();
        }

        return (
          "<ROUND "  +
            "VIEW="  + getViewNumber().toString()      + ", " +
            "SEQ = " + seqn                            +
          ">"
        );
    }

    
}
