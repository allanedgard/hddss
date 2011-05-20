/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.examples.calcpbft;

import br.ufba.lasid.jds.jbft.pbft.server.AutonomicPBFTServer;

/**
 *
 * @author aliriosa
 */
public class CalcAutonomicPBFTServer extends CalcPBFTServer{

   @Override
   protected void setProtocol() {
      setProtocol(new AutonomicPBFTServer());
   }


}
