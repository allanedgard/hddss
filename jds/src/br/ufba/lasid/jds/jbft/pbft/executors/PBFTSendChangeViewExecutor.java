/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTSendChangeViewExecutor extends PBFTServerExecutor{

    public PBFTSendChangeViewExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        /* get change view buffer */
        Buffer buffer = ((PBFT)getProtocol()).getChangeViewBuffer();

        /* get the local process id */
        Object rID = getProtocol().getLocalProcess().getID();
        
        PBFTMessage cv = null;

        /**
         * look for the last change view of the local process which was added to
         * buffer
         */
        for(int i = buffer.size()-1; i >= 0; i--){

            cv = (PBFTMessage) buffer.get(i);

            Object cvrID = cv.get(PBFTMessage.REPLICAIDFIELD);

            if(cvrID.equals(rID)){

                break;
                
            }
                
        }

        /**
         * send the change view message to group
         */
        if(cv != null){

            Group g = ((PBFT)getProtocol()).getLocalGroup();

            getProtocol().getCommunicator().multicast(cv, g);
            
            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID() + "] "
              + "multicasts change view message at time "
              + ((PBFT)getProtocol()).getTimestamp()
            );

        }
    }



}
