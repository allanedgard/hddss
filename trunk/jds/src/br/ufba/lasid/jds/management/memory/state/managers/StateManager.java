/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.state.managers;

import br.ufba.lasid.jds.management.memory.BufferMemory;
import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.state.IState;
import br.ufba.lasid.jds.management.memory.storages.IStorage;
import br.ufba.lasid.jds.util.XObject;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author aliriosa
 */
public class StateManager implements IStateManager{
    IState currentState;

    public StateManager(IState currentState) throws Exception{
        this.currentState = currentState;
    }

    public StateManager(byte[] buf) throws Exception{
        setCurrentState(buf);
    }

    public void set(String variable, Object value) throws Exception {
        this.currentState.set(variable, value);
    }

    public Object get(String variable) throws Exception {
        return this.currentState.get(variable);
    }

    public IStorage getStorage(String storageID) {
        return this.currentState.getStorage(storageID);
    }

    public void connect(String storageID, IStorage storage) {
        this.currentState.connect(storageID, storage);
    }

    public void disconnect(String storageID) {
        this.currentState.disconnect(storageID);
    }

    public void setCurrentState(IState state) throws Exception {
        this.currentState = state;
    }

    public IState getCurrentState() throws Exception {
        return this.currentState;
    }

    public void setCurrentState(byte[] buf) throws Exception {
        IState state = (IState)XObject.byteArrayToObject(buf);
        setCurrentState(state);
    }

    public IMemory bufferMemory() throws Exception {

        BufferMemory bm = new BufferMemory(byteArray());
        bm.setOptions(options);
        return bm;
        
    }

    public Set<String> getStorateIDSet(){
        return this.currentState.getStorateIDSet();
    }

    Properties options = new Properties();

    public void setOptions(Properties options){
        this.options.putAll(options);
    }

    public Properties getOptions(){
        return this.options;
    }

    public byte[] byteArray() throws Exception {
        return XObject.objectToByteArray(getCurrentState());
    }


}
