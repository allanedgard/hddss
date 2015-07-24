/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss;

import br.ufba.lasid.jds.util.XObject;

/**
 *
 * @author aliriosa
 */
public class CPULoadAware extends CPU{

   @Override
   public double proc(Object data) {
      
      double delay = 0;
      
      try{
         int dsize = XObject.objectToByteArray(data).length * 8;
         delay = ((double)dsize / (double)getProcessingRate());
      }catch(Exception e){
         
      }

      return delay;
   }

}
