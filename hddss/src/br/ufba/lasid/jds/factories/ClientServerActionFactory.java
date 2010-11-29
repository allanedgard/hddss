/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.factories;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.cs.actions.ExecuteAction;
import br.ufba.lasid.jds.cs.actions.ReceiveReplyAction;
import br.ufba.lasid.jds.cs.actions.ReceiveRequestAction;
import br.ufba.lasid.jds.cs.actions.SendReplyAction;
import br.ufba.lasid.jds.cs.actions.SendRequestAction;
import br.ufba.lasid.jds.cs.comm.ClientServerMessage;
import br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class ClientServerActionFactory extends ActionFactory{
    
    public static Action create(Wrapper wapper) {
        Message m = (Message) wapper;

        if(m.getType() == ClientServerMessage.TYPE.SENDREQUEST.getValue()){
            return new SendRequestAction(m);
        }

        if(m.getType() == ClientServerMessage.TYPE.RECEIVEREQUEST.getValue()){
            return new ReceiveRequestAction(m);
        }

        if(m.getType() == ClientServerMessage.TYPE.EXECUTE.getValue()){
            return new ExecuteAction(m);
        }

        if(m.getType() == ClientServerMessage.TYPE.SENDREPLY.getValue()){
            return new SendReplyAction(m);
        }

        if(m.getType() == ClientServerMessage.TYPE.RECEIVEREPLY.getValue()){
            return new ReceiveReplyAction(m);
        }

        return null;
    }

}
