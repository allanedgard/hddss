/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state.decorators;

import br.ufba.lasid.jds.management.trash.state.IPersistentState;
import br.ufba.lasid.jds.management.trash.state.IPersistentStateProvider;
import br.ufba.lasid.jds.management.trash.state.PersistentState;
import br.ufba.lasid.jds.util.JDSUtility;
import java.util.Properties;

/**
 * Recoverable State with Persitent Memory
 * @author aliriosa
 */
public class RPStateProvider implements IPersistentStateProvider{

    public IPersistentState create(Properties options) throws Exception {
        Properties ioptions = new Properties(JDSUtility.Options);
        ioptions.putAll(options);
        ioptions.put(JDSUtility.Filename, ioptions.getProperty(JDSUtility.PersistentStorageID));
        String storageID = ioptions.getProperty(JDSUtility.PersistentStorageID);
        IPersistentState state =  new PersistentState(ioptions);
        state.setStorageID(storageID);
        return state;
    }

}
