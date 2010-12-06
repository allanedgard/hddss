/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTBatchMessage extends PBFTRequestMessage{

    @Override
    public String getID() {
        String client =
            ((br.ufba.lasid.jds.Process)get(PBFTMessage.CLIENTFIELD)).getID().toString();

        String timestamp = ((Long)get(PBFTMessage.TIMESTAMPFIELD)).toString();

        return client + "." + timestamp;
    }



}
