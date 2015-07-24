/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTMessageOrderingInfo extends PBFTServerMessageInfo{

   public PBFTMessageOrderingInfo(int minimum) {
      super(minimum);
   }

   public void put(PBFTMessageOrdering m) {
      if(m != null && m.getSequenceNumber() != null && m.getViewNumber() != null){
         long seqn = m.getSequenceNumber();
         super.put(seqn, m);
      }
   }

}
