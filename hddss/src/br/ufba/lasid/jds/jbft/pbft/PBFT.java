/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.cs.ClientServerProtocol;
import br.ufba.lasid.jds.util.Wrapper;
import br.ufba.lasid.jds.factories.PBFTActionFactory;

/**
 * Pratical Byzantine Fault Tolerant Protocol (Castro and Liskov, 1999)
 * @author aliriosa
 */
public class PBFT extends ClientServerProtocol{

    public static String LOCALGROUP = "__LOCALGROUP";
    public static String GROUPLEADER = "__GROUPLEADER";
    public static String SCHEDULER  = "__SCHEDULER";
    public static String PREPREPARETIMEOUT = "__PREPREPARETIMEOUT";
    public static String LATEPRIMARYTIMEOUT = "__LATEPRIMAYTIMEOUT";
    public static String CLIENTRETRANSMISSIONTIMEOUT = "__CLIENTRETRANSMISSIONTIMEOUT";
    public static String CLIENTMSGAUTHENTICATOR = "CLIENTMSGAUTHENTICATOR";
    public static String CURRENTVIEW = "__CURRENTVIEW";
    public static String ALLOWABLENUMBEROFFAULTREPLICAS = "__ALLOWABLENUMBEROFFAULTREPLICAS";
    public static String CLIENTRETRANSMISSIONTIMOUT = "__CLIENTRETRANSMISSIONTIMOUT";
    
    
    @Override
    public void doAction(Wrapper w){
       //System.out.println("[Protocol] call Protocol.perform");
       perform(PBFTActionFactory.create(w));
    }


}
