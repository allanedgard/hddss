/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.jbft.pbft.util.CheckpointIndex;
import br.ufba.lasid.jds.jbft.pbft.util.CheckpointIndexList;

/**
 *
 * @author aliriosa
 */
public class PBFTMetaData extends PBFTServerMessage{

    CheckpointIndexList indexes = new CheckpointIndexList();

    public PBFTMetaData(Long seqn, Object replicaID) {
        setSequenceNumber(seqn);
        setReplicaID(replicaID);
    }


    public CheckpointIndexList getIndexes() {
        return indexes;
    }

    public void addIndex(CheckpointIndex _index){
        if(_index == null){
            return;
        }
        for(CheckpointIndex index : indexes){
            if(index.getIndexPattern().equals(_index.getIndexPattern())){
                return;
            }
        }

        indexes.add(_index);
        
    }
    public void addIndex(String pattern){
        addIndex(new CheckpointIndex(pattern));
    }

    public void addIndex(String digest, long seqn){
        addIndex(new CheckpointIndex(sequenceNumber, digest));
    }

    @Override
    public final String toString() {
        String tuples = "";
        String more  = "";
        for(CheckpointIndex index : indexes){
            tuples += more + "<" + index.getSequenceNumber() + ", " + index.getDigest() + ">";
            more = ";";
        }
        
        return (
                "<META-DATA" + ", " +
                 "CURRENT-LCWM = " + getSequenceNumber() + ", " +
                 "SUBPARTITION-TUPLES  = " + tuples + ", " +
                 "SENDER = " + getReplicaID() + ", " +
                 ">"
        );
    }
}
