/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.jbft.pbft.actions;

import trash.br.ufba.lasid.jds.Action;
import trash.br.ufba.lasid.jds.comm.Message;
import trash.br.ufba.lasid.jds.util.Wrapper;

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