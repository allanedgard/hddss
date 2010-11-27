/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft.pbft.actions;

import br.ufba.lasid.util.Message;
import br.ufba.lasid.util.Wrapper;
import br.ufba.lasid.util.actions.Action;
import br.ufba.lasid.util.actions.ActionFactory;
import br.ufba.lasid.jbft.pbft.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTActionFactory extends ActionFactory{

    public static Action create(Wrapper wapper) {
        Message m = (Message) wapper;

        if(m.getType() == PBFTMessage.TYPE.SENDREQUEST.getValue()){
            return new SendRequestAction(m);
        }

        if(m.getType() == PBFTMessage.TYPE.RECEIVEREQUEST.getValue()){
            return new ReceiveRequestAction(m);
        }

        if(m.getType() == PBFTMessage.TYPE.PREPREPARE.getValue()){
            return new PrePrepareAction(m);
        }

        if(m.getType() == PBFTMessage.TYPE.PREPARE.getValue()){
            return new PrepareAction(m);
        }

        if(m.getType() == PBFTMessage.TYPE.COMMIT.getValue()){
            return new CommitAction(m);
        }

        if(m.getType() == PBFTMessage.TYPE.EXECUTE.getValue()){
            return new ExecuteAction(m);
        }

        if(m.getType() == PBFTMessage.TYPE.SENDREPLY.getValue()){
            return new SendReplyAction(m);
        }

        if(m.getType() == PBFTMessage.TYPE.RECEIVEREPLY.getValue()){
            return new ReceiveReplyAction(m);
        }

        return null;
    }

}
