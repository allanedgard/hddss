/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision;

import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.Subject;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBag;

/**
 *
 * @author aliriosa
 */
public class BagSubject extends Subject{

   protected PBFTBag bag;

   public static final int REPLICAID = 0;
   public static final int MESSAGES  = 1;
   public static final int SEQUENCENUMBER = 2;


   public BagSubject(PBFTBag bag) {
      this.bag = bag;
   }

   public boolean equals(ISubject b) {
      if(!(b != null && (b instanceof BagSubject))){
         return false;
      }

      return true;

   }

   public Object getInfo(int i) {
      switch(i){
         case REPLICAID:
            return bag.getReplicaID();
         case MESSAGES:
            return bag.getMessages();
         case SEQUENCENUMBER:
            return bag.getSequenceNumber();

         default:
            return null;
      }
   }


}
