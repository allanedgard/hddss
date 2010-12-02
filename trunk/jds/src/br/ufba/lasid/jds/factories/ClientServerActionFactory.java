/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.factories;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.cs.actions.ExecuteRequestAction;
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
        ClientServerMessage m = (ClientServerMessage) wapper;
        
        ClientServerMessage.TYPE type =
                (ClientServerMessage.TYPE)m.get(ClientServerMessage.TYPEFIELD);
        
        if(type.equals(ClientServerMessage.TYPE.SENDREQUEST)){
            return new SendRequestAction(m);
        }

        if(type.equals(ClientServerMessage.TYPE.RECEIVEREQUEST)){
            return new ReceiveRequestAction(m);
        }

        if(type.equals(ClientServerMessage.TYPE.EXECUTE)){
            return new ExecuteRequestAction(m);
        }

        if(type.equals(ClientServerMessage.TYPE.SENDREPLY)){
            return new SendReplyAction(m);
        }

        if(type.equals(ClientServerMessage.TYPE.RECEIVEREPLY)){
            return new ReceiveReplyAction(m);
        }

        return null;
    }

}
