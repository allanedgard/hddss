/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft.pbft.actions;

import br.ufba.lasid.util.actions.Action;
import br.ufba.lasid.util.Message;

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
