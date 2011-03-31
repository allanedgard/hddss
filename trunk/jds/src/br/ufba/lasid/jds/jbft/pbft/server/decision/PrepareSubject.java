/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision;

import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.Subject;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;

/**
 *
 * @author aliriosa
 */
public class PrepareSubject extends Subject{
   
   protected PBFTPrepare prepare;

   public static final int SEQUENCENUMBER = 0;
   public static final int     VIEWNUMBER = 1;
   public static final int     DIGESTLIST = 2;

   public PrepareSubject(PBFTPrepare prepare) {
      this.prepare = prepare;
   }
   
   public boolean equals(ISubject b) {
      if(!(b != null && (b instanceof PrepareSubject))){
         return false;
      }

      PrepareSubject ps = (PrepareSubject) b;
      
      try{
         return (
            ps.prepare.getViewNumber().equals(prepare.getViewNumber())           &&
            ps.prepare.getSequenceNumber().equals(prepare.getSequenceNumber())   &&
            ps.prepare.getDigests().equals(prepare.getDigests())  
         );
         
      }catch(Exception e){
         return false;
      }
   }

   public PBFTPrepare getPrepare(){
      return prepare;
   }
   public Object getInfo(int i) {
      switch(i){
         case SEQUENCENUMBER:
            return prepare.getSequenceNumber();
         case VIEWNUMBER:
            return prepare.getViewNumber();
         case DIGESTLIST:
            return prepare.getDigests();
         default:
            return null;
      }
   }

}
