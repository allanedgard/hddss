/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision;

import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.Subject;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewACK;

/**
 *
 * @author aliriosa
 */
public class ChangeViewACKSubject extends Subject{

   protected PBFTChangeViewACK changeviewACK;

   public static final int   PROMPTER = 0;
   public static final int VIEWNUMBER = 1;
   public static final int     DIGEST = 2;

   public ChangeViewACKSubject(PBFTChangeViewACK changeviewACK) {
      this.changeviewACK = changeviewACK;
   }

   public boolean equals(ISubject b) {
      if(!(b != null && (b instanceof ChangeViewACKSubject))){
         return false;
      }

      ChangeViewACKSubject cva = (ChangeViewACKSubject) b;

      try{
         return (
            cva.changeviewACK.getViewNumber().equals(changeviewACK.getViewNumber())           &&
            cva.changeviewACK.getDigest().equals(changeviewACK.getDigest())                   &&
            cva.changeviewACK.getPrompterID().equals(changeviewACK.getPrompterID())
         );

      }catch(Exception e){
         return false;
      }
   }

   public PBFTChangeViewACK getChangeViewACK(){
      return changeviewACK;
   }
   public Object getInfo(int i) {
      switch(i){
         case PROMPTER:
            return changeviewACK.getPrompterID();
         case VIEWNUMBER:
            return changeviewACK.getViewNumber();
         case DIGEST:
            return changeviewACK.getDigest();
         default:
            return null;
      }
   }

}
