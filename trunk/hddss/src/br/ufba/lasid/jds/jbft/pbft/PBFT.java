/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.cs.ClientServerProtocol;
import br.ufba.lasid.jds.util.Wrapper;
import br.ufba.lasid.jds.factories.PBFTActionFactory;
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
    public static String CLIENTSCHEDULER  = "__SCHEDULER";
    public static String PREPREPARETIMEOUT = "__PREPREPARETIMEOUT";
    public static String LATEPRIMARYTIMEOUT = "__LATEPRIMAYTIMEOUT";
    public static String CLIENTRETRANSMISSIONTIMEOUT = "__CLIENTRETRANSMISSIONTIMEOUT";
    public static String CLIENTMSGAUTHENTICATOR = "CLIENTMSGAUTHENTICATOR";
    public static String CURRENTVIEW = "__CURRENTVIEW";
    public static String ALLOWABLENUMBEROFFAULTREPLICAS = "__ALLOWABLENUMBEROFFAULTREPLICAS";
    public static String CLOCKSYSTEM = "__CLOCKSYSTEM";
    public static String REQUESTBUFFER = "__REQUESTBUFFER";
    public static String CLIENTAUTHENTICATOR = "__CLIENTAUTHENTICATOR";
    public static String SERVERAUTHENTICATOR = "__SERVERAUTHENTICATOR";
    
    @Override
    public void doAction(Wrapper w){
       //System.out.println("[Protocol] call Protocol.perform");
       perform(PBFTActionFactory.create(w));
    }

    public Long getRetransmissionTimeout(){
        return (Long)getContext().get(PBFT.CLIENTRETRANSMISSIONTIMEOUT);
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

}
