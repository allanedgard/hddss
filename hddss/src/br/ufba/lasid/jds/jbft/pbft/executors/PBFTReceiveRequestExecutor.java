/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.cs.executors.ClientServerReceiveRequestExecutor;
import br.ufba.lasid.jds.Process;
import br.ufba.lasid.jds.Scheduler;
import br.ufba.lasid.jds.Task;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.ChangeViewAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.security.Authenticator;

/**
 *
 * @author aliriosa
 */
public class PBFTReceiveRequestExecutor extends ClientServerReceiveRequestExecutor{

    public PBFTReceiveRequestExecutor(Protocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        PBFTMessage m = (PBFTMessage)act.getMessage();
        
        Authenticator<PBFTMessage> auth =
                (Authenticator<PBFTMessage>) getProtocol().getContext().get(
                    PBFT.CLIENTMSGAUTHENTICATOR
                 );


        if(!auth.check(m)){
            /*is a malicious client ... do nothing*/
            return;
        }

        if(isPrimary(getProtocol().getLocalProcess())){

            PBFTMessage pp = createPrePrepare(m);
            getProtocol().getCommunicator().multicast(
                pp, (Group)getProtocol().getContext().get(PBFT.LOCALGROUP)
            );
            
        }else{

            Scheduler scheduler = (Scheduler)getProtocol().getContext().get(PBFT.SCHEDULER);

            scheduler.schedule(
               (Task)getProtocol().getExecutors().get(ChangeViewAction.class),
               (Long)getProtocol().getContext().get(PBFT.LATEPRIMARYTIMEOUT)
            );
            
        }
        
    }
    /*
     *  [TODO]If the local process is the primary replica then it has to assign 
        a sequence number for the request and create a preprepare message.
     */
    public PBFTMessage createPrePrepare(PBFTMessage request){
                
        Authenticator<PBFTMessage> auth =
                (Authenticator<PBFTMessage>) getProtocol().getContext().get(
                    PBFT.CLIENTMSGAUTHENTICATOR
                 );

        PBFTMessage pp = new PBFTMessage(PBFTMessage.TYPE.PREPREPARE);

        pp.put("REQUEST", request);
        pp.put("VIEW", getProtocol().getContext().get(PBFT.CURRENTVIEW));
        pp.setSequenceNumber(PBFTMessage.newSequenceNumber());
        pp.put("DIGEST", auth.digest(request));
        
        return pp;
        
    }
    /**
     * [TODO] develop this method.
     * @param p
     * @return
     */
    public boolean isPrimary(Process p){
        return ((Process)getProtocol().getContext().get(PBFT.GROUPLEADER)).equals(p);
    }



}
