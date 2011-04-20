/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author aliriosa
 */
public class PBFTChangeViewACKInfo {

   TreeMap<Integer, ArrayList<PBFTChangeViewACK>> table = new TreeMap<Integer, ArrayList<PBFTChangeViewACK>>();

   public boolean put(PBFTChangeViewACK ack){
      if(isValid(ack)){
         if(!contains(ack)){
            int view = ack.getViewNumber();
            ArrayList<PBFTChangeViewACK> acks = table.get(view);

            if(acks == null){
               acks = new ArrayList<PBFTChangeViewACK>();
               table.put(view, acks);
            }

            acks.add(ack);
            return true;
         }
      }

      return false;
   }

   public boolean contains(Object senderID, Object prompterID, int view){
      if(senderID != null && prompterID != null && view >= 0){
         if(!table.isEmpty()){
            if(table.containsKey(view)){
               List<PBFTChangeViewACK> acks = table.get(view);
               for(PBFTChangeViewACK me : acks){
                  if(me.getReplicaID().equals(senderID) && me.getPrompterID().equals(prompterID)){
                     return true;
                  }
               }
            }
         }
      }

      return false;      
   }
   
   public boolean contains(PBFTChangeViewACK ack){
      if(isValid(ack)){
         if(!table.isEmpty()){
            int view = ack.getViewNumber();

            if(table.containsKey(view)){
               List<PBFTChangeViewACK> acks = table.get(view);
               for(PBFTChangeViewACK me : acks){
                  if(me.getReplicaID().equals(ack.getReplicaID()) && me.getPrompterID().equals(ack.getPrompterID()) && me.getDigest().equals(ack.getDigest())){
                     return true;
                  }
               }
            }
         }
      }

      return false;
      
   }
   public boolean isValid(PBFTChangeViewACK ack){
      if(!(ack != null && ack.getPrompterID() != null && ack.getReplicaID() != null && ack.getDigest() != null)){
         return false;
      }
      return true;
   }

   public boolean gc(int view){
      boolean ok = false;
      if(!table.isEmpty()){
         int start = table.firstKey();

         for(int lookup = start; lookup <= view; lookup++){
            table.remove(lookup);
         }

      }
      return ok;
   }

   public int count(Object prompterID, String digest, int view){
      return count(prompterID, digest, view, false);
   }

   public int count(Object prompterID, String digest, int view, boolean includePrompter){
      int count = 0;
      if(!table.isEmpty()){
         if(table.containsKey(view)){
            ArrayList<PBFTChangeViewACK> acks = table.get(view);
            for(PBFTChangeViewACK ack : acks){

               if((!ack.getPrompterID().equals(ack.getReplicaID()) || includePrompter) && ack.getPrompterID().equals(prompterID) && ack.getDigest().equals(digest)){
                  count++;
               }
            }
         }
      }
      return count;
   }
}
