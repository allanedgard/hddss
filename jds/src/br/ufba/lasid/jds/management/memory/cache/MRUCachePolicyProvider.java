/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.cache;

import br.ufba.lasid.jds.util.JDSUtility;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class MRUCachePolicyProvider implements ICachePolicyProvider{

    public ICachePolicy create(Properties options) throws Exception {
        
        Properties defaultProperties = JDSUtility.Options;
        
        String maxSize = options.getProperty( JDSUtility.MaximumCacheSize,
                                              defaultProperties.getProperty(JDSUtility.MaximumCacheSize));
        
        return new MRUCachePolicy(Integer.parseInt(maxSize));

    }

}
