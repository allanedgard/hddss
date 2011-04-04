/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.voting;

import br.ufba.lasid.jds.decision.ISubject;
import java.util.Set;

/**
 *
 * @author aliriosa
 */
public interface ICounting {
      public Long get(ISubject subject);
      public void count(VoteList votes);
      public Set<ISubject> getSubjects();
}
