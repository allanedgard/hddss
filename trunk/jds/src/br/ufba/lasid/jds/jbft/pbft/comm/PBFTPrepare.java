/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.util.DigestList;

/**
 *
 * @author aliriosa
 */
public class PBFTPrepare extends PBFTMessageOrdering{
    
    public PBFTPrepare(Integer viewNumber, Long sequenceNumber, Object replicaID){

        setViewNumber(viewNumber);
        setSequenceNumber(sequenceNumber);
        setReplicaID(replicaID);

    }

    public PBFTPrepare(PBFTPrePrepare pp,  Object replicaID){

        setViewNumber(pp.getViewNumber());
        setSequenceNumber(pp.getSequenceNumber());
        digests.addAll(pp.getDigests());
        setReplicaID(replicaID);

    }

    public PBFTPrepare(Integer viewNumber, Long sequenceNumber, DigestList digests, Object replicaID){

        setViewNumber(viewNumber);
        setSequenceNumber(sequenceNumber);
        digests.addAll(digests);
        setReplicaID(replicaID);
        
    }

    @Override
    public final String toString() {
        Object rid = getReplicaID();
        return (
                "<PREPARE" + ", " +
                 "VIEW = " + getViewNumber().toString() + ", " +
                 "SEQUENCE = " + getSequenceNumber().toString() + ", " +
                 "SERVER = " + (rid == null ? "NULL" : rid) + ", " +
                 "DIGESTS = {" + digestsToString()+ "}" +
                 ">"
        );
    }

}
