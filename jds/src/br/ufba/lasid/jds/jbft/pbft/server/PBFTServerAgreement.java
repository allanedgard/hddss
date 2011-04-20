/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.StatedPBFTRequestMessage.RequestState;
import br.ufba.lasid.jds.util.JDSUtility;

/**
 *
 * @author aliriosa
 */
public class PBFTServerAgreement {
   protected PBFTServer pbft;

   public PBFTServerAgreement(PBFTServer pbft) {
      this.pbft = pbft;
   }

 /*########################################################################
  # 1. Methods for handling pre-prepare messages.
  #########################################################################*/
   public void handle(PBFTPrePrepare pp){

      Object lpid = pbft.getLocalServerID();
      long    now = pbft.getClockValue();

      JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", received " + pp);

      /* If I received a invalid preprepare then I'll discard it. */
      if(!(pp != null && pp.getSequenceNumber() != null && pp.getViewNumber() != null && pp.getDigests() != null && pp.getReplicaID() != null)){
         JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", discarded " + pp + " because it's a malformed pre-prepare.");
         return;
      }

      /* If the received pre-prepare wasn't sent by a group member then I'll discard it. */
      if(!pbft.wasSentByAGroupMember(pp)){
         JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", discarded " + pp + " because it wasn't sent by group member.");
         return;
      }

      long lcwm = pbft.getLCWM();
      long hcwm = pbft.getHCWM();
      long seqn = pp.getSequenceNumber();

      /*If seqn(pp) not in (lcwm, hcwm] then pp will be discarded. */
      if(!(lcwm < seqn && seqn <= hcwm)){
         JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", discarded " + pp + " because its SEQN{" + seqn +"} not in (" + lcwm + "; " + hcwm + "].");
         return;
      }

      int itView = pp.getViewNumber();
      int myView = pbft.getCurrentViewNumber();

      /* If the pre-prepare belongs to a different view but it was sent by the primary of such view it can be used to complete a old change-view or advance in the next views */
      if(pbft.isPrimary(itView)){
         JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", isn't able to prepare " + pp + " because it belongs to a different view.");
         pbft.getPrePrepareInfo().put(pp);
      }

      /* If the pre-prepare was sent in my current view then I'll check and process it.*/
      if(itView == myView){

         Object currentPrimaryID = pbft.getCurrentPrimaryID();

         /* If the preprepare wasn't sent by the current primary replica then it'll be discarded. */
         if(!pbft.wasSentByPrimary(pp)){
            JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", discarded " + pp + " because it wasn't sent by primary server s" + currentPrimaryID);
            return;
         }

         /*If a previous pre-prepare with same sequence number was accepted */
         if(pbft.getPrePrepareInfo().count(myView, seqn) > 0){
            JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", discarded " + pp + " because it has a previous pre-prepare with same SEQN{" + seqn + "}");
            return;
         }

         pbft.getPrePrepareInfo().put(new PBFTPrePrepare(pp));

         for(String digest : pp.getDigests()){

            if(!pbft.getRequestInfo().hasRequest(digest)){
               pbft.getRequestInfo().add(digest, null, RequestState.PREPREPARED);
            }else{
               pbft.getRequestInfo().assign(digest, RequestState.PREPREPARED);
            }

            pbft.getRequestInfo().assign(digest, seqn);
         }

         /*If the pre-prepare has some request that I hasn't been received yet then I won't be able to send the prepare. */
         if(pbft.getRequestInfo().hasSomeRequestMissed(seqn)){
            JDSUtility.debug("[handle(preprepare)] s" + lpid + ", at time " + now + ", isn't able to prepare " + pp + " because it has missed requests.");
            return;
         }

         pbft.getStateLog().updateNextPrePrepareSEQ(pp);
         if(!pbft.isPrimary()){
            pbft.emitPrepare(pp);
         }
         return;
      }
      
   }//end handle(pp)
   
}
