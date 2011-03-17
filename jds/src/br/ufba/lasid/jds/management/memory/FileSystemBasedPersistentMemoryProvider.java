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
public class FileSystemBasedPersistentMemoryProvider implements IPersistentMemoryProvider{

    public IPersistentMemory create(Properties options) throws Exception {
        
        IPersistentMemory memory; String fname; String mode; String psize;

        Properties defaultOptions = JDSConfigurator.Options;
        
        fname = options.getProperty( JDSConfigurator.Filename,
                                     defaultOptions.getProperty(JDSConfigurator.Filename));

        mode  = options.getProperty( JDSConfigurator.FileAccessMode,
                                     defaultOptions.getProperty(JDSConfigurator.FileAccessMode));

        psize  = options.getProperty( JDSConfigurator.MaximumPageSize,
                                     defaultOptions.getProperty(JDSConfigurator.MaximumPageSize));

        
        memory = new FileSystemBasedPersistentMemory(fname, mode);

        memory.setPageSize(Long.parseLong(psize));
        memory.setOptions(options);
        
        return memory;
    }

}
