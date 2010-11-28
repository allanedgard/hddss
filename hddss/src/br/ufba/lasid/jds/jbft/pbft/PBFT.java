/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.Protocol;
import br.ufba.lasid.jds.group.Group;
import br.ufba.lasid.jds.util.Wrapper;
import br.ufba.lasid.jds.factories.PBFTActionFactory;

/**
 *
 * @author aliriosa
 */
public class PBFT extends Protocol{
    
    Group group;

    @Override
    public void doAction(Wrapper w){
       System.out.println("[Protocol] call Protocol.perform");
       perform(PBFTActionFactory.create(w));
    }

}
