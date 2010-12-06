/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.actions;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class PrePrepareAction extends Action{

    public PrePrepareAction() {
    }

    public PrePrepareAction(Message m) {
        super(m);
    }

    public PrePrepareAction(Wrapper wapper) {
        super(wapper);
    }

}
