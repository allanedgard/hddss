/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.bargaining.auction;

import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.bargaining.IProposal;
import br.ufba.lasid.jds.decision.bargaining.IProposalValidator;
import br.ufba.lasid.jds.decision.bargaining.ProposalList;

/**
 *
 * @author aliriosa
 */
public class SoftAuction implements IAuction{

   Bidtable bidtable = new Bidtable();
   IProposalValidator validator;
   int compstrategy = IProposal.BIGGER;

   public SoftAuction(IProposalValidator validator, int compstrategy) {
      this.validator = validator;
      this.compstrategy = compstrategy;
   }
      

   public void addProposal(ILot lot, IProposal proposal) {
      if(validator.validate(proposal)){
         ProposalList proposals = bidtable.get(lot);
         if(proposals == null){
            proposals = new ProposalList();
            bidtable.put(lot, proposals);
         }
         if(!proposals.contains(proposal)){
            proposals.add(proposal);
         }
      }//end has a valid proposal
   }

   public ISubject decide() {

      AuctionResult result = new AuctionResult();

      /* for each lot in auction */
      for(ILot lot : bidtable.keySet()){
         
         ProposalList proposals = bidtable.get(lot);
         ProposalList differents = new ProposalList();
         
         /* each proposal for the current lot */
         for(IProposal proposal : proposals){

            boolean nocontain = true;
            
            /*for each different kinds of proposal */
            for(IProposal different : differents){
               nocontain = nocontain && proposal.compareTo(different) == IProposal.DIFFERENT;
            }

            /*if there is a kind of proposal which hasn't included yet then it'll be included*/
            if(nocontain){
               differents.add(proposal);
            }
         }

         /* for each different kinds of proposal */
         for(IProposal different : differents){
            /* init the winner */
            IProposal winner = different;
            int count = 0;
            /* for each proposal for the current lot */
            for(IProposal proposal : proposals){
               /* compare the new proposal with the current winner proposal */
               int cmp = winner.compareTo(proposal);

               /*if winner and the new proposal are similar */
               if(cmp != IProposal.DIFFERENT){
                  /* increase the number of the proposal for the current proposal kind */
                  count ++;

                 /* if the new proposal is bigger than the current winner then it'll be the current winner */
                  if(cmp == compstrategy){
                     winner = proposal;
                  }
               }
            }

            /* put the winner proposal of the current kind of proposal for this lot in the results, taking into account the
               number of proposal that was offered */
            result.put(lot, winner, count);
         }
      }
      
      /* return the results */
      return result;
   }

}
