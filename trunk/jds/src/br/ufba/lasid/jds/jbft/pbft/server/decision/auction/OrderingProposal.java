/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server.decision.auction;

import br.ufba.lasid.jds.decision.ISubject;
import br.ufba.lasid.jds.decision.bargaining.IProposal;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessageOrdering;

/**
 *
 * @author aliriosa
 */
public abstract class OrderingProposal implements IProposal{
   public static final int MESSAGE = 0;
   PBFTMessageOrdering message;

   public Object getInfo(int i) {
      if(i == MESSAGE) return this.message;
      return null;
   }

   public boolean equals(ISubject b) {
      if(!(b instanceof IProposal)){
         return false;
      }
      return compareTo((IProposal)b) == IProposal.EQUAL;
   }


}
