/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.Executor;
import br.ufba.lasid.jds.cs.actions.ExecuteRequestAction;
import br.ufba.lasid.jds.cs.actions.ReceiveRequestAction;
import br.ufba.lasid.jds.cs.actions.SendReplyAction;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.actions.HandleBatchAction;
import br.ufba.lasid.jds.jbft.pbft.actions.BatchRequestAction;
import br.ufba.lasid.jds.jbft.pbft.actions.BatchTimeoutAction;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferCommitAction;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferCommittedRequestAction;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferPrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferPrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.BufferReceivedRequestAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.actions.CommitAction;
import br.ufba.lasid.jds.jbft.pbft.actions.CreateCommitAction;
import br.ufba.lasid.jds.jbft.pbft.actions.CreatePrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.CreatePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteCheckPointAction;
import br.ufba.lasid.jds.jbft.pbft.actions.FecthStateAction;
import br.ufba.lasid.jds.jbft.pbft.actions.PrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.PrepareAction;

import br.ufba.lasid.jds.jbft.pbft.actions.FecthStateAction;
import br.ufba.lasid.jds.jbft.pbft.actions.PrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.PrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteCurrentRoundPhaseOneAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteCurrentRoundPhaseThreeAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteCurrentRoundPhaseTwoAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteReplyPhaseAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteStartNewRoundPhaseOneAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteStartNewRoundPhaseThreeAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteStartNewRoundPhaseTwoAction;
import br.ufba.lasid.jds.jbft.pbft.actions.FecthStateAction;
import br.ufba.lasid.jds.jbft.pbft.actions.GarbageCollectionAction;
import br.ufba.lasid.jds.jbft.pbft.actions.NewViewAction;
import br.ufba.lasid.jds.jbft.pbft.actions.PrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.PrepareAction;
//import br.ufba.lasid.jds.jbft.pbft.actions.SendCheckPointRequestAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ReceiveChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ReceiveCommitAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ReceivePrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ReceivePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.RetransmiteReplyAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ScheduleBacthEndAction;
import br.ufba.lasid.jds.jbft.pbft.actions.ScheduleNewViewAction;
import br.ufba.lasid.jds.jbft.pbft.actions.SendCommitAction;
import br.ufba.lasid.jds.jbft.pbft.actions.SendPrePrepareAction;
import br.ufba.lasid.jds.jbft.pbft.actions.SendPrepareAction;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTHandleBatchExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTBatchRequestExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTBufferCommitExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTBufferCommittedRequestExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTBufferPrePrepareExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTBufferPrepareExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTBufferReceivedRequestExecutor;
import br.ufba.lasid.jds.jbft.pbft.actions.ReceiveNewViewAction;
import br.ufba.lasid.jds.jbft.pbft.actions.SendCheckpointAction;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTChangeViewExecutor;
//import br.ufba.lasid.jds.jbft.pbft.executors.PBFTCommitExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTCreateCommitExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTCreatePrePrepareExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTCreatePrepareExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTExecuteCheckPointExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTExecuteCurrentRoundPhaseOneExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTExecuteCurrentRoundPhaseThreeExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTExecuteCurrentRoundPhaseTwoExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTExecuteReplyPhaseExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTExecuteStartNewRoundPhaseOneExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTExecuteStartNewRoundPhaseThreeExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTExecuteStartNewRoundPhaseTwoExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTFecthStateExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTGarbageCollectionExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTNewViewExecutor;
//import br.ufba.lasid.jds.jbft.pbft.executors.PBFTPrePrepareExecutor;
//import br.ufba.lasid.jds.jbft.pbft.executors.PBFTPrepareExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTReceiveChangeViewExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTReceiveCommitExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTReceivePrePrepareExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTReceivePrepareExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTReceiveNewViewExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTReceiveRequestExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTRetransmiteReplayExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTScheduleBacthEndExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTScheduleNewViewExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTSendCheckpointExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTSendCommitExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTSendPrePrepareExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTSendPrepareExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTSendReplyExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTServerExecuteRequestExecutor;


/**
 *
 * @author aliriosa
 */
public class Agent_ServerPBFT extends Agent_PBFT implements PBFTServer<Integer>{

