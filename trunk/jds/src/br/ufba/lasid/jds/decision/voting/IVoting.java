/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.voting;

import br.ufba.lasid.jds.decision.IDecisionStrategy;

/**
 *
 * @author aliriosa
 */
public interface IVoting extends IDecisionStrategy {
   public void add(IVote vote);
   public void count();
   public VoteList getVotes();
   public ICounting getCounting();
}
