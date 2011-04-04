/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision.auction;

import br.ufba.lasid.jds.decision.bargaining.IProposal;
import br.ufba.lasid.jds.decision.bargaining.auction.ILot;
import br.ufba.lasid.jds.decision.bargaining.auction.SoftAuction;

/**
 *
 * @author aliriosa
 */
public class NewViewAuction extends SoftAuction{

   public NewViewAuction(long lcwm, long hcwm, int compstrategy) {
      super(new NewViewProposalValitor(lcwm, hcwm), compstrategy);
   }

   public void addProposal(long seqn, IProposal proposal) {
      super.addProposal(new Sequence(seqn), proposal);
   }


}
