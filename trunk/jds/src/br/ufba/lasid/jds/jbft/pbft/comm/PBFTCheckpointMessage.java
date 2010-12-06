/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTCheckpointMessage extends PBFTMessage{

    @Override
    public String getID() {

        String seq = get(PBFTMessage.SEQUENCENUMBERFIELD).toString();
        String dig = get(PBFTMessage.DIGESTFIELD).toString();
        String rep = get(PBFTMessage.REPLICAIDFIELD).toString();

        return (seq + "." + dig + "." + rep);

    }



}
