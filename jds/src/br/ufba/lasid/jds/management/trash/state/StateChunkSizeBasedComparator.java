/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.trash.state;

import java.util.Comparator;

/**
 *
 * @author aliriosa
 */
public class StateChunkSizeBasedComparator implements Comparator<StateChunk>{

    public int compare(StateChunk o1, StateChunk o2) {
        Long l1 = o1.getLength();
        Long l2 = o2.getLength();

        return l1.compareTo(l2);
    }
}
