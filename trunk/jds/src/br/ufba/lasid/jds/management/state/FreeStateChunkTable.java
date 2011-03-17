/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state;

import java.util.Comparator;
import java.util.TreeSet;

/**
 *
 * @author aliriosa
 */
public class FreeStateChunkTable extends TreeSet<StateChunk>{

    public FreeStateChunkTable(Comparator<? super StateChunk> comparator) {
        super(comparator);
    }

}
