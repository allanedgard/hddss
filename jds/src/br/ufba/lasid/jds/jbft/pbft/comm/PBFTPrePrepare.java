/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTPrePrepare extends PBFTMessageOrdering{

    public PBFTPrePrepare(Integer viewNumber, Long sequenceNumber, Object replicaID) {
        this.viewNumber = viewNumber;
        this.sequenceNumber = sequenceNumber;
        this.replicaID = replicaID;
    }

    @Override
    public final String toString() {
        Object rid = getReplicaID();
        return (
                "<PRE-PREPARE" + ", " +
                 "VIEW = " + getViewNumber().toString()     + ", " +
                 "SEQUENCE = " + getSequenceNumber().toString()  + ", " +
                 "SERVER = " + (rid == null ? "NULL" : rid)    + ", " +
                 "DIGESTS = {" + digestsToString()+ "}"         +
                 ">"
        );
    }


}
