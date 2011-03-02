/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.architectures;

import br.ufba.lasid.jds.architectures.Architecture;
import br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors.PBFTPeriodicStatusActiveExecutor;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors.PBFTCheckpointCollectorServant;
import br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors.PBFTPrePrepareCollectorServant;
import br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors.PBFTPrepareCollectorServant;
import br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors.PBFTRequestCollectorServant;
import br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors.PBFTCommitBrokerServant;
import br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors.PBFTDoerExecutor;
import br.ufba.lasid.jds.jbft.pbft.executors.serverexecutors.PBFTStatusActiveCollectorServant;

/**
 *
 * @author aliriosa
 */
public class PBFTServerArchitecture extends Architecture{

    PBFTServer pbft;
    protected static String requestCollector = "requestCollector";
    protected static String preprepareCollector = "preprepareCollector";
    protected static String prepareCollector = "prepareCollector";
    protected static String commitBroker = "commitBroker";
    protected static String replyDoer = "replyDoer";
    protected static String periodicStatusActive = "periodicStatusActive";
    protected static String statusActiveCollector = "statusActiveCollector";
    protected static String checkpointCollector = "checkpointCollector";
    protected static String communicator = "communicator";
    
    public PBFTServer getPBFTServer() {
        return pbft;
    }

    public void setPBFTServer(PBFTServer pbftServer) {
        this.pbft = pbftServer;
    }

    public PBFTServerArchitecture(PBFTServer pbft){
        this.pbft = pbft;
    }

    public PBFTServerArchitecture(){
        
    }
    
    @Override
    public void buildup() {

        //set();
        add(requestCollector, new PBFTRequestCollectorServant(pbft));
        add(preprepareCollector, new PBFTPrePrepareCollectorServant(pbft));
        add(prepareCollector, new PBFTPrepareCollectorServant(pbft));
        add(commitBroker, new PBFTCommitBrokerServant(pbft));
        add(replyDoer, new PBFTDoerExecutor(pbft));
        add(periodicStatusActive, new PBFTPeriodicStatusActiveExecutor(pbft));
        add(statusActiveCollector, new PBFTStatusActiveCollectorServant(pbft));
        add(checkpointCollector, new PBFTCheckpointCollectorServant(pbft));
        add(communicator, pbft.getCommunicator());

        connect(communicator, requestCollector);
        connect(communicator, preprepareCollector);
        connect(communicator, prepareCollector);
        connect(communicator, commitBroker);
        connect(communicator, statusActiveCollector);
        connect(communicator, checkpointCollector);
        connect(commitBroker, replyDoer);

        //event("AfterRequestExecutionEvent", pbft, "updateXXXXX", after);
        //handle(""AfterRequestExecutionEvent", ckecpoint);

        //connect(requestCollector, communicator);
        
        super.buildup();
    }



}
