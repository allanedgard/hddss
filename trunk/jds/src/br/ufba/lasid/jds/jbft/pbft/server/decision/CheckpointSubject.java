/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision;

import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.Subject;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;

/**
 *
 * @author aliriosa
 */
public class CheckpointSubject extends Subject{

   protected PBFTCheckpoint checkpoint;

   public static final int SEQUENCENUMBER = 0;
   public static final int         DIGEST = 1;

   public CheckpointSubject(PBFTCheckpoint checkpoint) {
      this.checkpoint = checkpoint;
   }

   public boolean equals(ISubject b) {
      if(!(b != null && (b instanceof CheckpointSubject))){
         return false;
      }

      CheckpointSubject cs = (CheckpointSubject) b;

      try{
         return (
            cs.checkpoint.getDigest().equals(checkpoint.getDigest())                   &&
            cs.checkpoint.getSequenceNumber().equals(checkpoint.getSequenceNumber())
         );

      }catch(Exception e){
         return false;
      }
   }

   public Object getInfo(int i) {
      switch(i){
         case SEQUENCENUMBER:
            return checkpoint.getSequenceNumber();
         case DIGEST:
            return checkpoint.getDigest();
         default:
            return null;
      }
   }


}
