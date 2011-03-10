/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.architectures;

import br.ufba.lasid.hdf.connectors.IConnector;
import br.ufba.lasid.hdf.IConsumer;
import br.ufba.lasid.hdf.ISupplier;
import br.ufba.lasid.hdf.connectors.OneToManyConnector;
import br.ufba.lasid.hdf.connectors.OneToOneConnector;
import br.ufba.lasid.hdf.util.ConnectorList;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class Architecture {
    protected boolean shutdown = false;
    Hashtable<String, ISupplier> sptable = new Hashtable<String, ISupplier>();
    Hashtable<String, IConsumer> cntable = new Hashtable<String, IConsumer>();
    Hashtable<String, Thread>    thtable = new Hashtable<String, Thread>();
    
    Hashtable<String, ArrayList<String>> xtable =
            new Hashtable<String, ArrayList<String>>();
    
    ConnectorList connectors = new ConnectorList();

    public void add(String name, Object obj){
        if(obj instanceof ISupplier) addSupplier(name, (ISupplier)obj);        
        if(obj instanceof IConsumer) addConsumer(name, (IConsumer)obj);
        if(obj instanceof Thread)    addThread(name, (Thread)obj);
    }
    
    public void addThread(String name, Thread thr){
        thtable.put(name, thr);
    }
    public void addSupplier(String name, ISupplier supplier){
        sptable.put(name, supplier);
    }

    public void addConsumer(String name, IConsumer consumer){
        cntable.put(name, consumer);
    }
    
    public void connect(String suppliername, String consumername){

        ISupplier supplier = sptable.get(suppliername);
        IConsumer consumer = cntable.get(consumername);

        if(supplier != null && consumer != null){
            ArrayList<String> consumers = xtable.get(suppliername);

            if(consumers == null){
                consumers = new ArrayList<String>();
                xtable.put(suppliername, consumers);
            }

            consumers.add(consumername);
        }
    }

    public void buildup(){
        for(String skey : xtable.keySet()){
            ArrayList<String> consumers = xtable.get(skey);
            IConnector connector = null;

            if(consumers != null ){                
                if(consumers.size() == 1){                    
                    connector = new OneToOneConnector();
                    connector.connect(
                            sptable.get(skey),
                            cntable.get(consumers.get(0))
                    );                    
                }else{
                    if(consumers.size() > 1){
                        connector = new OneToManyConnector();                        
                        connector.connectTo(sptable.get(skey));
                        for(String ckey : consumers){
                            connector.connectTo(cntable.get(ckey));
                        }
                    }
                }
                connectors.add(connector);
            }
        }

        //startup();
    }

    public void startup(){
        buildup();
        connectors.start();

        for(Thread t : thtable.values()){
            if(!t.isAlive()){
                t.start();
                
            }
        }
        
    }

    public void shutdown(){
        for(ISupplier s : sptable.values()){

            if(s instanceof Thread){
                //((Thread)s).interrupt();
                ((Thread)s).stop();
            }
        }

        connectors.disconnect();

        for(IConsumer c : cntable.values()){

            if(c instanceof Thread){
                //((Thread)c).interrupt();
                ((Thread)c).stop();
            }
        }


        
        connectors.stop();
        connectors.clear();

        castoff();
    }

    public void castoff(){
        
        sptable.clear();
        cntable.clear();

        for(String xkey : xtable.keySet()){
            xtable.get(xkey).clear();
        }

        xtable.clear();

        shutdown = true;
    }

}