    @Override
    public void setup() {
        super.setup();
        
        /**
         * This setup can be executed using a configuration file, it could be
         * a better form to make the setup easier.
         */

        /* change view related actions*/
        getProtocol().addExecutor(ChangeViewAction.class, newPBFTChangeViewExecutor());
        getProtocol().addExecutor(NewViewAction.class, newPBFTNewViewExecutor());
        getProtocol().addExecutor(ReceiveNewViewAction.class, newPBFTReceiveNewViewExecutor());
        getProtocol().addExecutor(ScheduleNewViewAction.class, newPBFTScheduleNewViewExecutor());
        getProtocol().addExecutor(ReceiveChangeViewAction.class, newPBFTReceiveChangeViewExecutor());
        
        /* request reception related actions */
        getProtocol().addExecutor(ExecuteStartNewRoundPhaseOneAction.class, newPBFTExecuteStartNewRoundPhaseOneExecutor());
        getProtocol().addExecutor(ExecuteStartNewRoundPhaseTwoAction.class, newPBFTExecuteStartNewRoundPhaseTwoExecutor());
        getProtocol().addExecutor(ExecuteStartNewRoundPhaseThreeAction.class, newPBFTExecuteStartNewRoundPhaseThreeExecutor());
        getProtocol().addExecutor(ScheduleBacthEndAction.class, newPBFTScheduleBacthEndExecutor());
        getProtocol().addExecutor(BufferReceivedRequestAction.class, newBufferReceivedRequestExecutor());
        getProtocol().addExecutor(RetransmiteReplyAction.class, newRetransmiteReplayExecutor());
        getProtocol().addExecutor(HandleBatchAction.class, newPBFTHandleBatchExecutor());
        getProtocol().addExecutor(BatchRequestAction.class, newPBFTBatchRequestExecutor());

        Executor rrExecutor = newPBFTReceiveRequestExecutor();

        getProtocol().addExecutor(ReceiveRequestAction.class, rrExecutor);
        getProtocol().addExecutor(BatchTimeoutAction.class, rrExecutor);

        /* preprepare related actions */
        getProtocol().addExecutor(ExecuteCurrentRoundPhaseOneAction.class, newPBFTExecuteCurrentRoundPhaseOneExecutor());
        getProtocol().addExecutor(CreatePrePrepareAction.class, newPBFTCreatePrePrepareExecutor());
        getProtocol().addExecutor(BufferPrePrepareAction.class, newPBFTBufferPrePrepareExecutor());
        getProtocol().addExecutor(SendPrePrepareAction.class, newPBFTSendPrePrepareExecutor());
        getProtocol().addExecutor(ReceivePrePrepareAction.class, newPBFTReceivePrePrepareExecutor());

        /* prepare related actions */
        getProtocol().addExecutor(ExecuteCurrentRoundPhaseTwoAction.class, newPBFTExecuteCurrentRoundPhaseTwoExecutor());
        getProtocol().addExecutor(CreatePrepareAction.class, newPBFTCreatePrepareExecutor());
        getProtocol().addExecutor(BufferPrepareAction.class, newPBFTBufferPrepareExecutor());
        getProtocol().addExecutor(SendPrepareAction.class, newPBFTSendPrepareExecutor());
        getProtocol().addExecutor(ReceivePrepareAction.class, newPBFTReceivePrepareExecutor());

        /* Commit related actions */
        getProtocol().addExecutor(ExecuteCurrentRoundPhaseThreeAction.class, newPBFTExecuteCurrentRoundPhaseThreeExecutor());
        getProtocol().addExecutor(CreateCommitAction.class, newPBFTCreateCommitExecutor());
        getProtocol().addExecutor(BufferCommitAction.class, newPBFTBufferCommitExecutor());
        getProtocol().addExecutor(SendCommitAction.class, newPBFTSendCommitExecutor());
        getProtocol().addExecutor(ReceiveCommitAction.class, newPBFTReceiveCommitExecutor());

        /* Reply Related Actions */
        getProtocol().addExecutor(ExecuteReplyPhaseAction.class, newPBFTExecuteReplyPhaseExecutor());
        getProtocol().addExecutor(BufferCommittedRequestAction.class, newPBFTBufferCommittedRequestExecutor());
        getProtocol().addExecutor(ExecuteRequestAction.class, newPBFTServerExecuteExecutor());
        getProtocol().addExecutor(SendReplyAction.class, newPBFTSendReplyExecutor());

        /* Garbage Collector Action */
        Executor gcExecutor = newPBFTGarbageCollectionExecutor();
        getProtocol().addExecutor(GarbageCollectionAction.class, gcExecutor);
        getProtocol().addExecutor(SendReplyAction.class, gcExecutor);
        getProtocol().addExecutor(SendCheckpointAction.class, newPBFTSendCheckPointRequestExecutor());
    }

    public Executor newPBFTGarbageCollectionExecutor(){
        return new PBFTGarbageCollectionExecutor(getProtocol());
    }
    
    public Executor newPBFTReceiveNewViewExecutor(){
        return new PBFTReceiveNewViewExecutor(getProtocol());
    }

    public Executor newPBFTBufferCommittedRequestExecutor(){
        return new PBFTBufferCommittedRequestExecutor(getProtocol());
    }
    public Executor newPBFTExecuteReplyPhaseExecutor(){
        return new PBFTExecuteReplyPhaseExecutor(getProtocol());
    }
    public Executor newPBFTReceiveCommitExecutor(){
        return new PBFTReceiveCommitExecutor(getProtocol());
    }

