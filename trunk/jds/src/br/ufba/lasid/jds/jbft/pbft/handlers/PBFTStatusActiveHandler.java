/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.handlers;

import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;

/**
 *
 * @author aliriosa
 */
class PBFTStatusActiveHandler extends PBFTServerMessageHandler{

    public PBFTStatusActiveHandler(PBFTServer protocol) {
        super(protocol);
    }

    public void handle() {
        
        PBFTStatusActive sa = (PBFTStatusActive) this.input;

        if(getProtocol().canProceed(sa)){
            
        }
    }
}
