/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IState;
import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IStore;
import br.ufba.lasid.jds.util.Debugger;
import java.io.IOException;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.MRU;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import jdbm.recman.CacheRecordManager;

/**
 *
 * @author aliriosa
 */
public class PBFTCheckpointStorage implements IStore<String, IState> {

    RecordManager recman;
    BTree tree;

    public PBFTCheckpointStorage(Object replicaID) throws IOException{
        String database  = "checkpoint@replica" + replicaID;
        String btreename = "__checkpoint__btree";

        recman = new CacheRecordManager(RecordManagerFactory.createRecordManager(database), new MRU(3));

        long recid = recman.getNamedObject(btreename);



        if(recid    != 0){
            tree = BTree.load(recman, recid);
            Debugger.debug("[PBFTCheckpointStorage]Reloading existing B+Tree with " + tree.size() + " records");
        }else{
            tree = BTree.createInstance(recman, new CheckpointKeyComparator());
            
            recman.setNamedObject(btreename, tree.getRecid());
            Debugger.debug("[PBFTCheckpointStorage]Reloading existing B+Tree with " + tree.size() + " records");
        }
        
    }
    
    public synchronized  void write(String index, IState data, boolean replace) throws IOException {
        tree.insert(index, data, replace);
    }

    public synchronized IState read(String index) throws IOException{
        return (IState)tree.find(index);
    }

    public synchronized void commit() throws IOException{
        recman.commit();
        //TupleBrowser browser = tree.browse(null);
        //Tuple tuple = new Tuple();
        //browser.getPrevious(tuple);
        //System.out.println("LAST TREE NODE = <KEY = " + tuple.getKey() + "|#| VALUE = " + tuple.getValue() + ">");
    }

    public synchronized Tuple findGreaterOrEqual(String index) throws IOException{
        return tree.findGreaterOrEqual(index);
    }

    public synchronized Tuple getLast() throws IOException{
        Tuple tuple = null;

        if(tree.size() > 0){
            
            TupleBrowser browser = tree.browse(null);
            
            if(browser != null){
                tuple = new Tuple(); browser.getPrevious(tuple);
            }
        }
        
        return tuple;
    }

}
