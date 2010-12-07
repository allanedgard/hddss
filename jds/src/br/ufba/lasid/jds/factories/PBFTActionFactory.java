/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.factories;

import br.ufba.lasid.jds.cs.actions.SendRequestAction;
import br.ufba.lasid.jds.cs.actions.SendReplyAction;
import br.ufba.lasid.jds.cs.actions.ExecuteRequestAction;
import br.ufba.lasid.jds.cs.actions.ReceiveRequestAction;
import br.ufba.lasid.jds.cs.actions.ReceiveReplyAction;
import br.ufba.lasid.jds.util.Wrapper;
import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.cs.actions.CreateRequestAction;
import br.ufba.lasid.jds.jbft.pbft.actions.BatchTimeoutAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteCheckPointAction;
import br.ufba.lasid.jds.jbft.pbft.actions.FecthStateAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ReceiveCheckpointAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ReceiveCommitAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ReceivePrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ReceivePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.RetransmiteRequestAction;
import br.ufba.lasid.jds.jbft.pbft.actions.SendCheckpointAction;
import br.ufba.lasid.jds.jbft.pbft.actions.SendCommitAction;
import br.ufba.lasid.jds.jbft.pbft.actions.SendPrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.SendPrepareAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTActionFactory extends ActionFactory{

    public static Action create(Wrapper wapper) {
//        Message m = (Message) wapper;

        PBFTMessage.TYPE type =
                (PBFTMessage.TYPE)wapper.get(PBFTMessage.TYPEFIELD);

        if(type == null){
            return new CreateRequestAction(wapper);
        }
        
        if(type.equals(PBFTMessage.TYPE.SENDREQUEST)){
            return new SendRequestAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.RECEIVEREQUEST)){
            return new ReceiveRequestAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.SENDPREPREPARE)){
            return new SendPrePrepareAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.RECEIVEPREPREPARE)){
            return new ReceivePrePrepareAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.SENDPREPARE)){
            return new SendPrepareAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.RECEIVEPREPARE)){
            return new ReceivePrepareAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.SENDCOMMIT)){
            return new SendCommitAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.RECEIVECOMMIT)){
            return new ReceiveCommitAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.EXECUTE)){
            return new ExecuteRequestAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.SENDREPLY)){
            return new SendReplyAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.RECEIVEREPLY)){
            return new ReceiveReplyAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.REQUESTRETRANSMITION)){
            return new RetransmiteRequestAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.CHANGEVIEW)){
            return new ChangeViewAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.BATCHING)){
            return new BatchTimeoutAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.SENDCHECKPOINT)){
            return new SendCheckpointAction(wapper);
        }
        if(type.equals(PBFTMessage.TYPE.FETCHSTATE)){
            return new FecthStateAction(wapper);
        }
        if(type.equals(PBFTMessage.TYPE.EXECUTECHECKPOINT)){
            return new ExecuteCheckPointAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.RECEIVECHECKPOINT)){
            return new ReceiveCheckpointAction(wapper);
        }


        return null;
    }

}
