/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

import br.ufba.lasid.jds.management.IProvider;
import java.io.PrintStream;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public final class JDSUtility{

    public static final String                 MaximumPageSize = "MaximumPageSize";
    public static final String                      PageOffset = "PageOffset";
    public static final String                       PageIndex = "PageIndex";
    public static final String                       PageBytes = "PageBytes";
    public static final String                    PageProvider = "PageProvider";
    public static final String          VolatileMemoryProvider = "VolatileMemoryProvider";
    public static final String        PersistentMemoryProvider = "PersistentMemoryProvider";
    public static final String                        Filename = "Filename";
    public static final String                  FileAccessMode = "FileAccessMode";
    public static final String                MaximumCacheSize = "MaximumCacheSize";
    public static final String                   CacheProvider = "CacheProvider";
    public static final String             CachePolicyProvider = "CachePolicyProvider";
    public static final String           VolatileStateProvider = "VolatileStateProvider";
    public static final String         PersistentStateProvider = "PersistentStateProvider";
    public static final String             PersistentStorageID = "PersistentStorageID";
    public static final String                       StampSize = "StampSize";
    public static final String             BTreeStructureOrder = "BTreeStructureOrder";
    public static final String      BTreeStructureMaximumDepth = "BTreeStructureMaximumDepth";
    public static final String          DRStateManagerProvider = "DRStateManagerProvider";
    public static final String RecovarableStateManagerProvider = "RecovarableStateManagerProvider";
    public static final String        BaseStateManagerProvider = "BaseStateManagerProvider";

    public static final Properties Options = new Properties();
    public static PrintStream out = System.out;

    static{
        
        
        Options.put( JDSUtility.MaximumPageSize,
                            "4096");

        Options.put( JDSUtility.PageOffset,
                            "0");

        Options.put( JDSUtility.PageIndex,
                            "0");

        Options.put( JDSUtility.PageBytes,
                            "");

        Options.put( JDSUtility.Filename,
                            "_persistent_memory");
        Options.put( JDSUtility.FileAccessMode,
                            "rw");
        Options.put( JDSUtility.PageProvider,
                            "br.ufba.lasid.jds.management.memory.pages.PageProvider");

        Options.put( JDSUtility.VolatileMemoryProvider,
                            "br.ufba.lasid.jds.management.memory.BufferMemoryProvider");

        Options.put( JDSUtility.PersistentMemoryProvider,
                            "br.ufba.lasid.jds.management.memory.FileMemoryProvider");

        Options.put( JDSUtility.MaximumCacheSize,
                            "256");

        Options.put( JDSUtility.CacheProvider,
                            "br.ufba.lasid.jds.management.memory.cache.CacheProvider");

        Options.put( JDSUtility.CachePolicyProvider,
                            "br.ufba.lasid.jds.management.memory.cache.MRUCachePolicyProvider");

        Options.put( JDSUtility.VolatileStateProvider,
                            "br.ufba.lasid.jds.management.state.VolatileStateProvider");

        Options.put( JDSUtility.PersistentStateProvider,
                            "br.ufba.lasid.jds.management.state.PersistentStateProvider");

        Options.put( JDSUtility.PersistentStorageID,
                            "_persistent_storage");

        Options.put( JDSUtility.StampSize, "33");
        
        Options.put( JDSUtility.BTreeStructureOrder,
                            "256");

        Options.put( JDSUtility.BTreeStructureMaximumDepth,
                            "4");

        Options.put(  JDSUtility.RecovarableStateManagerProvider,
                      "br.ufba.lasid.jds.management.memory.state.managers.RecoverableStateManagerProvider");

        Options.put(  JDSUtility.BaseStateManagerProvider,
                      "br.ufba.lasid.jds.management.memory.state.managers.StateManagerProvider");


    }


    public static <T> T create(String providername) throws Exception{

        return (T)create(providername, new Properties(JDSUtility.Options));
    }

    public static <T> T create(String providername, Properties options) throws Exception{

         Properties ioptions = new Properties(JDSUtility.Options);
         ioptions.putAll(options);

        String provider = ioptions.getProperty(providername);

        Class _class = Class.forName(provider);

        IProvider factory =  (IProvider) _class.newInstance();

        return (T) factory.create(ioptions);
    }

    public static boolean debug = true;

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        JDSUtility.debug = debug;
    }

    public synchronized static void debug(String txt){
        //if(txt.matches("[s4]") || txt.matches("[p4]") || txt.matches("[ID=4]"))
        if(isDebug())  out.println(txt);
    }


//    public static <T> T create(String name) throws Exception{
//        return (T)create(name, JDSUtility.Options);
//    }
//
//    public static <T> T create(String name, Properties options) throws Exception{
//        Properties ioptions = new Properties(JDSUtility.Options);
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
//        Properties ioptions = new Properties(JDSUtility.Options);
//
//        ioptions.putAll(options);
//
//        String _class = ioptions.getProperty(name);
//
//        return Class.forName(_class);
//    }


}
