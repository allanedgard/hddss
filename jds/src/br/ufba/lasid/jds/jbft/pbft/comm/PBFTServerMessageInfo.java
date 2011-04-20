/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.MessageCollection;
import java.util.TreeMap;

/**
 *
 * @author aliriosa
 */
public class PBFTServerMessageInfo extends TreeMap<Long, MessageCollection>{

   int minimum = 0;

   public PBFTServerMessageInfo(int minimum) {
      this.minimum = minimum;
   }

   public void put(PBFTServerMessage m){
      if(m != null && m.getSequenceNumber() != null){
         Long seqn = m.getSequenceNumber();
         this.put(seqn, m);
      }
   }
   
   protected void put(long seqn, PBFTServerMessage m){
      if((m == null)){
         return;
      }


      MessageCollection msgs = this.get(seqn);

      if(msgs == null){
         msgs = new MessageCollection();
         super.put(seqn, msgs);
      }

      msgs.add(m);
   }

   
   public boolean hasEnough(long seqn){
      return count(seqn) >= minimum;
   }

   public int count(long seqn){
      if(!isEmpty() && containsKey(seqn)){
         MessageCollection msgs = get(seqn);
         return msgs.size();
      }
      return 0;
      
   }

   public long getFirstSequenceNumber(){
      if(!isEmpty()){
         return firstKey();
      }

      return -3;
   }

   public long getLastSequenceNumber(){
      if(!isEmpty()){
         return lastKey();
      }

      return -2;
   }

   public void removeRange(long startSEQ, long finalSEQ){
      for(long ikey = finalSEQ; ikey >= startSEQ; ikey--){
         remove(ikey);
      }
   }

   public void removeLowerOrEqaul(long seqn){
      removeRange(getFirstSequenceNumber(), seqn);
   }

   public void removeGreaterOrEqual(long seqn){
      removeRange(seqn, getLastSequenceNumber());
   }

   public void removeAbove(long seqn){
      removeGreaterOrEqual(seqn+1);
   }

   public void removeBellow(long seqn){
      removeLowerOrEqaul(seqn-1);
   }

   public PBFTServerMessage getMine(Object pid, long seqn){

      if(pid != null && containsKey((Long)seqn)){
         MessageCollection msgs = get((Long)seqn);

         for(IMessage m : msgs){
            PBFTServerMessage sm = (PBFTServerMessage) m;
            if(pid.equals(sm.getReplicaID())){
               return sm;
            }
         }
      }

      return null;
   }
  
   
}
