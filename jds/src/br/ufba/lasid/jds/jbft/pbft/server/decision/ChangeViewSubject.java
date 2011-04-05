/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision;

import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.Subject;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeView;

/**
 *
 * @author aliriosa
 */
public class ChangeViewSubject extends Subject{

   protected PBFTChangeView changeview;

   public static final int    VIEWNUMBER = 0;
   public static final int     REPLICAID = 1;
   public static final int PREPREPARESET = 2;
   public static final int    PREPARESET = 3;
   public static final int CHECKPOINTSET = 4;

   public ChangeViewSubject(PBFTChangeView changeview) {
      this.changeview = changeview;
   }

   public boolean equals(ISubject b) {
      if(!(b != null && (b instanceof ChangeViewSubject))){
         return false;
      }

      ChangeViewSubject cv = (ChangeViewSubject) b;

      try{
         return (
            cv.changeview.getViewNumber().equals(changeview.getViewNumber())
         );

      }catch(Exception e){
         return false;
      }
   }

   public PBFTChangeView getChangeView(){
      return changeview;
   }
   public Object getInfo(int i) {
      switch(i){
         case REPLICAID:
            return changeview.getReplicaID();
         case VIEWNUMBER:
            return changeview.getViewNumber();
         case PREPREPARESET:
            return changeview.getPrePrepareSet();
         case PREPARESET:
            return changeview.getPrepareSet();
         case CHECKPOINTSET:
            return changeview.getCheckpointSet();
         default:
            return null;
      }
   }

}
