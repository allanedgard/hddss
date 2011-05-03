/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.cs.IClient;
import br.ufba.lasid.jds.prototyping.hddss.pbft.comm.SimulatedPBFTCommunicator;
import br.ufba.lasid.jds.jbft.pbft.client.PBFTClient;
import br.ufba.lasid.jds.jbft.pbft.architectures.PBFTClientArchitecture;
import br.ufba.lasid.jds.prototyping.hddss.RuntimeSupport.Variable;
import br.ufba.lasid.jds.util.JDSUtility;
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



        JDSUtility.debug = infra.debug;
        JDSUtility.out = infra.context.get(Variable.StdOutput).<java.io.PrintStream>value();

        SimulatedScheduler scheduler = new SimulatedScheduler(this.infra.clock);
        
        getProtocol().setCommunicator(new SimulatedPBFTCommunicator(this, getProtocol()));
        getProtocol().setLocalProcess(this);
        getProtocol().setClock(this.infra.cpu);
        getProtocol().setScheduler(scheduler);
        getProtocol().setLocalGroup(getGroup());
        getProtocol().setRemoteProcess(getGroup());
        ((PBFTClient)getProtocol()).setClient(this);

        try {
            //getProtocol().setAuthenticator(new SHA1withDSASunMessageAuthenticator());
            getProtocol().setAuthenticator(new SimulatedAuthenticator(this));

        } catch (Exception ex) {
            Logger.getLogger(SimulatedPBFTAgent.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

        getProtocol().setArchitecture(
            new PBFTClientArchitecture((PBFTClient)getProtocol())
        );
   
                
    }

//    public void setRetransmissionTimeout(String timeout) {
//        ((PBFTClient)getProtocol()).setRetransmissionTimeout(new Long(timeout));
//    }

    public void setMaxTimeout(String timeout) {
        ((PBFTClient)getProtocol()).setMaxTimeout(Double.valueOf(timeout));
    }

    public void setMinTimeout(String timeout) {
        ((PBFTClient)getProtocol()).setMinTimeout(Double.valueOf(timeout));
    }




    @Override
    public void startup() {
         getProtocol().getArchitecture().buildup();
    }


}
