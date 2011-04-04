/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.bargaining.auction;

import br.ufba.lasid.jds.decision.ISubject;

/**
 *
 * @author aliriosa
 */
public interface IAuctionResult extends ISubject{

   public ILotResult get(ILot lot);
   public LotList getLots();

}
