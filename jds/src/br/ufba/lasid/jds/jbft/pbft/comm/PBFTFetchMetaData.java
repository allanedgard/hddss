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

   public int getTAG() {
      return -1;
   }

   public String getTAGString() {
      return "FETCHMETADATA";
   }
}
