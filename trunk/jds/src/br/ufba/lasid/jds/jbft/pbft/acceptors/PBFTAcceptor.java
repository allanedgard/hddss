/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.acceptors;

import br.ufba.lasid.jds.jbft.pbft.PBFT;

/**
 *
 * @author aliriosa
 */
public abstract class PBFTAcceptor<T> implements IAcceptor<T> {

    protected PBFT protocol;

    public PBFTAcceptor(PBFT protocol) {
        this.protocol = protocol;
    }

    public PBFT getProtocol() {
        return protocol;
    }

    public void setProtocol(PBFT protocol) {
        this.protocol = protocol;
    }
    
    
}
