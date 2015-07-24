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
public class PBFTCommit extends PBFTServerMessage{

    public PBFTCommit(PBFTPrepare p, Object replicaID){
        setViewNumber(p.getViewNumber());
        setSequenceNumber(p.getSequenceNumber());
        setReplicaID(replicaID);
    }

    public PBFTCommit(Integer viewNumber, Long sequenceNumber, Object replicaID){
        setViewNumber(viewNumber);
        setSequenceNumber(sequenceNumber);
        setReplicaID(replicaID);
    }
    
    @Override
    public final String toString() {

        return (
                "<COMMIT" + ", " +
                 "VIEW = " + getViewNumber().toString() + ", " +
                 "SEQUENCE = " + getSequenceNumber().toString() + ", " +
                 "SENDER = " + getReplicaID().toString() +
                 ">"
        );
    }

   public int getTAG() {
      return IPBFTServer.COMMIT;
   }

   public String getTAGString() {
      return "COMMIT";
   }
}
