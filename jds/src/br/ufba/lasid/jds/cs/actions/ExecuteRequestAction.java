/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs.actions;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class ExecuteRequestAction extends Action{

    public ExecuteRequestAction() {
    }

    public ExecuteRequestAction(Message m) {
        super(m);
    }

    public ExecuteRequestAction(Wrapper wapper) {
        super(wapper);
    }

}
