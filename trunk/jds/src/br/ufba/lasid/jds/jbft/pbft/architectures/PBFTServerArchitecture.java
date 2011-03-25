/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.architectures;

import br.ufba.lasid.jds.adapters.EventHandler;
import br.ufba.lasid.jds.architectures.Architecture;
import br.ufba.lasid.jds.jbft.pbft.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.fmm.PBFTServerMultiModeMachine;
import br.ufba.lasid.jds.jbft.pbft.handlers.PBFTServerServant;

/**
 *
 * @author aliriosa
 */
public class PBFTServerArchitecture extends Architecture{

    IPBFTServer pbft;
//    protected static String requestCollector = "requestCollector";
//    protected static String preprepareCollector = "preprepareCollector";
//    protected static String prepareCollector = "prepareCollector";
//    protected static String commitBroker = "commitBroker";
//    protected static String bagBroker = "bagBroker";
//    protected static String replyDoer = "replyDoer";
//    protected static String periodicStatusActive = "periodicStatusActive";
//    protected static String statusActiveCollector = "statusActiveCollector";
//    protected static String checkpointCollector = "checkpointCollector";
    protected static String PBFTCommunicatorTag = "__PBFTCommunicator";
    public static String PBFTServantTag = "__PBFTServant";
    
    public IPBFTServer getPBFTServer() {
        return pbft;
    }

    public void setPBFTServer(IPBFTServer pbftServer) {
        this.pbft = pbftServer;
    }

    public PBFTServerArchitecture(IPBFTServer pbft){
        this.pbft = pbft;
    }

    public PBFTServerArchitecture(){
        
    }
    
    @Override
    public void buildup() {

        add(PBFTServantTag, new PBFTServerServant(new PBFTServerMultiModeMachine(pbft)));
        add(PBFTCommunicatorTag, pbft.getCommunicator());
        connect(PBFTCommunicatorTag, PBFTServantTag);

        //set();
//        add(requestCollector, new PBFTRequestCollectorServant(pbft));
//        add(preprepareCollector, new PBFTPrePrepareCollectorServant(pbft));
//        add(prepareCollector, new PBFTPrepareCollectorServant(pbft));
//        add(commitBroker, new PBFTCommitBrokerServant(pbft));
//        add(bagBroker, new PBFTBagBrokerServant(pbft));
//        add(replyDoer, new PBFTDoerExecutor(pbft));
//        add(periodicStatusActive, new PBFTPeriodicStatusActiveExecutor(pbft));
//        add(statusActiveCollector, new PBFTStatusActiveCollectorServant(pbft));
//        add(checkpointCollector, new PBFTCheckpointCollectorServant(pbft));
//        connect(PBFTCommunicatorTag, requestCollector);
//        connect(PBFTCommunicatorTag, preprepareCollector);
//        connect(PBFTCommunicatorTag, prepareCollector);
//        connect(PBFTCommunicatorTag, commitBroker);
//        connect(PBFTCommunicatorTag, bagBroker);
//        connect(PBFTCommunicatorTag, statusActiveCollector);
//        connect(PBFTCommunicatorTag, checkpointCollector);
//        connect(commitBroker, replyDoer);
//        connect(bagBroker, replyDoer);
        
        super.buildup();
    }



}
