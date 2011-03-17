/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.pages;

import br.ufba.lasid.jds.management.IProvider;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public interface IPageProvider extends IProvider<IPage>{
    public IPage create(Properties options) throws Exception;
}
