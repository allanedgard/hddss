/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTFetchMetaData extends PBFTServerMessage{


    public PBFTFetchMetaData(Long lc, Object replicaID){
        setSequenceNumber(lc);
        setReplicaID(replicaID);
    }

    @Override
    public final String toString() {

        return (
                "<FETCH-META-DATA" + ", " +
                 "LC = " + getSequenceNumber() + ", " +
                 "SENDER = " + getReplicaID().toString() +
                 ">"
        );
    }
}
