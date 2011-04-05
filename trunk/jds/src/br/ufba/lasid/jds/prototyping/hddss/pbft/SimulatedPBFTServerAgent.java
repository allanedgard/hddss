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
import br.ufba.lasid.jds.jbft.pbft.server.IPBFTServer;
import br.ufba.lasid.jds.jbft.pbft.server.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.architectures.PBFTServerArchitecture;
import br.ufba.lasid.jds.jbft.pbft.fmm.PBFTServerMultiModeMachine;

/**
 *
 * @author aliriosa
 */
public abstract class SimulatedPBFTServerAgent extends SimulatedPBFTAgent implements IServer<Integer>{

    public SimulatedPBFTServerAgent() {
        setProtocol(new PBFTServerMultiModeMachine(PBFTServer.create()));
    }

    @Override
    public void setup() {

        super.setup();
        SimulatedScheduler scheduler = new SimulatedScheduler(this.infra.clock);
        //scheduler.setAgent(this);
        //((Simulator)this.infra.context).p[this.ID] = (Agent) Adapter.create(this, scheduler);

        getProtocol().setCommunicator(new SimulatedPBFTCommunicator(this));
        getProtocol().setLocalProcess(this);
        getProtocol().setClock(this.infra.clock);
        getProtocol().setScheduler(scheduler);
        ((IPBFTServer)getProtocol()).setServer(this);
        getProtocol().setLocalGroup(getGroup());

        try {
            getProtocol().setAuthenticator(new SHA1withDSASunMessageAuthenticator());
            
        } catch (Exception ex) {
            Logger.getLogger(SimulatedPBFTAgent.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

        getProtocol().setArchitecture(
            new PBFTServerArchitecture((IPBFTServer)getProtocol())
        );

        getProtocol().getArchitecture().buildup();
        
//        getProtocol().buildup();

    }

    public void setCheckpointPeriod(String period){
        ((IPBFTServer)getProtocol()).setCheckpointPeriod(Long.valueOf(period));
    }

    public void setCheckpointFactor(String factor){
        ((IPBFTServer)getProtocol()).setCheckpointFactor(Long.valueOf(factor));
    }

    public void setBatchingSize(String size){
     ((IPBFTServer)getProtocol()).setBatchSize(Integer.valueOf(size));
    }

    public void setRejuvenationWindow(String timeout){
        ((IPBFTServer)getProtocol()).setRejuvenationWindow(Long.valueOf(timeout));
    }

    public void setBatchingTimeout(String timeout){
        ((IPBFTServer)getProtocol()).setBatchTimeout(Long.valueOf(timeout));
    }

    public void setViewChangeRetransmittionTimeout(String timeout){
        ((IPBFTServer)getProtocol()).setChangeViewRetransmissionTimeout(Long.valueOf(timeout));
    }
    
//    public void setCurrentPrimary(String addr){
//        ((IPBFTServer)getProtocol()).setCurrentPrimaryID(Integer.valueOf(addr));
//    }

    public void setPrimaryFaultTimeout(String timeout){
        ((IPBFTServer)getProtocol()).setPrimaryFaultTimeout(Long.valueOf(timeout));
    }

    public void setCurrentView(String v){
        ((IPBFTServer)getProtocol()).setCurrentViewNumber(Integer.valueOf(v));
    }

    public void setSendStatusPeriod(String period){
        ((IPBFTServer)getProtocol()).setSendStatusPeriod(Long.valueOf(period));
    }

    public void setSlidingWindowSize(String size){
        ((IPBFTServer)getProtocol()).setSlidingWindowSize(Long.valueOf(size));
    }

    @Override
    public void startup() {
        ((IPBFTServer)getProtocol()).getArchitecture().buildup();
        ((IPBFTServer)getProtocol()).loadState();
        ((IPBFTServer)getProtocol()).schedulePeriodicStatusSend();
        ((IPBFTServer)getProtocol()).emitFetch();
    }


}
