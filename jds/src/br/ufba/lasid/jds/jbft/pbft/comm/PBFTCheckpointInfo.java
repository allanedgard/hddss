/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.MessageCollection;

/**
 *
 * @author aliriosa
 */
public class PBFTCheckpointInfo extends PBFTServerMessageInfo{

   public PBFTCheckpointInfo(int f) {
      super(2 * f + 1);
   }

   @Override
   public void put(PBFTServerMessage m) {
      if(m != null && m.getSequenceNumber() != null && m instanceof PBFTCheckpoint && ((PBFTCheckpoint)m).getDigest() != null){
         this.put(m.getSequenceNumber(), m);
      }
   }


   @Override
   protected void put(long seqn, PBFTServerMessage m) {

      if(m != null && m.getSequenceNumber() != null && (m instanceof PBFTCheckpoint) && ((PBFTCheckpoint)m).getDigest() != null ){
         if(!this.contains((PBFTCheckpoint)m)){
            super.put(seqn, m);
         }
      }

   }


   public boolean contains(PBFTCheckpoint m){

      if(m!= null && m.getSequenceNumber() != null && m.getDigest() != null){

         Long     seqn = m.getSequenceNumber();
         String digest = m.getDigest();

         if(!containsKey(seqn)){
            return false;
         }

         MessageCollection msgs = super.get(seqn);

         if(msgs.isEmpty()){
            return false;
         }

         for(IMessage im : msgs){

            PBFTCheckpoint me = (PBFTCheckpoint) im;

            if(me.getSequenceNumber() != null && me.getSequenceNumber().equals(seqn)){

               if(me.getDigest() != null && me.getDigest().equals(digest)){

                  if(me.getReplicaID() != null && me.getReplicaID().equals(m.getReplicaID())){
                     return true;
                  }

               }

            }

         }

      }

      return false;
   }


}
