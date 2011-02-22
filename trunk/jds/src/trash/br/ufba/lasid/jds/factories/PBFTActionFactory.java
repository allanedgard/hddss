/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.factories;

import trash.br.ufba.lasid.jds.cs.actions.SendRequestAction;
import trash.br.ufba.lasid.jds.cs.actions.SendReplyAction;
import trash.br.ufba.lasid.jds.cs.actions.ExecuteRequestAction;
import trash.br.ufba.lasid.jds.cs.actions.ReceiveRequestAction;
import trash.br.ufba.lasid.jds.cs.actions.ReceiveReplyAction;
import trash.br.ufba.lasid.jds.util.Wrapper;
import trash.br.ufba.lasid.jds.Action;
import trash.br.ufba.lasid.jds.cs.actions.CreateRequestAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.BatchTimeoutAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.ChangeViewAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.ExecuteCheckPointAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.FetchStateAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.ReceiveChangeViewAckAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.ReceiveChangeViewAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.ReceiveCheckpointAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.ReceiveCommitAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.ReceiveNewViewAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.ReceivePrePrepareAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.ReceivePrepareAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.RetransmiteRequestAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.SendCheckpointAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.SendCommitAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.SendPrePrepareAction;
import trash.br.ufba.lasid.jds.jbft.pbft.actions.SendPrepareAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTActionFactory extends ActionFactory{

    public static Action create(Wrapper wapper) {
//        Message m = (Message) wapper;

//        PBFTMessage.TYPE type =
//                (PBFTMessage.TYPE)wapper.get(PBFTMessage.TYPEFIELD);
/*
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
            return new FetchStateAction(wapper);
        }
        if(type.equals(PBFTMessage.TYPE.EXECUTECHECKPOINT)){
            return new ExecuteCheckPointAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.RECEIVECHECKPOINT)){
            return new ReceiveCheckpointAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.RECEIVECHANGEVIEW)){
            return new ReceiveChangeViewAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.RECEIVECHANGEVIEWACK)){
            return new ReceiveChangeViewAckAction(wapper);
        }

        if(type.equals(PBFTMessage.TYPE.RECEIVENEWVIEW)){
            return new ReceiveNewViewAction(wapper);
        }
*/
        return null;
    }

}
