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
public class FileMemoryProvider implements IPersistentMemoryProvider{

    public IPersistentMemory create(Properties options) throws Exception {
        
        IPersistentMemory memory; String fname; String mode; String psize;

        Properties defaultOptions = JDSUtility.Options;
        
        fname = options.getProperty( JDSUtility.Filename,
                                     defaultOptions.getProperty(JDSUtility.Filename));

        mode  = options.getProperty( JDSUtility.FileAccessMode,
                                     defaultOptions.getProperty(JDSUtility.FileAccessMode));

        psize  = options.getProperty( JDSUtility.MaximumPageSize,
                                     defaultOptions.getProperty(JDSUtility.MaximumPageSize));

        
        memory = new FileMemory(fname, mode);

        memory.setPageSize(Long.parseLong(psize));
        memory.setOptions(options);
        
        return memory;
    }

}
