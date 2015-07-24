/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision.auction;

import br.ufba.lasid.jds.decision.bargaining.IProposal;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;

/**
 *
 * @author aliriosa
 */
public class PrePrepareProposal extends OrderingProposal{

   public PrePrepareProposal(PBFTPrePrepare message) {
      this.message = message;
   }

   /**
    * Compare this proposal with other.
    * @param -- the other proposal.
    * @return -- -1, 0, 1, 2 if this proposal is lower, equals, bigger or different to other.
    */
   public int compareTo(IProposal o) {
      if(!(o != null && o instanceof OrderingProposal)){
         return DIFFERENT;
      }

      OrderingProposal other = (OrderingProposal) o;

      int mview = message.getViewNumber();
      int oview = other.message.getViewNumber();

      if(mview == oview && message.getDigests().equals(other.message.getDigests())){
         return EQUAL;
      }

      if(mview < oview && message.getDigests().equals(other.message.getDigests())){
         return LOWER;
      }

      if(mview > oview && message.getDigests().equals(other.message.getDigests())){
         return BIGGER;
      }

      return DIFFERENT;
   }

}
