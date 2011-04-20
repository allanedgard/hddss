/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision;

import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.Subject;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;

/**
 *
 * @author aliriosa
 */
public class PrePrepareSubject extends Subject{

   protected PBFTPrePrepare preprepare;

   public static final int SEQUENCENUMBER = 0;
   public static final int     VIEWNUMBER = 1;
   public static final int     DIGESTLIST = 2;

   public PrePrepareSubject(PBFTPrePrepare preprepare) {
      this.preprepare = preprepare;
   }

   public boolean equals(ISubject b) {
      if(!(b != null && (b instanceof PrePrepareSubject))){
         return false;
      }

      PrePrepareSubject ps = (PrePrepareSubject) b;

      try{
         return (
            ps.preprepare.getViewNumber().equals(preprepare.getViewNumber())           &&
            ps.preprepare.getSequenceNumber().equals(preprepare.getSequenceNumber())   &&
            ps.preprepare.getDigests().equals(preprepare.getDigests())
         );

      }catch(Exception e){
         return false;
      }
   }

   public PBFTPrePrepare getPrePrepare(){
      return preprepare;
   }
   public Object getInfo(int i) {
      switch(i){
         case SEQUENCENUMBER:
            return preprepare.getSequenceNumber();
         case VIEWNUMBER:
            return preprepare.getViewNumber();
         case DIGESTLIST:
            return preprepare.getDigests();
         default:
            return null;
      }
   }

}
