/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.state;

import br.ufba.lasid.jds.management.memory.storages.IStorage;
import br.ufba.lasid.jds.management.memory.storages.Storagetable;
import java.util.Hashtable;
import java.util.Set;

/**
 *
 * @author aliriosa
 */
public class State implements IState{
    
    Hashtable<String, Object> variables = new Hashtable<String, Object>();
    Storagetable storages = new Storagetable();

    public void set(String variable, Object value) throws Exception {
        variables.put(variable, value);
    }//end set(variable, value)

    public Object get(String variable) throws Exception {
        return variables.get(variable);
    }//end value = get(variable)

    public IStorage getStorage(String storagename) {
        return storages.get(storagename);
    }

    public void connect(String storagename, IStorage storage) {
        storages.put(storagename, storage);
    }

    public void disconnect(String storagename){
        storages.remove(storagename);
    }

    public Set<String> getStorateIDSet(){
        return (Set<String>) storages.keySet();
    }
}
