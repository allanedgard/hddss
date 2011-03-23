/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.trash.state;

import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.util.JDSUtility;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class PersistentState extends BaseState implements IPersistentState{
    Object storageID;
    public PersistentState() throws Exception {
        this(JDSUtility.Options);
    }

    public PersistentState(Properties options) throws Exception {
        super(
           (IStateManager) JDSUtility.create(JDSUtility.PersistentStateProvider, options),
          (IMemory)JDSUtility.create(JDSUtility.PersistentMemoryProvider, options)
        );
    }

    
    public void setStorageID(Object storageID) throws Exception {
        this.storageID = storageID;
    }

    public Object getStorageID() throws Exception {
        return this.storageID;
    }
    
}
