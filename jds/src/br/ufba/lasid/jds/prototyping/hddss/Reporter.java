/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss;

import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class Reporter {
   ReporterDB db = new ReporterDB();

   public enum RESULTTYPE{
      STATS, COUNTING;
   }
   
   public void newTable(String tbname){
   }

   class ReporterDB extends Hashtable<String, ReporterTB>{
      
   }

   class ReporterTB extends Hashtable<Object, Object>{
      
   }
}
