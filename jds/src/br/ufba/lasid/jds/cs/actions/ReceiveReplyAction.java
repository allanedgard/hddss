/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs.actions;

import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class ReceiveReplyAction extends Action{

    public ReceiveReplyAction(Message m) {
        super(m);
    }

    public ReceiveReplyAction(Wrapper wapper) {
        super(wapper);
    }

}
