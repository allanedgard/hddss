/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.bargaining.auction;

import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.bargaining.IProposal;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class AuctionResult extends Hashtable<ILot,  ILotResult> implements IAuctionResult{

   public static final int LOTS = 0;
   public static final int PROPOSALS = 1;
   public static final int BIDCOUNT = 2;
   
   public ILotResult get(ILot lote) {
      return super.get(lote);
   }

   public LotList getLots() {
      LotList lots = new LotList();
      lots.addAll(super.keySet());
      return lots;
   }

   public boolean equals(ISubject s) {
      if(!(s != null && (s instanceof IAuctionResult))){
         return false;
      }

      IAuctionResult other = (IAuctionResult)s;
      LotList olots = other.getLots();
      LotList tlots = this.getLots();

      if(!olots.equals(tlots)){
         return false;
      }

      for(ILot lot : tlots){
         ILotResult tlr = this.get(lot);
         ILotResult olr = other.get(lot);

         if(!tlr.equals(olr)){
            return false;
         }
      }
      return true;
   }

   public Object getInfo(int i) {
      if(i == LOTS) return getLots();
      if(i == PROPOSALS) return super.values();

      return null;
   }

   public synchronized void put(ILot key, IProposal proposal, int count) {
         ILotResult lresult = get(key);
         lresult.add(proposal, count);

   }


}
