/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state;

import br.ufba.lasid.jds.management.JDSConfigurator;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class PersistentStateProvider implements IPersistentStateProvider{

    public IPersistentState create(Properties options) throws Exception {
        Properties ioptions = new Properties(JDSConfigurator.Options);
        ioptions.putAll(options);
        ioptions.put(JDSConfigurator.Filename, ioptions.getProperty(JDSConfigurator.PersistentStorageID));
        String storageID = ioptions.getProperty(JDSConfigurator.PersistentStorageID);
//        ioptions.setProperty(JDSConfigurator.hasAVolatileStateMemory, "false");
        IPersistentState state =  new PersistentState(ioptions);
        state.setStorageID(storageID);
        return state;
    }

}
