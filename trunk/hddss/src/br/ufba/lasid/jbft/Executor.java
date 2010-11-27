/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft;

import br.ufba.lasid.jbft.actions.Action;

/**
 *
 * @author aliriosa
 */
public class Executor {
    protected Protocol protocol;

    public Executor(Protocol protocol){
        this.protocol = protocol;
    }
    public void execute(Action act) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
