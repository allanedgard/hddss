/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.voting;

import br.ufba.lasid.jds.decision.ISubject;

/**
 *
 * @author aliriosa
 */
public interface IVote {
   public ISubject getSubject();
   public IElector getElector();
}
