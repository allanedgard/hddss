/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.pages;

import br.ufba.lasid.jds.management.JDSConfigurator;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class PageProvider implements IPageProvider{
    
    public IPage create(Properties options) throws Exception{
        IPage page; String maxPageSize; String offset; String index; String bytes;

        Properties defaultOptions = JDSConfigurator.Options;

        maxPageSize = options.getProperty( JDSConfigurator.MaximumPageSize,
                                           defaultOptions.getProperty(JDSConfigurator.MaximumPageSize));

        offset = options.getProperty( JDSConfigurator.PageOffset,
                                      defaultOptions.getProperty(JDSConfigurator.PageOffset) );

        index = options.getProperty( JDSConfigurator.PageIndex,
                                     defaultOptions.getProperty(JDSConfigurator.PageIndex) );
        
        bytes = options.getProperty( JDSConfigurator.PageBytes, "");

        page = new BasePage(Long.parseLong(maxPageSize));
        page.setIndex(Long.parseLong(index));
        page.setOffset(Long.parseLong(offset));
        page.setBytes(bytes.getBytes(), bytes.getBytes().length);

        return page;

    }

}
