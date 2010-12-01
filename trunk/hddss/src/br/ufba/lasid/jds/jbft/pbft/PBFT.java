/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.cs.ClientServerProtocol;
import br.ufba.lasid.jds.util.Wrapper;
import br.ufba.lasid.jds.factories.PBFTActionFactory;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.security.Authenticator;
import br.ufba.lasid.jds.util.Buffer;
import br.ufba.lasid.jds.util.Clock;
import br.ufba.lasid.jds.util.Debugger;
import br.ufba.lasid.jds.util.Scheduler;

/**
 * Pratical Byzantine Fault Tolerant Protocol (Castro and Liskov, 1999)
 * @author aliriosa
 */
public class PBFT extends ClientServerProtocol{

    public static String DEBUGGER = "__DEBUGGER";
    public static String LOCALGROUP = "__LOCALGROUP";
    public static String GROUPLEADER = "__GROUPLEADER";
    public static String CLIENTSCHEDULER  = "__CLIENTSCHEDULER";
    public static String PRIMARYFDSCHEDULER = "__PRIMARYFDSCHEDULER";
    public static String PREPREPARETIMEOUT = "__PREPREPARETIMEOUT";
    public static String LATEPRIMARYTIMEOUT = "__LATEPRIMAYTIMEOUT";
    public static String CLIENTRETRANSMISSIONTIMEOUT = "__CLIENTRETRANSMISSIONTIMEOUT";
    public static String CLIENTMSGAUTHENTICATOR = "CLIENTMSGAUTHENTICATOR";
    public static String CURRENTVIEW = "__CURRENTVIEW";
    public static String ALLOWABLENUMBEROFFAULTREPLICAS = "__ALLOWABLENUMBEROFFAULTREPLICAS";
    public static String CLOCKSYSTEM = "__CLOCKSYSTEM";
    public static String REQUESTBUFFER = "__REQUESTBUFFER";
    public static String PREPREPAREBUFFER = "__PREPREPAREBUFFER";
   public static String  PREPAREBUFFER = "__PREPAREBUFFER";
    public static String CLIENTAUTHENTICATOR = "__CLIENTAUTHENTICATOR";
    public static String SERVERAUTHENTICATOR = "__SERVERAUTHENTICATOR";
    public static String PRIMARYFAULTTIMEOUT = "__PRIMARYFAULTYTIMEOUT";
    
    @Override
    public void doAction(Wrapper w){
       //System.out.println("[Protocol] call Protocol.perform");
       perform(PBFTActionFactory.create(w));
    }

    public Long getRetransmissionTimeout(){
        return (Long)getContext().get(PBFT.CLIENTRETRANSMISSIONTIMEOUT);
    }

    public Long getPrimaryFaultyTimeout(){
        return (Long)getContext().get(PBFT.PRIMARYFAULTTIMEOUT);
    }

    public void setPrimaryFaultTimeout(Long timeout){
        getContext().put(PBFT.PRIMARYFAULTTIMEOUT, timeout);
    }
    public Long getTimestamp(){
        return new Long(((Clock)getContext().get(PBFT.CLOCKSYSTEM)).value());
    }

    public Debugger getDebugger(){
        return (Debugger) getContext().get(PBFT.DEBUGGER);
    }

    public Scheduler getClientScheduler(){
        return (Scheduler)(getContext().get(PBFT.CLIENTSCHEDULER));
    }

    public Scheduler getPrimaryFDScheduler(){
        return (Scheduler)(getContext().get(PBFT.PRIMARYFDSCHEDULER));
    }

    public Authenticator getServerAuthenticator(){
        return (Authenticator)(getContext().get(PBFT.SERVERAUTHENTICATOR));
    }

    public Authenticator getClientMessageAuthenticator(){
        return (Authenticator)(getContext().get(PBFT.CLIENTMSGAUTHENTICATOR));
    }

    public synchronized Buffer getRequestBuffer(){
        return ((Buffer)(getContext().get(PBFT.REQUESTBUFFER)));
    }

    public boolean isPrimary(){
        return isPrimary(getLocalProcess());
    }
    public boolean isPrimary(br.ufba.lasid.jds.Process p){
        return (getContext().get(PBFT.GROUPLEADER)).equals(p.getID());
    }

    public Integer getCurrentView(){
        return (Integer)getContext().get(PBFT.CURRENTVIEW);
    }

    public void setCurrentView(Integer v){
        getContext().put(PBFT.CURRENTVIEW, v);
    }

    public Group getLocalGroup(){
        return (Group)getContext().get(PBFT.LOCALGROUP);
    }

    public Buffer getPreprepareBuffer() {
        return (Buffer)getContext().get(PBFT.PREPREPAREBUFFER);
    }

    public Buffer getPrepareBuffer() {
        return (Buffer)getContext().get(PBFT.PREPAREBUFFER);
    }

    public boolean belongsToCurrentView(PBFTMessage m) {
        return getCurrentView().equals(m.get(PBFTMessage.VIEWFIELD));
    }

    public boolean existsPrePrepare(PBFTMessage m) {
        
        Buffer buffer = getPreprepareBuffer();
        
        for(Object item : buffer){
            PBFTMessage pp = (PBFTMessage) item;

            boolean viewCheck   = pp.get(PBFTMessage.VIEWFIELD).equals(m.get(PBFTMessage.VIEWFIELD));
            boolean digestCheck = pp.get(PBFTMessage.DIGESTFIELD).equals(m.get(PBFTMessage.DIGESTFIELD));
            boolean sequenceCheck = pp.get(PBFTMessage.SEQUENCENUMBERFIELD).equals(m.get(PBFTMessage.SEQUENCENUMBERFIELD));

            if(viewCheck && digestCheck && sequenceCheck){
                return true;
            }
            
        }
        
        return false;
    }

    public int getServiceBFTResilience(){
        return (int)(Math.floor(getLocalGroup().getGroupSize()/3));
    }
    public boolean gotQuorum(PBFTMessage m){

        if(isPrepare(m)){
            return gotPrepareQuorum(m);
        }

        return false;
    }

    public boolean gotPrepareQuorum(PBFTMessage m){

        Buffer buffer = getPrepareBuffer();
        
        int quorum = 0;
        int f      = getServiceBFTResilience();
        
        for(Object item : buffer){

            PBFTMessage p = (PBFTMessage) item;
            boolean viewCheck   = p.get(PBFTMessage.VIEWFIELD).equals(m.get(PBFTMessage.VIEWFIELD));
            boolean digestCheck = p.get(PBFTMessage.DIGESTFIELD).equals(m.get(PBFTMessage.DIGESTFIELD));
            boolean sequenceCheck = p.get(PBFTMessage.SEQUENCENUMBERFIELD).equals(m.get(PBFTMessage.SEQUENCENUMBERFIELD));
            boolean replicaCheck = p.get(PBFTMessage.REPLICAIDFIELD).equals(m.get(PBFTMessage.REPLICAIDFIELD));

            if(viewCheck && digestCheck && sequenceCheck && !replicaCheck){
                quorum++;
            }
            
        }

        return (quorum >= 2 * f);
    }

    public boolean isPrepare(PBFTMessage m){
        return m.get(PBFTMessage.TYPEFIELD).equals(PBFTMessage.TYPE.PREPARE);
    }

}
