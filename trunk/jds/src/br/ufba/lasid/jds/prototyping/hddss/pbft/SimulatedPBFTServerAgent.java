/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.prototyping.hddss.pbft.comm.SimulatedPBFTCommunicator;
import br.ufba.lasid.jds.security.SHA1withDSASunMessageAuthenticator;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.ufba.lasid.jds.cs.IServer;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.architectures.PBFTServerArchitecture;

/**
 *
 * @author aliriosa
 */
public abstract class SimulatedPBFTServerAgent extends SimulatedPBFTAgent implements IServer<Integer>{

    public SimulatedPBFTServerAgent() {
        setProtocol(new PBFTServer());
    }

    @Override
    public void setup() {

        super.setup();
        SimulatedSchedulerHook scheduler = new SimulatedSchedulerHook(this.infra.clock);

        //((Simulator)this.infra.context).p[this.ID] = (Agent) Adapter.newInstance(this, scheduler);

        getProtocol().setCommunicator(new SimulatedPBFTCommunicator(this));
        getProtocol().setLocalProcess(this);
        getProtocol().setClock(this.infra.clock);
        getProtocol().setScheduler(scheduler);
        ((PBFTServer)getProtocol()).setServer(this);
        getProtocol().setLocalGroup(getGroup());

        try {
            getProtocol().setAuthenticator(new SHA1withDSASunMessageAuthenticator());
            
        } catch (Exception ex) {
            Logger.getLogger(SimulatedPBFTAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

        getProtocol().setArchitecture(
            new PBFTServerArchitecture((PBFTServer)getProtocol())
        );
        
        getProtocol().buildup();
        getProtocol().startup();

    }

    public void setCheckpointPeriod(String period){
        ((PBFTServer)getProtocol()).setCheckpointPeriod(Long.valueOf(period));
    }

    public void setCheckpointFactor(String factor){
        ((PBFTServer)getProtocol()).setCheckpointFactor(Long.valueOf(factor));
    }

    public void setBatchingSize(String size){
     ((PBFTServer)getProtocol()).setBatchSize(Integer.valueOf(size));
    }

    public void setRejuvenationWindow(String timeout){
        ((PBFTServer)getProtocol()).setRejuvenationWindow(Long.valueOf(timeout));
    }


    public void setBatchingTimeout(String timeout){
        ((PBFTServer)getProtocol()).setBatchTimeout(Long.valueOf(timeout));
    }

    public void setViewChangeRetransmittionTimeout(String timeout){
        ((PBFTServer)getProtocol()).setChangeViewRetransmissionTimeout(Long.valueOf(timeout));
    }

    public void setCurrentPrimary(String addr){
        ((PBFTServer)getProtocol()).setCurrentPrimaryID(Integer.valueOf(addr));
    }

    public void setPrimaryFaultTimeout(String timeout){
        ((PBFTServer)getProtocol()).setPrimaryFaultTimeout(Long.valueOf(timeout));
    }

    public void setCurrentView(String v){
        ((PBFTServer)getProtocol()).setCurrentViewNumber(Integer.valueOf(v));
    }

    public void setSendStatusPeriod(String period){
        ((PBFTServer)getProtocol()).setSendStatusPeriod(Long.valueOf(period));
    }

    public void setSlidingWindowSize(String size){
        ((PBFTServer)getProtocol()).setSlidingWindowSize(Long.valueOf(size));
    }

}
