/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision.auction;

import br.ufba.lasid.jds.decision.bargaining.IProposal;
import br.ufba.lasid.jds.decision.bargaining.IProposalValidator;
import br.ufba.lasid.jds.util.DigestList;

/**
 *
 * @author aliriosa
 */
public class NewViewProposalValitor implements IProposalValidator{
   protected long lcwm;
   protected long hcwm;

   public NewViewProposalValitor(long lcwm, long hcwm) {
      this.lcwm = lcwm;
      this.hcwm = hcwm;
   }
   
   public boolean validate(IProposal proposal) {
      if(!(proposal != null && proposal instanceof OrderingProposal)){
         return false;
      }

      OrderingProposal mop = (OrderingProposal) proposal;

      if(mop.message == null){
         return false;
      }

      Integer viewn = mop.message.getViewNumber();

      if(!(viewn != null && viewn.compareTo(0) >= 0)){
         return false;
      }

      Long seqn = mop.message.getSequenceNumber();

      if(!(seqn != null && seqn.compareTo(0L) >= 0)){
         return false;
      }

      if(seqn.compareTo(lcwm) <= 0){
         return false;
      }

      if(seqn.compareTo(hcwm) == 1){
         return false;
      }

      DigestList digests = mop.message.getDigests();

      if(digests == null){
         return false;
      }
      
      return true;
   }
   
}
