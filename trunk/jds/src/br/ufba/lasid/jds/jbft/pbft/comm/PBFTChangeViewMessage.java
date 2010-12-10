/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTChangeViewMessage extends PBFTMessage{

    @Override
    public String getID() {
        String view = get(PBFTMessage.VIEWFIELD).toString();
        String rep  = get(PBFTMessage.REPLICAIDFIELD).toString();
        String chkpn = get(PBFTMessage.CHECKPOINTNUMBERFIELD).toString();

        return view + "." + rep + "." + chkpn;

    }

}
