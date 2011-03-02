/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMessage;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;

/**
 *
 * @author aliriosa
 */
public interface IPBFTServer{

    public void collect(PBFTMessage     message     );

    public void handle(PBFTRequest      request     );
    public void handle(PBFTPrePrepare   preprepare  );
    public void handle(PBFTPrepare      prepare     );
    public void handle(PBFTCommit       commit      );
    public void handle(PBFTStatusActive statusActive);

}
