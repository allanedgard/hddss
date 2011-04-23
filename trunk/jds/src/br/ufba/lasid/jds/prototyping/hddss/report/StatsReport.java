/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.report;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author aliriosa
 */
public class StatsReport extends Report<DescriptiveStatistics, Double>{

   public StatsReport(int id, String name) {
      super(id, name);
      data = new DescriptiveStatistics();
   }

   @Override
   public void insert() {
      //do nothing
   }

   @Override
   public void insert(Double info) {
      data.addValue(info);
   }


}
