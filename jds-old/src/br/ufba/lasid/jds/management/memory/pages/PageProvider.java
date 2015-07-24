/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.pages;

import br.ufba.lasid.jds.util.JDSUtility;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class PageProvider implements IPageProvider{
    
    public IPage create(Properties options) throws Exception{
        IPage page; String maxPageSize; String offset; String index; String bytes;

        Properties defaultOptions = JDSUtility.Options;

        maxPageSize = options.getProperty( JDSUtility.MaximumPageSize,
                                           defaultOptions.getProperty(JDSUtility.MaximumPageSize));

        offset = options.getProperty( JDSUtility.PageOffset,
                                      defaultOptions.getProperty(JDSUtility.PageOffset) );

        index = options.getProperty( JDSUtility.PageIndex,
                                     defaultOptions.getProperty(JDSUtility.PageIndex) );
        
        bytes = options.getProperty( JDSUtility.PageBytes, "");

        page = new BasePage(Long.parseLong(maxPageSize));
        page.setIndex(Long.parseLong(index));
        page.setOffset(Long.parseLong(offset));
        page.setBytes(bytes.getBytes(), bytes.getBytes().length);

        return page;

    }

}
