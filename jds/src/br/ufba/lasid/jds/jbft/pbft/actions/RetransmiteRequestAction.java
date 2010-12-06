/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.actions;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class RetransmiteRequestAction extends Action{

    public RetransmiteRequestAction(Wrapper w) {
        super(w);
    }

    public RetransmiteRequestAction() {
        super();
    }

}
