/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.jbft.pbft.comm.util;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import java.util.Comparator;

/**
 *
 * @author aliriosa
 */
public class PBFTMessageSequenceComparator implements Comparator<PBFTMessage>{

    public int compare(PBFTMessage m1, PBFTMessage m2){
        return -1; //m1.compareTo(m2);
    }


}
