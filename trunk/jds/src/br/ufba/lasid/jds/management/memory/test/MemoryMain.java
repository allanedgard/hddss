/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.test;

import br.ufba.lasid.jds.management.JDSConfigurator;
import br.ufba.lasid.jds.management.memory.cache.ICache;
import br.ufba.lasid.jds.management.memory.pages.IPage;
import java.util.*;

/**
 *
 * @author aliriosa
 */
public class MemoryMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{


        Properties options = new Properties();
        
        options.setProperty(JDSConfigurator.Filename, "Cache.cc");
        options.setProperty(JDSConfigurator.MaximumPageSize, "8");
        options.setProperty(JDSConfigurator.MaximumCacheSize, "3");

        //ICache cache = BaseCacheFactory.create(options);
        ICache cache = JDSConfigurator.create(JDSConfigurator.CacheProvider, options);
        String s = "Hello World!!!!";
        cache.write(s.getBytes());
        byte[] bytes = new byte[3];
        
        //cache.seek(0);
        IPage p = cache.readPage(0);
        System.out.println("read data: '" + new String(p.getBytes(),0, (int)p.getSize()) + "'");
//        cache.read(bytes, 0, bytes.length);
//
//        System.out.println("read data: '" + new String(bytes,0, bytes.length) + "'");
//        System.out.println("pages: '" + cache.getCurrentNumberOfPages() + "'");
//
//        System.out.println("s: '" + s +"', length: " + s.length());
//
//        //s += new String(new byte[3]);
//
//        //System.out.println("s: '" + s +"', length: " + s.length());
//
//        String mem = s;
//
//        System.out.println("mem: '" + mem +"', length: " + mem.length());
//        s = "Hello";
//        long mlength = mem.length();
//        long moffset = mem.length();
//        long eoffset = moffset + s.length();
//        if(eoffset > mlength){
//            eoffset = mlength;
//        }
//        mem = mem.substring(0, (int)moffset) + s + mem.substring((int)eoffset, mem.length());
//
//        System.out.println("mem: '" + mem +"', length: " + mem.length());
//
        
        
/**
 * Test memory
 */
/*        IMemory main = BaseMemoryFactory.create(options);

        for(int i = 0; i < 100000; i++){
            
            main.write("Alo Mundo!!!\n".getBytes());
        }        

        IPage page = main.readPage(0);

        System.out.println("read:" + new String(page.getBytes(), 0, (int)page.getSize()));
        final String s = "Etcha!!!!!!!";
        
        page.setBytes(s.getBytes(), s.length());

        main.writePage(page);
        System.out.println("Total number of pages:" + main.getCurrentNumberOfPages());
        System.out.println("Modified Pages:" );
        String more = "";
        for(long index : main.getRecentlyModifiedPageIndexes()){
            System.out.print(more + index);
            more = ",";
        }

*/
        //CachedMemory cache = new CachedMemory(main, null, 0);
        
    }

}
