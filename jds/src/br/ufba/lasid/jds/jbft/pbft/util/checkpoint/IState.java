/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util.checkpoint;

/**
 *
 * @author aliriosa
 */
/**
 * Represents an abstraction for a service state. This abstraction represents the
 * state as set of pairs &lt;VariableID; VariableValue&gt;. The application pro-
 * grammer is responsable for determine those variables are going to belong to
 * the application state. The state application must be enoungth for a replica
 * recorevy the required data during a recovering process.
 * @author aliriosa
 */
public interface IState <VariableID, VariableValue>{

    /**
     * Define a value for a state variable.
     * @param ID - the variable ID (e.g. index or name).
     * @param value - the variable value.
     */
    public void set(VariableID ID, VariableValue value);

    /**
     * Retrieve a variable value from the state.
     * @param ID - the variable ID (e.g. index or name).
     * @return - if the variable was defined then it'll return the variable
     * value, otherwise it'll return null.
     */
    public VariableValue get(VariableID ID);

}

