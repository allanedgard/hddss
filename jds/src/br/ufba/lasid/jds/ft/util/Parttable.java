/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.ft.util;

import br.ufba.lasid.jds.ft.util.PartTree.PartEntry;
import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class Parttable extends Hashtable<Long, PartTree.PartEntry> implements IParttable{

    public PartEntry get(Long recid) throws Exception {
        return super.get(recid);
    }

}
