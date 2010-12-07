/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.Action;
import br.ufba.lasid.jds.DistributedProtocol;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.actions.ExecuteCheckPointAction;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.util.Buffer;

/**
 *
 * @author aliriosa
 */
public class PBFTCheckStateExecutor extends PBFTServerExecutor{

    public PBFTCheckStateExecutor(DistributedProtocol protocol) {
        super(protocol);
    }

    @Override
    public synchronized void execute(Action act) {
        //super.execute(act);
        PBFTMessage checkpoint = (PBFTMessage) act.getWrapper();

        Buffer cmBuffer = ((PBFT)getProtocol()).getCommittedBuffer();
        Buffer cpBuffer = ((PBFT)getProtocol()).getCheckpointBuffer();

        if(((PBFT)getProtocol()).isValidSequenceNumber(checkpoint)){
            if(((PBFT)getProtocol()).gotQuorum(checkpoint)){

                System.out.println(
                    "server [p" + getProtocol().getLocalProcess().getID() + "] " 
                  + "is goint to ckeckpoint the state sequence number "
                  + checkpoint.get(PBFTMessage.SEQUENCENUMBERFIELD)
                 );
                
                getProtocol().perform(new ExecuteCheckPointAction(checkpoint));

            }
        }
/*
        Collections.sort(
            buffer, (Comparator)(new PBFTMessageSequenceComparator())
        );

        for(Object item : buffer){
            PBFTMessage m = (PBFTMessage) item;
            System.out.println(
                "server [p" + getProtocol().getLocalProcess().getID() + "] " +
                "sequence " + m.get(PBFTMessage.SEQUENCENUMBERFIELD)
             );
        }
 * 
 */
    }

}
