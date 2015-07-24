/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.state;

import br.ufba.lasid.jds.management.memory.storages.IStorage;
import java.util.Set;

/**
 * An abstract representation of the application state. The application state
 * is represented by the values of its internal variables stored in a volatile
 * memory as well as data stored in persistent storage devices.
 * 
 * The internal variables and data, named state variables and persistent data,
 * determined how the application will behave (processing) when it eventually
 * receive new inputs.
 * 
 * <P>
 * At runtime the applications are responsible by promoting state transitions,
 * that means to create, redefine or free variables or to read, write or rewrite
 * persistent data. 
 * 
 * State variables, unlike the other ones, can remain between processing rounds
 * of a same application context. An application context can be defined
 * Thus,
 * the state can be seen as a an abstraction for the internal memory of an
 * application.
 *
 * @author Alirio Sá.
 */
public interface IState<Value> {

    /**
     * Sets the value of a state variable or create a new variable with the 
     * specified value if it such variable doesn't exist.
     * 
     * @param variable - the variable identifier.
     * @param value - the variable value.
     * @throws Exception - If an error occurs.
     */
    public void set(String variable, Value value) throws Exception;
    /**
     * Gets the value of a state variable.
     * 
     * @param variable - the variable identifier.
     * @return - the variable value or null if the variable doesn't exist.
     * @throws Exception - If an error occurs.
     */
    public Value get(String variable) throws Exception;

    /**
     * Gets a storage of persistent data
     * @param storageID - the storage identifier
     * @return the request storage, or null if it doesn't exist.
     */
    public IStorage getStorage(String storageID);

    public void connect(String storageID, IStorage storage);
    public void disconnect(String storageID);

    public Set<String> getStorateIDSet();

}
