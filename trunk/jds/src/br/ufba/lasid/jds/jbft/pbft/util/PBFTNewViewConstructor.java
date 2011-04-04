/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.MessageCollection;
import br.ufba.lasid.jds.decision.bargaining.IProposal;
import br.ufba.lasid.jds.decision.bargaining.auction.AuctionResult;
import br.ufba.lasid.jds.decision.bargaining.auction.IAuctionResult;
import br.ufba.lasid.jds.decision.bargaining.auction.ILotResult;
import br.ufba.lasid.jds.decision.bargaining.auction.LotList;
import br.ufba.lasid.jds.decision.voting.Quorumtable;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTNewView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.server.decision.auction.PrepareProposal;
import br.ufba.lasid.jds.jbft.pbft.server.decision.auction.NewViewAuction;
import br.ufba.lasid.jds.jbft.pbft.server.decision.auction.OrderingProposal;
import br.ufba.lasid.jds.jbft.pbft.server.decision.auction.PrePrepareProposal;
import br.ufba.lasid.jds.jbft.pbft.server.decision.auction.Sequence;
import br.ufba.lasid.jds.jbft.pbft.server.decision.auction.SequenceList;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class PBFTNewViewConstructor {

   protected Hashtable<String, PBFTChangeView> cvtable = new Hashtable<String, PBFTChangeView>();
   protected Quorumtable<Long> qtable = new Quorumtable<Long>();
   protected IPBFTServer pbft;

   public PBFTNewViewConstructor(IPBFTServer pbft) {
      this.pbft = pbft;
   }


   public void addChangeView(String digest, PBFTChangeView cv){
      if(cv!= null && digest != null && !cvtable.containsKey(digest)){
         cvtable.put(digest, cv);
      }
   }

   protected long lcwm = -1;
   protected long hcwm = -1;
   protected long checkpointPeriod = 0;
   protected long checkpointFactor = 0;
   protected int resilience = 0;
   
   public void findCheckpointWaterMarks(){

       for(PBFTChangeView cv : cvtable.values()){
           MessageCollection checkpoints  = cv.getCheckpointSet();
           if(checkpoints != null && checkpoints.isEmpty()){
              for(IMessage m : checkpoints){
                 PBFTCheckpoint checkpoint = (PBFTCheckpoint) m;
                 if(checkpoint != null && checkpoint.getSequenceNumber() != null){
                     long seqn = checkpoint.getSequenceNumber();
                     if(seqn > lcwm){
                        lcwm = seqn;
                     }//end if checkpoint.sequenceNumber > lcwm
                 }//end if is a valid checkpoint message
              }//end for each checkpoint message
           }//end if is a valid checkpoint set
       }//end for each change-view message

       hcwm = lcwm + checkpointFactor * checkpointPeriod;
   }

   public PBFTNewView buildNewView(){

      findCheckpointWaterMarks();
      
      long minSEQ = Long.MAX_VALUE;
      long maxSEQ = -1;

      PBFTNewView nv = new PBFTNewView();

      NewViewAuction pauction = new NewViewAuction(lcwm, hcwm, IProposal.BIGGER);
      NewViewAuction qauction = new NewViewAuction(lcwm, hcwm, IProposal.LOWER);

      SequenceList sequences = new SequenceList();

      /* for each certificated change-view message */
      for(String cvkey : cvtable.keySet()){

         PBFTChangeView cv = cvtable.get(cvkey);

         /* for each message in prepare set (P)*/
         for(IMessage m : cv.getPrepareSet()){

            /* get the prepare message */
            PBFTPrepare pr = (PBFTPrepare) m;
            
            /* the prepare sequence number */
            long seqn = pr.getSequenceNumber();

           /* put the sequence as lot in the auction for request ordering */
            pauction.addProposal(seqn, new PrepareProposal(pr));

            if(!sequences.contains(seqn)) sequences.add(seqn);

            if(minSEQ > seqn) minSEQ = seqn;
            if(maxSEQ < seqn) maxSEQ = seqn;

         }

         /* for each message in pre-prepare set (Q) */
         for(IMessage m : cv.getPrePrepareSet()){
            /* get the pre-prepare message */
            PBFTPrePrepare pp = (PBFTPrePrepare) m;

            /* the pre-prepare sequence number */
            long seqn = pp.getSequenceNumber();

           /* put the sequence as lot in the auction for request ordering */            
            qauction.addProposal(seqn, new PrePrepareProposal(pp));

            if(!sequences.contains(seqn)) sequences.add(seqn);

            if(minSEQ > seqn) minSEQ = seqn;
            if(maxSEQ < seqn) maxSEQ = seqn;

         }

         nv.getChangeViewTable().put(cvkey, cv);
      }

      /* process auction and get results */
      IAuctionResult presult = (IAuctionResult) pauction.decide();
      IAuctionResult qresult = (IAuctionResult) qauction.decide();

      /* we shouldn't execute this test but if these conditions was true then something bad probabily happened */
      if(minSEQ <= lcwm    ) minSEQ = lcwm + 1;
      if(minSEQ >  lcwm + 1) minSEQ = lcwm + 1;
      if(maxSEQ >  hcwm    ) maxSEQ = hcwm + 0;

      int f = pbft.getServiceBFTResilience();
      
      nv.setReplicaID(pbft.getLocalServerID());

      for(long seqn = minSEQ; seqn <= maxSEQ; seqn ++){
         PBFTPrePrepare pp = new PBFTPrePrepare(pbft.getCurrentViewNumber(), seqn, pbft.getLocalServerID());
         if(sequences.contains(seqn)){

            Sequence sequence = sequences.get(seqn);
            ILotResult lpresult = presult.get(sequence);

            for(int p = 0; p < lpresult.size(); p++){

               int pcount = lpresult.getCount(p);
               IProposal pproposal = lpresult.getProposal(p);

               if(pcount >= 2 * f + 1){

                  ILotResult lqresult = qresult.get(sequence);

                  for(int q = 0; q < lqresult.size(); q ++){
                     int qcount = lqresult.getCount(q);
                     IProposal qproposal = lqresult.getProposal(q);

                     if(qcount >= f + 1){
                        
                        if(pproposal.equals(qresult)){
                           PBFTPrePrepare pp1 = (PBFTPrePrepare) qproposal.getInfo(OrderingProposal.MESSAGE);
                           if(pp1 != null){
                              pp.getDigests().addAll(pp1.getDigests());
                           }//end if is valid pre-prepare
                        }//end if is the same proposal
                     }//end if has f + 1
                  }//end for each q auction result
               }//end if has 2 * f + 1
            }//end for each p auction result
         }//end contains seqn
         nv.getPreprepareSet().add(pp);
      }//end for each seqn

      nv.setViewNumber(pbft.getCurrentViewNumber());

      return nv;
   }
}
