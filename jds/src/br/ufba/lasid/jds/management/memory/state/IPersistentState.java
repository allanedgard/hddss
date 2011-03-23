/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.state;

import br.ufba.lasid.jds.management.memory.state.IState;

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
public interface IPersistentState extends IState{

}
