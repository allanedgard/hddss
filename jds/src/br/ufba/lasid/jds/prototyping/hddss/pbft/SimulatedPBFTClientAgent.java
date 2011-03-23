/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.cs.IClient;
import br.ufba.lasid.jds.prototyping.hddss.pbft.comm.SimulatedPBFTCommunicator;
import br.ufba.lasid.jds.jbft.pbft.PBFTClient;
import br.ufba.lasid.jds.jbft.pbft.architectures.PBFTClientArchitecture;
import br.ufba.lasid.jds.security.SHA1withDSASunMessageAuthenticator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public abstract class SimulatedPBFTClientAgent extends SimulatedPBFTAgent implements IClient<Integer>{

    public SimulatedPBFTClientAgent() {
        setProtocol(new PBFTClient());
    }

    @Override
    public void setup() {
        super.setup();

        SimulatedScheduler scheduler = new SimulatedScheduler(this.infra.clock);
        scheduler.setAgent(this);

        //((Simulator)this.infra.context).p[this.ID] = (Agent) Adapter.newInstance(this, scheduler);
        
        getProtocol().setCommunicator(new SimulatedPBFTCommunicator(this));
        getProtocol().setLocalProcess(this);
        getProtocol().setClock(this.infra.clock);
        getProtocol().setScheduler(scheduler);
        getProtocol().setLocalGroup(getGroup());
        getProtocol().setRemoteProcess(getGroup());
        ((PBFTClient)getProtocol()).setClient(this);

        try {
            getProtocol().setAuthenticator(new SHA1withDSASunMessageAuthenticator());

        } catch (Exception ex) {
            Logger.getLogger(SimulatedPBFTAgent.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

        getProtocol().setArchitecture(
            new PBFTClientArchitecture((PBFTClient)getProtocol())
        );

        getProtocol().getArchitecture().buildup();
        
                
    }

    public void setRetransmissionTimeout(String timeout) {
        ((PBFTClient)getProtocol()).setRetransmissionTimeout(new Long(timeout));
    }

    @Override
    public void startup() {
        //getProtocol().startup();
    }


}