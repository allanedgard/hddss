/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.report;

/**
 *
 * @author aliriosa
 */
public class ValuedReport extends Report<Double, Double>{
   
   public ValuedReport(int id, String name) {
      super(id, name);
      data = 0.0;
   }

   @Override
   public void insert() {
      //throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void insert(Double info) {
      data = info;
   }



}
