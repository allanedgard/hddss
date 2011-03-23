/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.trash.state;

import br.ufba.lasid.jds.management.memory.IVolatileMemory;
import br.ufba.lasid.jds.util.JDSUtility;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class VolatileStateProvider implements IVolatileStateProvider{

    public IVolatileState create(Properties options) throws Exception {
        Properties ioptions = new Properties(JDSUtility.Options);
        ioptions.putAll(options);

        IVolatileMemory vm = JDSUtility.create(JDSUtility.VolatileMemoryProvider, ioptions);
        BaseStateManager bsm = new BaseStateManager();
        
        return new VolatileState(bsm, vm);
    }

}
