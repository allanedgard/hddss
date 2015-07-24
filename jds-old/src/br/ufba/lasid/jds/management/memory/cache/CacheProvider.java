/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.cache;

import br.ufba.lasid.jds.util.JDSUtility;
import br.ufba.lasid.jds.management.memory.IPersistentMemory;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class CacheProvider implements ICacheProvider{

    public ICache create(Properties options) throws Exception {

       Properties ioptions = new Properties(JDSUtility.Options);
       
       ioptions.putAll(options);
       
       String maxCacheSize = ioptions.getProperty( JDSUtility.MaximumCacheSize);

        IPersistentMemory pm = JDSUtility.create(JDSUtility.PersistentMemoryProvider, options);
        ICachePolicy      cp = JDSUtility.create(JDSUtility.CachePolicyProvider, options);
                
        ICache cache = new Cache(pm, cp);
        
        cache.setMaxSize(Integer.parseInt(maxCacheSize));

        return cache;
        
    }

    

}
