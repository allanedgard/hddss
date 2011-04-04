/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision.auction;

import br.ufba.lasid.jds.decision.bargaining.auction.ILot;
import br.ufba.lasid.jds.decision.bargaining.auction.LotList;

/**
 *
 * @author aliriosa
 */
public class SequenceList extends LotList{

   public Sequence get(long seqn){
      Sequence lookup = null;
      for(ILot lot : this){
         if(lot instanceof Sequence){
            Sequence current = (Sequence)lot;
            if(current.sequence == seqn){
               lookup = current;
               break;
            }
         }
      }
      return lookup;
   }

   public boolean contains(long seqn){
      return get(seqn) != null;
   }

   public boolean add(long seqn) {
      return super.add(new Sequence(seqn));
   }


}
