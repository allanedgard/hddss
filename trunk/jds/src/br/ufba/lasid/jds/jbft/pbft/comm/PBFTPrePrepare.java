/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;

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

   public int getTAG() {
      return IPBFTServer.PREPREPARE;
   }

   public String getTAGString() {
      return "PREPREPARE";
   }


}
