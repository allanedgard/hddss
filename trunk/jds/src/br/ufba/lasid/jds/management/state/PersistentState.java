/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state;

import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.JDSConfigurator;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class PersistentState extends BaseState implements IPersistentState{
    Object storageID;
    public PersistentState() throws Exception {
        this(JDSConfigurator.Options);
    }

    public PersistentState(Properties options) throws Exception {
        super(
           (IStateManager) JDSConfigurator.create(JDSConfigurator.PersistentStateProvider, options),
          (IMemory)JDSConfigurator.create(JDSConfigurator.PersistentMemoryProvider, options)
        );
    }

    
    public void setStorageID(Object storageID) throws Exception {
        this.storageID = storageID;
    }

    public Object getStorageID() throws Exception {
        return this.storageID;
    }

    public void checkpoint(Object checkpointID) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void rollback(Object checkpointID) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
