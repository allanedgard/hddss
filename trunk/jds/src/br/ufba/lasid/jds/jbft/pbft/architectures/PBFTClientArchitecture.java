/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.architectures;

import br.ufba.lasid.jds.architectures.Architecture;
import br.ufba.lasid.jds.jbft.pbft.PBFTClient;
import br.ufba.lasid.jds.jbft.pbft.executors.clientexecutors.PBFTReplyCollectorServant;
import br.ufba.lasid.jds.jbft.pbft.handlers.PBFTClientServant;

/**
 *
 * @author aliriosa
 */
public class PBFTClientArchitecture extends Architecture{

    PBFTClient pbftc;
    protected static String PBFTServantTAG = "__PBFTServant";
    protected static String PBFTCommunicatorTAG = "__PBFTCommunicatorTAG";

    public PBFTClient getPBFTClient() {
        return pbftc;
    }

    public void setPBFTClient(PBFTClient pbftc) {
        this.pbftc = pbftc;
    }

    public PBFTClientArchitecture(PBFTClient pbftc){
        this.pbftc = pbftc;
    }

    public PBFTClientArchitecture(){
        
    }

    @Override
    public void buildup() {

        add(PBFTServantTAG, new PBFTClientServant(getPBFTClient()));
        add(PBFTCommunicatorTAG, pbftc.getCommunicator());

        connect(PBFTCommunicatorTAG, PBFTServantTAG);

        super.buildup();
    }


}
