/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.executors.ClientServerServerExecuteExecutor;

/**
 *
 * @author aliriosa
 */
public class PBFTServerExecuteExecutor extends ClientServerServerExecuteExecutor{

    public PBFTServerExecuteExecutor(Protocol protocol) {
        super(protocol);
    }

    
}
