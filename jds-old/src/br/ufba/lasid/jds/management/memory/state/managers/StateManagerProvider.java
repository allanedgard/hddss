/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.state.managers;

import br.ufba.lasid.jds.management.memory.state.State;
import br.ufba.lasid.jds.util.JDSUtility;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class StateManagerProvider implements IStateManagerProvider{

    public IStateManager create(Properties options) throws Exception {

        Properties ioptions = new Properties(JDSUtility.Options);
        ioptions.putAll(options);

        StateManager sm = new StateManager(new State());
        
        return sm;
        
    }
}
