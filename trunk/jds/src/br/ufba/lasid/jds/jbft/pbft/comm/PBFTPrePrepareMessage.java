/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTPrePrepareMessage extends PBFTMessage{

    @Override
    public String getID() {
        String view = get(PBFTMessage.VIEWFIELD).toString();
        String seqn = get(PBFTMessage.SEQUENCENUMBERFIELD).toString();
        String dgst = get(PBFTMessage.DIGESTFIELD).toString();
        String repl = get(PBFTMessage.REPLICAIDFIELD).toString();

        return view + "." + seqn + "." + dgst + "." +repl;
    }



}
