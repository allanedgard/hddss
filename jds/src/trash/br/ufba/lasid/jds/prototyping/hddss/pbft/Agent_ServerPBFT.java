/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.prototyping.hddss.pbft;


//import br.ufba.lasid.jds.jbft.pbft.actions.SendCheckPointRequestAction;
//import br.ufba.lasid.jds.jbft.pbft.executors.PBFTCommitExecutor;
//import br.ufba.lasid.jds.jbft.pbft.executors.PBFTPrePrepareExecutor;
//import br.ufba.lasid.jds.jbft.pbft.executors.PBFTPrepareExecutor;
import br.ufba.lasid.jds.util.IPayload;


/**
 *
 * @author aliriosa
 */
public class Agent_ServerPBFT extends Agent_PBFT {

    public IPayload doService(IPayload arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
/*
    @Override
    public void setup() {
        super.setup();
        
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

        getProtocol().addExecutor(ExecuteCurrentRoundPhaseOneAction.class, newPBFTExecuteCurrentRoundPhaseOneExecutor());
        getProtocol().addExecutor(CreatePrePrepareAction.class, newPBFTCreatePrePrepareExecutor());
        getProtocol().addExecutor(BufferPrePrepareAction.class, newPBFTBufferPrePrepareExecutor());
        getProtocol().addExecutor(SendPrePrepareAction.class, newPBFTSendPrePrepareExecutor());
        getProtocol().addExecutor(ReceivePrePrepareAction.class, newPBFTReceivePrePrepareExecutor());

        getProtocol().addExecutor(ExecuteCurrentRoundPhaseTwoAction.class, newPBFTExecuteCurrentRoundPhaseTwoExecutor());
        getProtocol().addExecutor(CreatePrepareAction.class, newPBFTCreatePrepareExecutor());
        getProtocol().addExecutor(BufferPrepareAction.class, newPBFTBufferPrepareExecutor());
        getProtocol().addExecutor(SendPrepareAction.class, newPBFTSendPrepareExecutor());
        getProtocol().addExecutor(ReceivePrepareAction.class, newPBFTReceivePrepareExecutor());

        getProtocol().addExecutor(ExecuteCurrentRoundPhaseThreeAction.class, newPBFTExecuteCurrentRoundPhaseThreeExecutor());
        getProtocol().addExecutor(CreateCommitAction.class, newPBFTCreateCommitExecutor());
        getProtocol().addExecutor(BufferCommitAction.class, newPBFTBufferCommitExecutor());
        getProtocol().addExecutor(SendCommitAction.class, newPBFTSendCommitExecutor());
        getProtocol().addExecutor(ReceiveCommitAction.class, newPBFTReceiveCommitExecutor());

        getProtocol().addExecutor(ExecuteReplyPhaseAction.class, newPBFTExecuteReplyPhaseExecutor());
        getProtocol().addExecutor(BufferCommittedRequestAction.class, newPBFTBufferCommittedRequestExecutor());
        getProtocol().addExecutor(ExecuteRequestAction.class, newPBFTServerExecuteExecutor());
        getProtocol().addExecutor(SendReplyAction.class, newPBFTSendReplyExecutor());

        Executor gcExecutor = newPBFTGarbageCollectionExecutor();
        getProtocol().addExecutor(GarbageCollectionAction.class, gcExecutor);
        getProtocol().addExecutor(SendReplyAction.class, gcExecutor);
        getProtocol().addExecutor(SendCheckpointAction.class, newPBFTSendCheckpointRequestExecutor());
        getProtocol().addExecutor(ReceiveCheckpointAction.class, newPBFTReceiveCheckpointExecutor());
        getProtocol().addExecutor(BufferCheckpointAction.class, newPBFTBufferCheckpointExecutor());
        getProtocol().addExecutor(CheckStateAction.class, newPBFTCheckStateExecutor());
        getProtocol().addExecutor(ExecuteCheckPointAction.class, newPBFTExecuteCheckpointExecutor());
        getProtocol().addExecutor(RejuvenationAction.class, newPBFTRejuvenationExecutor());

        //getProtocol().addExecutor(ChangeViewAction.class, newPBFTChangeViewExecutor());
        getProtocol().addExecutor(ExecuteChangeViewRoundOneAction.class, newPBFTExecuteChangeViewRoundOneExecutor());
        getProtocol().addExecutor(CreateChangeViewAction.class, newPBFTCreateChangeViewExecutor());
        getProtocol().addExecutor(BufferChangeViewAction.class, newPBFTBufferChangeViewExecutor());
        getProtocol().addExecutor(SendChangeViewAction.class, newPBFTSendChangeViewExecutor());
        getProtocol().addExecutor(ReceiveChangeViewAction.class, newPBFTReceiveChangeViewExecutor());
        getProtocol().addExecutor(ExecuteChangeViewRoundTwoAction.class, newPBFTExecuteChangeViewRoundTwoExecutor());
        getProtocol().addExecutor(SendChangeViewAckAction.class, newPBFTSendChangeViewAckExecutor());
        getProtocol().addExecutor(ReceiveChangeViewAckAction.class, newPBFTReceiveChangeViewAckExecutor());
        getProtocol().addExecutor(ScheduleNewViewAction.class, newPBFTScheduleNewViewExecutor());
        getProtocol().addExecutor(DetectPrimaryFailureAction.class, newPBFTDetectPrimaryFailureExecutor());
        getProtocol().addExecutor(ExecuteChangeViewRoundThreeAction.class, newPBFTExecuteChangeViewRoundThreeExecutor());
        getProtocol().addExecutor(CreateChangeViewAckAction.class, newPBFTCreateChangeViewAckExecutor());
        getProtocol().addExecutor(BufferChangeViewAckAction.class, newPBFTBufferChangeViewAckExecutor());
        getProtocol().addExecutor(NewViewAction.class, newPBFTNewViewExecutor());
        getProtocol().addExecutor(ReceiveNewViewAction.class, newPBFTReceiveNewViewExecutor());
        getProtocol().addExecutor(RetransmiteChangeViewAction.class, newPBFTRetransmiteChangeViewExecutor());

    }

    public Executor newPBFTRetransmiteChangeViewExecutor(){
        return new PBFTRetransmiteChangeViewExecutor(getProtocol());
    }
    public Executor newPBFTCreateChangeViewAckExecutor(){
        return new PBFTCreateChangeViewAckExecutor(getProtocol());
    }
    public Executor newPBFTBufferChangeViewAckExecutor(){

        return new PBFTBufferChangeViewAckExecutor(getProtocol());
        
    }

    public Executor newPBFTExecuteChangeViewRoundThreeExecutor(){

        return new PBFTExecuteChangeViewRoundThreeExecutor(getProtocol());
        
    }
    public Executor newPBFTRejuvenationExecutor(){
        return new PBFTRejuvenationExecutor(getProtocol());
    }

    public Executor newPBFTReceiveChangeViewAckExecutor(){
        return new PBFTReceiveChangeViewAckExecutor(getProtocol());
    }

    public Executor newPBFTSendChangeViewAckExecutor(){
        return new PBFTSendChangeViewAckExecutor(getProtocol());
    }

    public Executor newPBFTExecuteChangeViewRoundTwoExecutor(){

        return new PBFTExecuteChangeViewRoundTwoExecutor(getProtocol());
        
    }
    public Executor newPBFTSendChangeViewExecutor(){

        return new PBFTSendChangeViewExecutor(getProtocol());
        
    }

    public Executor newPBFTBufferChangeViewExecutor(){
        return new PBFTBufferChangeViewExecutor(getProtocol());
    }

    public Executor newPBFTCreateChangeViewExecutor(){
        return new PBFTCreateChangeViewExecutor(getProtocol());
    }
    public Executor newPBFTExecuteChangeViewRoundOneExecutor(){
        return new PBFTExecuteChangeViewRoundOneExecutor(getProtocol());
    }
    public Executor newPBFTDetectPrimaryFailureExecutor(){
        return new PBFTDetectPrimaryFailureExecutor(getProtocol());

    }
    public Executor newPBFTBufferCheckpointExecutor(){
        return new PBFTBufferCheckpointExecutor(getProtocol());
    }
    public Executor newPBFTCheckStateExecutor(){
        return new PBFTCheckStateExecutor(getProtocol());
    }
    public Executor newPBFTReceiveCheckpointExecutor(){
        return new PBFTReceiveCheckpointExecutor(getProtocol());
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

    public Executor newPBFTExecuteCheckpointExecutor(){
        return new PBFTExecuteCheckPointExecutor(getProtocol());
    }
    public Executor newPBFTFecthStateExecutor(){
        return new PBFTFetchStateExecutor(getProtocol());
    }
    public Executor newPBFTSendCheckpointRequestExecutor(){
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
    /*
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
    
    public IPayload doService(IPayload arg) {
        return null;//throw new UnsupportedOperationException("Not supported yet.");
    }
*/
}
