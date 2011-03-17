/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory;

import br.ufba.lasid.jds.management.JDSConfigurator;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class BufferArrayVolatileMemoryProvider implements IVolatileMemoryProvider{

    public IVolatileMemory create(Properties options) throws Exception {

        IVolatileMemory memory; String psize;

        Properties defaultOptions = JDSConfigurator.Options;
        

        psize  = options.getProperty( JDSConfigurator.MaximumPageSize,
                                     defaultOptions.getProperty(JDSConfigurator.MaximumPageSize));


        memory = new BufferArrayVolatileMemory();

        memory.setPageSize(Long.parseLong(psize));
        memory.setOptions(options);

        return memory;
    }

}
