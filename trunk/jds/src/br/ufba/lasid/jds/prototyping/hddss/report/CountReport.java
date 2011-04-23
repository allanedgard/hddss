/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.report;

/**
 *
 * @author aliriosa
 */
public class CountReport extends Report<Integer, Object>{

   public CountReport(int id, String name) {
      super(id, name);
      data = 0;
   }

   @Override
   public void insert() {
      data++;
   }

   @Override
   public void insert(Object info) {
      data++;
   }

}
