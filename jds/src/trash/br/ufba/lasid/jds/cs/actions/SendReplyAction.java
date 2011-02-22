/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.cs.actions;

import trash.br.ufba.lasid.jds.Action;
import trash.br.ufba.lasid.jds.comm.Message;
import trash.br.ufba.lasid.jds.util.Wrapper;

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

    public SendReplyAction(Wrapper wapper) {
        super(wapper);
    }

}
