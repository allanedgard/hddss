/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.bargaining.auction;

import br.ufba.lasid.jds.decision.bargaining.IProposal;

/**
 *
 * @author aliriosa
 */
public interface ILotResult {

   public boolean isEmpty();

   public int size();

   public void remove(int i);

   public int indexof(IProposal proposal);
   public void add(IProposal proposal, int count);

   public IProposal getProposal(int i);

   public int getCount(int i);

}
