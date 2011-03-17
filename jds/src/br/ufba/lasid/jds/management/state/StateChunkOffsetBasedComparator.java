/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state;

import java.util.Comparator;

/**
 *
 * @author aliriosa
 */
public class StateChunkOffsetBasedComparator implements Comparator<StateChunk>{

    public int compare(StateChunk o1, StateChunk o2) {
        Long off1 = o1.getOffset();
        Long off2 = o2.getOffset();

        return off1.compareTo(off2);
    }

}
