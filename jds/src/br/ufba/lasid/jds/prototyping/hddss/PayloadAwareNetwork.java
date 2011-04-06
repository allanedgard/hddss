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
public class PayloadAwareNetwork extends Network{
    double transferrate = 0;
    double scost = 0;
    int mtu;
    public void setTransferRate(String v){
        transferrate = Double.parseDouble(v);
    }

    public void setMTU(String v){
        mtu = Integer.parseInt(v);
    }

    public void setSegmentationCost(String v){
        scost = Double.parseDouble(v);
    }

   @Override
   double delay(Message m) {
      int mbits = 0;
      double delay = 0;

      if(transferrate > 0){
         try{
            mbits = XObject.objectToByteArray(m.content).length * 8;
            int nsegments = (int)Math.ceil((double)mbits / (double)mtu);
            delay = (double)mbits / (double)transferrate + ((double)nsegments -  1.0) * (double)scost;
         }catch(Exception e){

         }
      }
      return delay;
   }

}
