/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.cache;

import br.ufba.lasid.jds.management.memory.IMemoryProvider;
import br.ufba.lasid.jds.management.JDSConfigurator;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class CacheProvider implements ICacheProvider{

    public ICache create(Properties options) throws Exception {
       Properties defaultOptions = JDSConfigurator.Options;
       
       String   mProvider = options.getProperty( JDSConfigurator.PersistentMemoryProvider,
                                defaultOptions.getProperty(JDSConfigurator.PersistentMemoryProvider));

       String    pProvider = options.getProperty( JDSConfigurator.CachePolicyProvider,
                                defaultOptions.getProperty(JDSConfigurator.CachePolicyProvider));

       String maxCacheSize = options.getProperty( JDSConfigurator.MaximumCacheSize,
                                defaultOptions.getProperty(JDSConfigurator.MaximumCacheSize));

        
        IMemoryProvider      mp = (    IMemoryProvider  ) Class.forName(mProvider).newInstance();
        ICachePolicyProvider pp = (ICachePolicyProvider ) Class.forName(pProvider).newInstance();
                
        ICache cache = new Cache(mp.create(options), pp.create(options));
        
        cache.setMaxSize(Integer.parseInt(maxCacheSize));
        
        return cache;
        
    }

    

}
