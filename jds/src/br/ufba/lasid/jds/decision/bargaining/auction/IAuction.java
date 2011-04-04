/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.bargaining.auction;

import br.ufba.lasid.jds.decision.bargaining.IBargaining;
import br.ufba.lasid.jds.decision.bargaining.IProposal;

/**
 *
 * @author aliriosa
 */
public interface IAuction extends IBargaining{

   public void addProposal(ILot lot, IProposal proposal);

}
