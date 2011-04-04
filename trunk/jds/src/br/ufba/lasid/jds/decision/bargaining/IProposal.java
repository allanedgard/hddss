/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision.bargaining;

import br.ufba.lasid.jds.decision.ISubject;

/**
 *
 * @author aliriosa
 */
public interface IProposal extends ISubject, Comparable<IProposal>{

   public static final int     LOWER = -1;
   public static final int     EQUAL =  0;
   public static final int    BIGGER = 1;
   public static final int DIFFERENT = 2;

}
