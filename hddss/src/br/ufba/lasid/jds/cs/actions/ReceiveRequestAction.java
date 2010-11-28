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
public class ReceiveRequestAction extends Action{

    public ReceiveRequestAction() {
    }

    public ReceiveRequestAction(Message m) {
        super(m);
    }

}
