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
public class PrepareAction extends Action{

    public PrepareAction() {
    }

   public PrepareAction(Message m) {
        super(m);
    }

    public PrepareAction(Wrapper wapper) {
        super(wapper);
    }

}
