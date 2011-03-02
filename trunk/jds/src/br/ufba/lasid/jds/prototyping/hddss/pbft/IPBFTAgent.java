/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.prototyping.hddss.IAgent;

/**
 *
 * @author aliriosa
 */
public interface IPBFTAgent extends IAgent{
    public PBFT getProtocol();
    public void setProtocol(PBFT pbft);
}
