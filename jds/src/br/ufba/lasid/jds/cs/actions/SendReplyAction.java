/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.cs.actions;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.comm.Message;

/**
 *
 * @author aliriosa
 */
public class SendReplyAction extends Action{

    public SendReplyAction() {
    }

    public SendReplyAction(Message m) {
        super(m);
    }

}
