/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTChangeView extends PBFTServerMessage{

    Object cset;
    Object ppset;
    Object pset;
    public void setCheckpointSet(Object set){
        cset = set;//put(AbstractPBFTMessage.STOREDCHECKPOINTSET, set);
    }

    public Object getCheckpointSet(){
        return cset;//get(AbstractPBFTMessage.STOREDCHECKPOINTSET);
    }

    public void setPrePrepareSet(Object set){
        ppset = set;//put(AbstractPBFTMessage.PREPREPAREDSET, set);
    }

    public Object getPrePrepareSet(){
        return ppset; //get(AbstractPBFTMessage.PREPREPAREDSET);
    }

    public void setPrepareSet(Object set){
        pset = set;//put(AbstractPBFTMessage.PREPAREDSET, set);
    }

    public Object getPrepareSet(){
        return pset;//get(AbstractPBFTMessage.PREPAREDSET);
    }

    @Override
    public final synchronized String toString() {

        return (
                "<VIEW-CHANGE" + ","                        +
                 "VIEW = " + getViewNumber().toString()     + ", " +
                 "SEQ = " + getSequenceNumber().toString()  + ", " +
                 "C = " + getCheckpointSet().toString()     + "," +
                 "P = " + getPrepareSet().toString()        + "," +
                 "Q = " + getPrePrepareSet().toString()     + "," +
                 "SENDER = " + getReplicaID().toString()    +
                 ">"
        );
    }

}
