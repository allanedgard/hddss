/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management;

import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public final class JDSConfigurator{

    public static final String             MaximumPageSize = "MaximumPageSize";
    public static final String                  PageOffset = "PageOffset";
    public static final String                   PageIndex = "PageIndex";
    public static final String                   PageBytes = "PageBytes";
    public static final String                PageProvider = "PageProvider";
    public static final String      VolatileMemoryProvider = "VolatileMemoryProvider";
    public static final String    PersistentMemoryProvider = "PersistentMemoryProvider";
    public static final String                    Filename = "Filename";
    public static final String              FileAccessMode = "FileAccessMode";
    public static final String            MaximumCacheSize = "MaximumCacheSize";
    public static final String               CacheProvider = "CacheProvider";
    public static final String         CachePolicyProvider = "CachePolicyProvider";
    public static final String       VolatileStateProvider = "VolatileStateProvider";
    public static final String     PersistentStateProvider = "PersistentStateProvider";
    public static final String         PersistentStorageID = "PersistentStorageID";
    public static final String                   StampSize = "StampSize";
    public static final String         BTreeStructureOrder = "BTreeStructureOrder";
    public static final String  BTreeStructureMaximumDepth = "BTreeStructureMaximumDepth";

    public static final Properties Options = new Properties();

    static{
        
        
        Options.put( JDSConfigurator.MaximumPageSize,
                            "4096");

        Options.put( JDSConfigurator.PageOffset,
                            "0");

        Options.put( JDSConfigurator.PageIndex,
                            "0");

        Options.put( JDSConfigurator.PageBytes,
                            "");

        Options.put( JDSConfigurator.Filename,
                            "_persistent_memory");
        Options.put( JDSConfigurator.FileAccessMode,
                            "rw");
        Options.put( JDSConfigurator.PageProvider,
                            "br.ufba.lasid.jds.management.memory.pages.PageProvider");

        Options.put( JDSConfigurator.VolatileMemoryProvider,
                            "br.ufba.lasid.jds.management.memory.VolatileMemoryProvider");

        Options.put( JDSConfigurator.PersistentMemoryProvider,
                            "br.ufba.lasid.jds.management.memory.FileSystemBasedPersistentMemoryProvider");

        Options.put( JDSConfigurator.MaximumCacheSize,
                            "256");

        Options.put( JDSConfigurator.CacheProvider,
                            "br.ufba.lasid.jds.management.memory.cache.CacheProvider");

        Options.put( JDSConfigurator.CachePolicyProvider,
                            "br.ufba.lasid.jds.management.memory.cache.MRUCachePolicyProvider");

        Options.put( JDSConfigurator.VolatileStateProvider,
                            "br.ufba.lasid.jds.management.state.VolatileStateProvider");

        Options.put( JDSConfigurator.PersistentStateProvider,
                            "br.ufba.lasid.jds.management.state.PersistentStateProvider");

        Options.put( JDSConfigurator.PersistentStorageID,
                            "_persistent_storage");

        Options.put( JDSConfigurator.StampSize, "33");
        
        Options.put( JDSConfigurator.BTreeStructureOrder,
                            "256");

        Options.put( JDSConfigurator.BTreeStructureMaximumDepth,
                            "4");

    }


    public static <T> T create(String providername) throws Exception{

        return (T)create(providername, new Properties(JDSConfigurator.Options));
    }

    public static <T> T create(String providername, Properties options) throws Exception{

         Properties ioptions = new Properties(JDSConfigurator.Options);
         ioptions.putAll(options);

        String provider = ioptions.getProperty(providername);

        Class _class = Class.forName(provider);

        IProvider factory =  (IProvider) _class.newInstance();

        return (T) factory.create(ioptions);
    }

//    public static <T> T create(String name) throws Exception{
//        return (T)create(name, JDSConfigurator.Options);
//    }
//
//    public static <T> T create(String name, Properties options) throws Exception{
//        Properties ioptions = new Properties(JDSConfigurator.Options);
//
//        ioptions.putAll(options);
//
//        Class factory = getFactory(name, options);
//
//        return (T) factory.getMethod("create", Properties.class).invoke(null, options);
//    }
//
//    protected static Class getFactory(String name, Properties options) throws Exception{
//
//        Properties ioptions = new Properties(JDSConfigurator.Options);
//
//        ioptions.putAll(options);
//
//        String _class = ioptions.getProperty(name);
//
//        return Class.forName(_class);
//    }


}
