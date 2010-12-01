/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.cs.actions.ExecuteAction;
import br.ufba.lasid.jds.cs.actions.ReceiveRequestAction;
import br.ufba.lasid.jds.cs.actions.SendReplyAction;
import br.ufba.lasid.jds.cs.executors.ClientServerSendReplyExecutor;
import br.ufba.lasid.jds.cs.executors.ClientServerServerExecuteExecutor;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.actions.BatchTimeoutAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.actions.CommitAction;
import br.ufba.lasid.jds.jbft.pbft.actions.PrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.PrepareAction;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTChangeViewExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTCommitExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTPrePrepareExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTPrepareExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTReceiveRequestExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTSendReplyExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTServerExecuteExecutor;


/**
 *
 * @author aliriosa
 */
public class Agent_ServerPBFT extends Agent_PBFT implements PBFTServer<Integer>{


    @Override
    public void setup() {
        super.setup();
        Executor rre = newPBFTReceiveRequestExecutor();
        getProtocol().addExecutor(ReceiveRequestAction.class, rre);
        getProtocol().addExecutor(BatchTimeoutAction.class, rre);
        getProtocol().addExecutor(PrePrepareAction.class, newPBFTPrePrepareExecutor());
        getProtocol().addExecutor(PrepareAction.class, newPBFTPrepareExecutor());
        getProtocol().addExecutor(CommitAction.class, newPBFTCommitExecutor());
        getProtocol().addExecutor(ExecuteAction.class, newPBFTServerExecuteExecutor());
        getProtocol().addExecutor(SendReplyAction.class, newPBFTSendReplyExecutor());
        getProtocol().addExecutor(ChangeViewAction.class, newPBFTChangeViewExecutor());

    }

    public Executor newPBFTReceiveRequestExecutor(){
        return new PBFTReceiveRequestExecutor(getProtocol());
    }
    public Executor newPBFTPrePrepareExecutor(){
        return new PBFTPrePrepareExecutor(getProtocol());
    }
    public Executor newPBFTPrepareExecutor(){
        return new PBFTPrepareExecutor(getProtocol());
    }
    public Executor newPBFTCommitExecutor(){
        return new PBFTCommitExecutor(getProtocol());
    }
    public Executor newPBFTServerExecuteExecutor(){
        PBFTServerExecuteExecutor exec =  new PBFTServerExecuteExecutor(getProtocol());
        exec.setServer(this);
        return exec;
    }
    public Executor newPBFTSendReplyExecutor(){
        return new PBFTSendReplyExecutor(getProtocol());
    }

    public Executor newPBFTChangeViewExecutor(){
        return new PBFTChangeViewExecutor(getProtocol());
    }
    public Object doService(Object arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
