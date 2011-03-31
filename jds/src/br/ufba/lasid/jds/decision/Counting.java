/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.decision;

import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class Counting extends Hashtable<ISubject, Long> implements ICounting{

   public Long get(ISubject subject) {
      return super.get(subject);
   }

}
