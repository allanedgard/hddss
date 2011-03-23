/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.state.managers;

import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.state.IState;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public interface IStateManager extends IState{

    public void   setCurrentState(IState state) throws Exception;
    public IState getCurrentState() throws Exception;

    public void    setCurrentState(byte[] buf) throws Exception;
    
    public IMemory bufferMemory() throws Exception;

    public void setOptions(Properties options);

    public Properties getOptions();

    public byte[] byteArray() throws Exception;



}
