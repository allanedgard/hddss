/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author aliriosa
 */
public class CheckpointKeyComparator implements Comparator<String>, Serializable{
    private static final long serialVersionUID = 2515624548186918337L;

    public int compare(String o1, String o2) {
        String seqn1 = o1.split(";")[0];
        String seqn2 = o2.split(";")[0];

        Long s1 = Long.valueOf(seqn1);
        Long s2 = Long.valueOf(seqn2);

        return s1.compareTo(s2);
    }

}
