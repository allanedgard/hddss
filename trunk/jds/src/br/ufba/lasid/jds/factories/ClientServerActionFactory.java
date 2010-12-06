/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.factories;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.cs.actions.CreateRequestAction;
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
    
    public static Action create(Wrapper wrapper) {
//        ClientServerMessage m = (ClientServerMessage) wrapper;
        
        ClientServerMessage.TYPE type =
                (ClientServerMessage.TYPE)wrapper.get(ClientServerMessage.TYPEFIELD);
        
        if(type == null){
            return new CreateRequestAction(wrapper);
        }

        if(type.equals(ClientServerMessage.TYPE.SENDREQUEST)){
            return new SendRequestAction(wrapper);
        }

        if(type.equals(ClientServerMessage.TYPE.RECEIVEREQUEST)){
            return new ReceiveRequestAction(wrapper);
        }

        if(type.equals(ClientServerMessage.TYPE.EXECUTE)){
            return new ExecuteRequestAction(wrapper);
        }

        if(type.equals(ClientServerMessage.TYPE.SENDREPLY)){
            return new SendReplyAction(wrapper);
        }

        if(type.equals(ClientServerMessage.TYPE.RECEIVEREPLY)){
            return new ReceiveReplyAction(wrapper);
        }

       return null;

    }

}
