/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.MessageCollection;
import br.ufba.lasid.jds.jbft.pbft.server.decision.auction.PrePrepareProposal;
import java.util.ArrayList;
import java.util.BitSet;

/**
 *
 * @author aliriosa
 */
public class PBFTStatusPending extends PBFTServerMessage{
   
   protected ArrayList changeViewReplicas = new ArrayList();
   
   protected boolean newView;
   protected long lastExecutedSequenceNumber;
   protected long lastStableCheckpointSequenceNumber;
   protected MessageCollection missedPrepares = new MessageCollection();
   
   public PBFTStatusPending(int viewn, long lastExecuted, long lastStableCheckpointSEQ, Object replicaID, boolean hasNewView) {
      setViewNumber(viewn);
      setReplicaID(replicaID);
      setLastExecutedSequenceNumber(lastExecuted);
      setLastStableCheckpointSequenceNumber(lastStableCheckpointSEQ);
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

   public MessageCollection getMissedPrepares() {
      return missedPrepares;
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
   private String missedPreparesToString(){
      String str = "";
      String more = "";

      for(IMessage m : missedPrepares){
         PBFTPrePrepare pp = (PBFTPrePrepare) m;
         str += more + "< SEQN = " + pp.getSequenceNumber() + ", VIEW = " + pp.getViewNumber() + ">";
         more = "; ";
      }
      return str;
   }
   @Override
   public final String toString() {
      Object rid = getReplicaID();
      return "<STATUS-PENDING," +
                  "VIEW = " + getViewNumber() + ", " +
                  "LCWM = " + getLastStableCheckpointSequenceNumber() + ", " +
                  "LAST-EXECUTED = " + getLastExecutedSequenceNumber() + ", " +
                  "HAS-NEWVIEW = " + hasNewView() + ", " +
                  "REPLICAID = " + (rid == null ? "NULL" : rid) + ", " +
                  "MISSED-PREPARES = {" + missedPreparesToString() + "}, " +
                  "VIEW-CHANGE-REPLICAS = {"  + replicasToString() + "}" +
             ">";
   }

}
