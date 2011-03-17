/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state;

import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.JDSConfigurator;
import br.ufba.lasid.jds.management.memory.IVolatileMemory;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class VolatileState extends BaseState implements IVolatileState{

    
    public VolatileState(IStateManager manager, IVolatileMemory memory) throws Exception {
        super(manager, memory);
    }

    public VolatileState(Properties options) throws Exception {
        super(
           new BaseStateManager(),
          (IMemory)JDSConfigurator.create(JDSConfigurator.VolatileMemoryProvider, options)
        );
    }    

}
