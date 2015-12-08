/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.instances.pbft.examples.calcpbft;

import br.ufba.lasid.jds.cs.ClientServerApplicationPayload;
import br.ufba.lasid.jds.util.XObject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class CalculatorPayload extends ClientServerApplicationPayload{
    private static String TYPEFIELD = "__TYPEFIELD";

   @Override
   public int getSizeInBytes() {
      try {
         return XObject.objectToByteArray(this).length;
      } catch (IOException ex) {
         Logger.getLogger(CalculatorPayload.class.getName()).log(Level.SEVERE, null, ex);
         return -1;
      }
   }


    
}
