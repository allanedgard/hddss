/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.actions;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.comm.Message;

/**
 *
 * @author aliriosa
 */
public class CommitAction extends Action{

    public CommitAction() {
    }

    public CommitAction(Message m) {
        super(m);
    }

}