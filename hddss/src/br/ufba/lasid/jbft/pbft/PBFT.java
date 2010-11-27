/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jbft.pbft;

import br.ufba.lasid.jbft.Protocol;
import br.ufba.lasid.jbft.Group;
import br.ufba.lasid.jbft.Wrapper;
import br.ufba.lasid.jbft.pbft.actions.PBFTActionFactory;

/**
 *
 * @author aliriosa
 */
public class PBFT extends Protocol{
    
    Group group;

    @Override
    public void doAction(Wrapper w){
       perform(PBFTActionFactory.create(w));
    }

}
