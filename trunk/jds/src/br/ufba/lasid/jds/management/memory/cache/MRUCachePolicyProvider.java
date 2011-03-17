/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.cache;

import br.ufba.lasid.jds.management.JDSConfigurator;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class MRUCachePolicyProvider implements ICachePolicyProvider{

    public ICachePolicy create(Properties options) throws Exception {
        
        Properties defaultProperties = JDSConfigurator.Options;
        
        String maxSize = options.getProperty( JDSConfigurator.MaximumCacheSize,
                                              defaultProperties.getProperty(JDSConfigurator.MaximumCacheSize));
        
        return new MRUCachePolicy(Integer.parseInt(maxSize));

    }

}
