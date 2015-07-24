/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
import br.ufba.lasid.jds.util.DigestList;
import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class PBFTStatusPending extends PBFTStatusActive{
   
   protected ArrayList changeViewReplicas = new ArrayList();
   
   protected boolean newView;
   protected long lastExecutedSequenceNumber;
   protected long lastStableCheckpointSequenceNumber;   
   
   public PBFTStatusPending(long bseqn, int viewn, long lastExecuted, long lastStableCheckpointSEQ, Object replicaID, boolean hasNewView) {
      super(bseqn, replicaID, viewn, lastExecuted, lastStableCheckpointSEQ);
      setNewView(hasNewView);

   }


   public boolean hasNewView() {
      return newView;
   }

   public void setNewView(boolean newView) {
      this.newView = newView;
   }

   public ArrayList getChangeViewReplicas() {
      return changeViewReplicas;
   }

   private String replicasToString(){
      String str = "";
      String more = "";
      for(Object rid : changeViewReplicas){
         str += more + rid;
         more = ", ";
      }

      return str;
   }

   @Override
   public final String toString() {
      Object rid = getReplicaID();
      return "<STATUS-PENDING," +
                  "BSEQN = " + getSequenceNumber() + ", " +
                  "VIEW = " + getViewNumber() + ", " +
                  "LCWM = " + getLastExecutedSEQ() + ", " +
                  "LAST-EXECUTED = " + getLastExecutedSEQ() + ", " +
                  "HAS-NEWVIEW = " + hasNewView() + ", " +
                  "REPLICAID = " + (rid == null ? "NULL" : rid) + ", " +
                  "VIEW-CHANGE-REPLICAS = {"  + replicasToString() + "}, " +
                  "MISSED-REQUESTS = {" + digestsToString() + "} " +
             ">";
   }

   @Override
   public int getTAG() {
      return IPBFTServer.STATUSPENDING;
   }

   @Override
   public String getTAGString() {
      return "STATUSPENDING";
   }

}