    public Executor newPBFTSendCommitExecutor(){
        return new PBFTSendCommitExecutor(getProtocol());
    }
    public Executor newPBFTBufferCommitExecutor(){
        return new PBFTBufferCommitExecutor(getProtocol());
    }
    public Executor newPBFTCreateCommitExecutor(){
        return new PBFTCreateCommitExecutor(getProtocol());
    }
    public Executor newPBFTExecuteCurrentRoundPhaseThreeExecutor(){
        return new PBFTExecuteCurrentRoundPhaseThreeExecutor(getProtocol());
    }
    public Executor newPBFTReceivePrepareExecutor(){
        return new PBFTReceivePrepareExecutor(getProtocol());
    }
    public Executor newPBFTSendPrepareExecutor(){
        return new PBFTSendPrepareExecutor(getProtocol());
    }
    public Executor newPBFTBufferPrepareExecutor(){
        return new PBFTBufferPrepareExecutor(getProtocol());
    }
    public Executor newPBFTCreatePrepareExecutor(){
        return new PBFTCreatePrepareExecutor(getProtocol());
    }
    public Executor newPBFTBufferPrePrepareExecutor(){
        return new PBFTBufferPrePrepareExecutor(getProtocol());
    }
  public Executor newPBFTCreatePrePrepareExecutor(){
      return new PBFTCreatePrePrepareExecutor(getProtocol());
  }
  public Executor newPBFTExecuteCurrentRoundPhaseTwoExecutor(){
      return new PBFTExecuteCurrentRoundPhaseTwoExecutor(getProtocol());
  }

   public Executor newPBFTExecuteCurrentRoundPhaseOneExecutor(){
       return new PBFTExecuteCurrentRoundPhaseOneExecutor(getProtocol());
   }

    public Executor newPBFTScheduleBacthEndExecutor(){
        return new PBFTScheduleBacthEndExecutor(getProtocol());
    }
    public Executor newPBFTExecuteStartNewRoundPhaseThreeExecutor(){

        return new PBFTExecuteStartNewRoundPhaseThreeExecutor(getProtocol());

    }

    public Executor newPBFTExecuteStartNewRoundPhaseTwoExecutor(){

        return new PBFTExecuteStartNewRoundPhaseTwoExecutor(getProtocol());

    }

    public Executor newPBFTExecuteStartNewRoundPhaseOneExecutor(){

        return new PBFTExecuteStartNewRoundPhaseOneExecutor(getProtocol());

    }
    public Executor newPBFTScheduleNewViewExecutor(){
        return new PBFTScheduleNewViewExecutor(getProtocol());
    }
    public Executor newPBFTHandleBatchExecutor(){
        return new PBFTHandleBatchExecutor(getProtocol());
    }

    public Executor newPBFTBatchRequestExecutor(){
        return new PBFTBatchRequestExecutor(getProtocol());
    }
    
    public Executor newRetransmiteReplayExecutor(){
        return new PBFTRetransmiteReplayExecutor(getProtocol());
    }
    
    public Executor newBufferReceivedRequestExecutor(){
        return new PBFTBufferReceivedRequestExecutor(getProtocol());
    }
    public Executor newPBFTNewViewExecutor(){
        return new PBFTNewViewExecutor(getProtocol());
    }

    public Executor newPBFTReceiveChangeViewExecutor(){
        return new PBFTReceiveChangeViewExecutor(getProtocol());
    }

    public Executor newPBFTExecuteCheckPointExecutor(){
        return new PBFTExecuteCheckPointExecutor(getProtocol());
    }
    public Executor newPBFTFecthStateExecutor(){
        return new PBFTFecthStateExecutor(getProtocol());
    }
    public Executor newPBFTSendCheckPointRequestExecutor(){
        return new PBFTSendCheckpointExecutor(getProtocol());
    }
    public Executor newPBFTReceiveRequestExecutor(){
        return new PBFTReceiveRequestExecutor(getProtocol());
    }

    public Executor newPBFTSendPrePrepareExecutor(){

        return new PBFTSendPrePrepareExecutor(getProtocol());

    }

    public Executor newPBFTReceivePrePrepareExecutor(){

        return new PBFTReceivePrePrepareExecutor(getProtocol());

    }

/*    public Executor newPBFTPrePrepareExecutor(){
        return new PBFTPrePrepareExecutor(getProtocol());
    }
 * 
 */
/*
 public Executor newPBFTPrepareExecutor(){
        return new PBFTPrepareExecutor(getProtocol());
    }
/*
    public Executor newPBFTCommitExecutor(){
        return new PBFTCommitExecutor(getProtocol());
    }
 * 
 */
    public Executor newPBFTServerExecuteExecutor(){
        PBFTServerExecuteRequestExecutor exec =  new PBFTServerExecuteRequestExecutor(getProtocol());
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
