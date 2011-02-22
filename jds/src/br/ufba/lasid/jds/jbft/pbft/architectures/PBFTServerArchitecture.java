/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.architectures;

import br.ufba.lasid.jds.architectures.Architecture;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTPeriodicStatusActiveExecutor;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTPrePrepareCollectorServant;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTPrepareCollectorServant;
import br.ufba.lasid.jds.jbft.pbft.executors.PBFTRequestCollectorServant;
import trash.br.ufba.lasid.jds.jbft.pbft.executors.PBFTCommitBrokerServant;
import trash.br.ufba.lasid.jds.jbft.pbft.executors.PBFTDoerExecutor;
import trash.br.ufba.lasid.jds.jbft.pbft.executors.PBFTStatusActiveCollectorServant;

/**
 *
 * @author aliriosa
 */
public class PBFTServerArchitecture extends Architecture{

    PBFTServer pbftServer;
    protected static String requestCollector = "requestCollector";
    protected static String preprepareCollector = "preprepareCollector";
    protected static String prepareCollector = "prepareCollector";
    protected static String commitBroker = "commitBroker";
    protected static String replyDoer = "replyDoer";
    protected static String periodicStatusActive = "periodicStatusActive";
    protected static String statusActiveCollector = "statusActiveCollector";
    protected static String communicator = "communicator";
    public PBFTServer getPBFTServer() {
        return pbftServer;
    }

    public void setPBFTServer(PBFTServer pbftServer) {
        this.pbftServer = pbftServer;
    }

    public PBFTServerArchitecture(PBFTServer pbft){
        this.pbftServer = pbft;
    }

    public PBFTServerArchitecture(){
        
    }
    
    @Override
    public void buildup() {

        add(requestCollector, new PBFTRequestCollectorServant(pbftServer));
        add(preprepareCollector, new PBFTPrePrepareCollectorServant(pbftServer));
        add(prepareCollector, new PBFTPrepareCollectorServant(pbftServer));
        add(commitBroker, new PBFTCommitBrokerServant(pbftServer));
        add(replyDoer, new PBFTDoerExecutor(pbftServer));
        add(periodicStatusActive, new PBFTPeriodicStatusActiveExecutor(pbftServer));
        add(statusActiveCollector, new PBFTStatusActiveCollectorServant(pbftServer));
        add(communicator, pbftServer.getCommunicator());

        addConnection(communicator, requestCollector);
        addConnection(communicator, preprepareCollector);
        addConnection(communicator, prepareCollector);
        addConnection(communicator, commitBroker);
        addConnection(communicator, statusActiveCollector);
        addConnection(commitBroker, replyDoer);
        //addConnection(requestCollector, communicator);
        
        super.buildup();
    }



}
