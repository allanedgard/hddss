/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.trash.state;

import br.ufba.lasid.jds.management.memory.IVolatileMemory;

/**
 *
 * @author aliriosa
 */
public class VolatileState extends BaseState implements IVolatileState{

    
    public VolatileState(IStateManager manager, IVolatileMemory memory) throws Exception {
        super(manager, memory);
    }

}
