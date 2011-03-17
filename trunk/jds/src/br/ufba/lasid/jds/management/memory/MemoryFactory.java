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
public class MemoryFactory {

    public static IMemory create() throws Exception{

        return create(new Properties(JDSConfigurator.Options));
    }

    public static IMemory create(Properties options) throws Exception{

         Properties ioptions = new Properties(JDSConfigurator.Options);
         ioptions.putAll(JDSConfigurator.Options);
         ioptions.putAll(options);

        String provider = ioptions.getProperty(
                              JDSConfigurator.VolatileMemoryProvider,
                              JDSConfigurator.Options.getProperty(
                                JDSConfigurator.VolatileMemoryProvider
                              )
                           );

        Class _class = Class.forName(provider);

        IMemoryProvider factory =  (IMemoryProvider) _class.newInstance();

        return factory.create(ioptions);
    }


}
