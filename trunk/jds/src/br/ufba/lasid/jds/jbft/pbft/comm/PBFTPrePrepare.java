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
public class PBFTPrePrepare extends PBFTServerMessage{

    protected DigestList digests = new DigestList();

    public PBFTPrePrepare(Integer viewNumber, Long sequenceNumber, Object replicaID) {
        this.viewNumber = viewNumber;
        this.sequenceNumber = sequenceNumber;
        this.replicaID = replicaID;
    }

    

    public DigestList getDigests() {
        return digests;
    }

    public String digestsToString(){
        String str  = "";
        String more = "";
        for(String s : digests){
            str += more + s;
            more = ";";
        }

        return str;
    }

    @Override
    public final synchronized String toString() {
        
        return (
                "<PRE-PREPARE" + ", " +
                 "VIEW = " + getViewNumber().toString()     + ", " +
                 "SEQUENCE = " + getSequenceNumber().toString()  + ", " +
                 "SERVER = " + getReplicaID().toString()    + ", " +
                 "DIGESTS = {" + digestsToString()+ "}"         +
                 ">"
        );
    }


}
