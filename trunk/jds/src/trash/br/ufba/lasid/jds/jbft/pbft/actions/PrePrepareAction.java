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
public class PrePrepareAction extends Action{

    public PrePrepareAction() {
    }

    public PrePrepareAction(Message m) {
        super(m);
    }

    public PrePrepareAction(Wrapper wapper) {
        super(wapper);
    }

}
