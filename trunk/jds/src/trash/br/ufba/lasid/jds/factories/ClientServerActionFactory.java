/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.factories;

import trash.br.ufba.lasid.jds.Action;
import trash.br.ufba.lasid.jds.comm.Message;
import trash.br.ufba.lasid.jds.cs.actions.CreateRequestAction;
import trash.br.ufba.lasid.jds.cs.actions.ExecuteRequestAction;
import trash.br.ufba.lasid.jds.cs.actions.ReceiveReplyAction;
import trash.br.ufba.lasid.jds.cs.actions.ReceiveRequestAction;
import trash.br.ufba.lasid.jds.cs.actions.SendReplyAction;
import trash.br.ufba.lasid.jds.cs.actions.SendRequestAction;
import trash.br.ufba.lasid.jds.cs.comm.ClientServerMessage;
import trash.br.ufba.lasid.jds.util.Wrapper;

/**
 *
 * @author aliriosa
 */
public class ClientServerActionFactory extends ActionFactory{
    
    public static Action create(Wrapper wrapper) {
//        ClientServerMessage m = (ClientServerMessage) wrapper;
        
//        ClientServerMessage.TYPE type =
//                (ClientServerMessage.TYPE)wrapper.get(ClientServerMessage.TYPEFIELD);
  /*
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
*/
       return null;

    }

}
