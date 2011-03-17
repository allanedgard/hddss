/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTServerMessage;
import java.util.Comparator;

/**
 *
 * @author aliriosa
 */
public class PBFTServerMessageSequenceNumberComparator implements Comparator<IMessage>{

    public int compare(IMessage o1, IMessage o2) {
        PBFTServerMessage m1 = (PBFTServerMessage)o1;
        PBFTServerMessage m2 = (PBFTServerMessage)o2;
        Long s1 = m1.getSequenceNumber();
        Long s2 = m2.getSequenceNumber();
        return s1.compareTo(s2);
    }

}
