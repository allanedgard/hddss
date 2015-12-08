/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.instances.pbft;

import br.ufba.lasid.jds.jbft.pbft.IPBFT;
import br.ufba.lasid.jds.prototyping.hddss.IAgent;

/**
 *
 * @author aliriosa
 */
public interface IPBFTAgent extends IAgent{
    public IPBFT getProtocol();
    public void setProtocol(IPBFT pbft);
}
