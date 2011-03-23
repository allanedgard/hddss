/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.state.managers;

import br.ufba.lasid.jds.management.IProvider;
import br.ufba.lasid.jds.util.JDSUtility;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class RecoverableStateManagerProvider implements IProvider<IRecovarableStateManager>{

    public IRecovarableStateManager create(Properties options) throws Exception {

        Properties initOptions  = new Properties(JDSUtility.Options);
        
        initOptions.putAll(options);

        IStateManager sm = JDSUtility.create( JDSUtility.BaseStateManagerProvider,
                                              initOptions );
        RecoverableStateManager rsm = new RecoverableStateManager(sm);

        rsm.setOptions(initOptions);
        return rsm;
    }

}
