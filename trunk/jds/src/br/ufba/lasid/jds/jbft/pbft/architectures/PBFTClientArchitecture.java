/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.architectures;

import br.ufba.lasid.jds.architectures.Architecture;
import br.ufba.lasid.jds.jbft.pbft.PBFTClient;
import br.ufba.lasid.jds.jbft.pbft.executors.clientexecutors.PBFTReplyCollectorServant;

/**
 *
 * @author aliriosa
 */
public class PBFTClientArchitecture extends Architecture{

    PBFTClient pbftclient;
    protected static String replyCollector = "replyCollector";
    protected static String communicator = "communicator";

    public PBFTClient getPBFTClient() {
        return pbftclient;
    }

    public void setPBFTClient(PBFTClient pbftclient) {
        this.pbftclient = pbftclient;
    }

    public PBFTClientArchitecture(PBFTClient pbftclient){
        this.pbftclient = pbftclient;
    }

    public PBFTClientArchitecture(){
        
    }

    @Override
    public void buildup() {

        add(replyCollector, new PBFTReplyCollectorServant(getPBFTClient()));
        add(communicator, pbftclient.getCommunicator());

        connect(communicator, replyCollector);
        //connect(replyCollector, communicator);

        super.buildup();
    }


}
