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
public class PBFTStatusPending extends PBFTServerMessage{
   
   protected ArrayList changeViewReplicas = new ArrayList();
   
   protected boolean newView;
   protected long lastExecutedSequenceNumber;
   protected long lastStableCheckpointSequenceNumber;
   protected DigestList missedRequests = new DigestList();
   
   public PBFTStatusPending(long bseqn, int viewn, long lastExecuted, long lastStableCheckpointSEQ, Object replicaID, boolean hasNewView) {
      setViewNumber(viewn);
      setReplicaID(replicaID);
      setLastExecutedSequenceNumber(lastExecuted);
      setLastStableCheckpointSequenceNumber(lastStableCheckpointSEQ);
      setNewView(hasNewView);
      setSequenceNumber(bseqn);
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

   public DigestList getMissedRequests() {
      return missedRequests;
   }

   public long getLastExecutedSequenceNumber() {
      return lastExecutedSequenceNumber;
   }

   public void setLastExecutedSequenceNumber(long lastExecutedSequenceNumber) {
      this.lastExecutedSequenceNumber = lastExecutedSequenceNumber;
   }

   public long getLastStableCheckpointSequenceNumber() {
      return lastStableCheckpointSequenceNumber;
   }

   public void setLastStableCheckpointSequenceNumber(long lastStableCheckpointSequenceNumber) {
      this.lastStableCheckpointSequenceNumber = lastStableCheckpointSequenceNumber;
   }

   private String replicasToString(){
      String str = "";
      String more = "";
      for(Object rid : changeViewReplicas){
         str += more + rid;
         more = ",";
      }

      return str;
   }
   private String missedRequestsToString(){
      String str = "";
      String more = "";

      for(String digest : missedRequests){
         str += more + digest;
         more = "; ";
      }
      return str;
   }
   @Override
   public final String toString() {
      Object rid = getReplicaID();
      return "<STATUS-PENDING," +
                  "BSEQN = " + getSequenceNumber() + ", " +
                  "VIEW = " + getViewNumber() + ", " +
                  "LCWM = " + getLastStableCheckpointSequenceNumber() + ", " +
                  "LAST-EXECUTED = " + getLastExecutedSequenceNumber() + ", " +
                  "HAS-NEWVIEW = " + hasNewView() + ", " +
                  "REPLICAID = " + (rid == null ? "NULL" : rid) + ", " +
                  "MISSED-PREPARES = {" + missedRequestsToString() + "}, " +
                  "VIEW-CHANGE-REPLICAS = {"  + replicasToString() + "}" +
             ">";
   }

   public int getTAG() {
      return IPBFTServer.STATUSPENDING;
   }

   public String getTAGString() {
      return "STATUSPENDING";
   }

}
