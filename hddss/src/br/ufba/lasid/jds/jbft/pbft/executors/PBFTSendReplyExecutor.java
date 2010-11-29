/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.executors.ClientServerSendReplyExecutor;

/**
 *
 * @author aliriosa
 */
public class PBFTSendReplyExecutor extends ClientServerSendReplyExecutor{

    public PBFTSendReplyExecutor(Protocol protocol) {
        super(protocol);
    }


}
