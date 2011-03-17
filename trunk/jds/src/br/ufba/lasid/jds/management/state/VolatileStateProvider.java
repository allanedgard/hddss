/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state;

import br.ufba.lasid.jds.management.JDSConfigurator;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class VolatileStateProvider implements IVolatileStateProvider{

    public IVolatileState create(Properties options) throws Exception {
        Properties ioptions = new Properties(JDSConfigurator.Options);
        ioptions.putAll(options);
        //ioptions.put(JDSConfigurator.hasAVolatileStateMemory, "true");
        return new VolatileState(ioptions);
    }

}
