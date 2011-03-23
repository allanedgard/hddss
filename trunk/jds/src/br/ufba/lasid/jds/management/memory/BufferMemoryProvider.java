/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory;

import br.ufba.lasid.jds.util.JDSUtility;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class BufferMemoryProvider implements IVolatileMemoryProvider{

    public IVolatileMemory create(Properties options) throws Exception {

        IVolatileMemory memory; String psize;

        Properties defaultOptions = JDSUtility.Options;
        

        psize  = options.getProperty( JDSUtility.MaximumPageSize,
                                     defaultOptions.getProperty(JDSUtility.MaximumPageSize));


        memory = new BufferMemory();

        memory.setPageSize(Long.parseLong(psize));
        memory.setOptions(options);

        return memory;
    }

}
