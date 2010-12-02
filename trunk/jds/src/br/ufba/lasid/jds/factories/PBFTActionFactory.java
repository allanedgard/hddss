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
import br.ufba.lasid.jds.comm.Message;
import br.ufba.lasid.jds.util.Wrapper;
import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.jbft.pbft.actions.BatchTimeoutAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.actions.CommitAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteCheckPointAction;
import br.ufba.lasid.jds.jbft.pbft.actions.FecthStateAction;
import br.ufba.lasid.jds.jbft.pbft.actions.PrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.PrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.RetransmissionAction;
import br.ufba.lasid.jds.jbft.pbft.actions.SendCheckPointRequestAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTActionFactory extends ActionFactory{

    public static Action create(Wrapper wapper) {
        Message m = (Message) wapper;

        PBFTMessage.TYPE type =
                (PBFTMessage.TYPE)m.get(PBFTMessage.TYPEFIELD);

        if(type.equals(PBFTMessage.TYPE.SENDREQUEST)){
            return new SendRequestAction(m);
        }

        if(type.equals(PBFTMessage.TYPE.RECEIVEREQUEST)){
            return new ReceiveRequestAction(m);
        }

        if(type.equals(PBFTMessage.TYPE.PREPREPARE)){
            return new PrePrepareAction(m);
        }

        if(type.equals(PBFTMessage.TYPE.PREPARE)){
            return new PrepareAction(m);
        }

        if(type.equals(PBFTMessage.TYPE.COMMIT)){
            return new CommitAction(m);
        }

        if(type.equals(PBFTMessage.TYPE.EXECUTE)){
            return new ExecuteRequestAction(m);
        }

        if(type.equals(PBFTMessage.TYPE.SENDREPLY)){
            return new SendReplyAction(m);
        }

        if(type.equals(PBFTMessage.TYPE.RECEIVEREPLY)){
            return new ReceiveReplyAction(m);
        }

        if(type.equals(PBFTMessage.TYPE.REQUESTRETRANSMITION)){
            return new RetransmissionAction(m);
        }

        if(type.equals(PBFTMessage.TYPE.CHANGEVIEW)){
            return new ChangeViewAction(m);
        }

        if(type.equals(PBFTMessage.TYPE.BATCHING)){
            return new BatchTimeoutAction(m);
        }

        if(type.equals(PBFTMessage.TYPE.SENDCHECKPOINTREQUEST)){
            return new SendCheckPointRequestAction(m);
        }
        if(type.equals(PBFTMessage.TYPE.FETCHSTATE)){
            return new FecthStateAction(m);
        }
        if(type.equals(PBFTMessage.TYPE.EXECUTECHECKPOINT)){
            return new ExecuteCheckPointAction(m);
        }

        return null;
    }

}
