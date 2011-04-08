/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;

/**
 *
 * @author aliriosa
 */
public class PBFTProcessingToken extends PBFTServerMessage{

    public PBFTProcessingToken(Integer viewNumber, Long sequenceNumber){
        setViewNumber(viewNumber);
        setSequenceNumber(sequenceNumber);
    }

    @Override
    public final String toString() {

        return (
                "<PROC-TOKEN" + ", " +
                 "VIEW = " + getViewNumber().toString() + ", " +
                 "SEQUENCE = " + getSequenceNumber().toString() +
                 ">"
        );
    }

   public int getTAG() {
      return IPBFTServer.TOKEN;
   }

   public String getTAGString() {
      return "TOKEN";
   }
}
