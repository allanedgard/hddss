/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.report;

/**
 *
 * @author aliriosa
 */
public abstract class Report<Data, Info> {
   String name;
   Data data;
   int id;

   public Report(int id, String name) {
      this.id = id;
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }


   public Data getResult(){
      return data;
   }

   public abstract void insert();
   public abstract void insert(Info info);
}
