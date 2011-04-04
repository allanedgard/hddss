/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.MessageCollection;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class PBFTNewView extends PBFTServerMessage{
    MessageCollection preprepareSet = new MessageCollection();
    Hashtable<String, PBFTChangeView> changeViewTable = new Hashtable<String, PBFTChangeView>();

   public Hashtable<String, PBFTChangeView> getChangeViewTable() {
      return changeViewTable;
   }
    
   public MessageCollection getPreprepareSet() {
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

    @Override
    public final String toString() {

        return (
                "<NEW-VIEW" + "," +
                 "VIEW = " + getViewNumber().toString() + ", " +
                 "V = " + getChangeViewTable().toString() + ", " +
                 "X = " + preprepareSetToString() + ", " +
                 "SENDER = " + getReplicaID().toString()+ 
                 ">"
        );
    }
    
}
