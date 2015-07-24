/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.MessageCollection;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class PBFTNewView extends PBFTServerMessage{
    MessageCollection preprepareSet = new MessageCollection();
    Hashtable<Object, String> changeViewTable = new Hashtable<Object, String>();
    String digest;

   public Hashtable<Object, String> getChangeViewtable() {
      return changeViewTable;
   }
    
   public MessageCollection getPrePrepareSet() {
      return preprepareSet;
   }

   private String preprepareSetToString(){
      String str = "";
      String more = "";

      for(IMessage m : preprepareSet){
         str += more + m.toString();
         more = ",";
      }
      return str;
   }

   public String getDigest() {
      return digest;
   }

   public void setDigest(String digest) {
      this.digest = digest;
   }

   
   
    @Override
    public final String toString() {

        return (
                "<NEW-VIEW" + ", " +
                 "VIEW = " + getViewNumber().toString() + ", " +
                 "V = " + getChangeViewtable().toString() + ", " +
                 "X = {" + "<CHECKPOINT SEQN = " + getSequenceNumber()+ ", DIGEST = " + digest + ">; " + preprepareSetToString() + "}, " +
                 "SENDER = " + getReplicaID().toString() +
                 ">"
        );
    }

   public int getTAG() {
      return IPBFTServer.NEWVIEW;
   }

   public String getTAGString() {
      return "NEWVIEW";
   }
    
}
