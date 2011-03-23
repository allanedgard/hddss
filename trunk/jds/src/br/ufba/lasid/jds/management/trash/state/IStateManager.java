/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.trash.state;

/**
 *
 * @author aliriosa
 */
public interface IStateManager {

    public StateChunk malloc(long size) throws Exception;
    public void free(StateChunk chunk) throws Exception;
    public StateVariable setVariable(Object variableID, Object variableValue, int size) throws Exception;
    public StateVariable getVariable(Object variableID) throws Exception;
    public void remove(Object variableID) throws Exception;
    public IState getState() throws Exception;
    public void setState(IState state) throws Exception;


}
