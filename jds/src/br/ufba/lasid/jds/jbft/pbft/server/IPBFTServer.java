/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.server;

import br.ufba.lasid.jds.adapters.IEventListener;
import br.ufba.lasid.jds.comm.MessageQueue;
import br.ufba.lasid.jds.cs.IServer;
import br.ufba.lasid.jds.jbft.pbft.IPBFT;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTBag;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTChangeViewACK;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCheckpoint;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTFetch;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTMetaData;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTNewView;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTProcessingToken;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTStatusActive;
import java.lang.reflect.Method;

/**
 *
 * @author aliriosa
 */
public interface IPBFTServer extends IPBFT{
  /*##############################################################
   * 0. Server Modes.
   ###############################################################*/

    public static final int   STARTING = 0;
    public static final int    RUNNING = 1;
    public static final int OVERLOADED = 2;
    public static final int   CHANGING = 3;

  /*##############################################################
   * 1. Execution Types.
   ###############################################################*/
    
   public static final int       REQUEST = 0;
   public static final int    PREPREPARE = 1;
   public static final int       PREPARE = 2;
   public static final int        COMMIT = 3;
   public static final int         REPLY = 4;
   public static final int  STATUSACTIVE = 5;
   public static final int           BAG = 6;
   public static final int    CHECKPOINT = 7;
   public static final int         FETCH = 8;
   public static final int      METADATA = 9;
   public static final int          DATA = 10;
   public static final int    CHANGEVIEW = 11;
   public static final int CHANGEVIEWACK = 12;
   public static final int       NEWVIEW = 13;
   public static final int STATUSPENDING = 14;
   public static final int         TOKEN = 15;

  /*##############################################################
   * 2. Event Types.
   ###############################################################*/
   public static final int    ARRIVE = 0;
   public static final int DEPARTURE = 1;

  /*##############################################################
   * 3. Methods for handling pbft server messages.
   ###############################################################*/
    public void handle(PBFTRequest        rq );
    public void handle(PBFTPrePrepare     ppr);
    public void handle(PBFTPrepare        pr );
    public void handle(PBFTCommit         cm );
    public void handle(PBFTStatusActive   sta);
    public void handle(PBFTFetch          ft );
    public void handle(PBFTMetaData       mdt);
    public void handle(PBFTData           dt );
    public void handle(PBFTCheckpoint     ck );
    public void handle(PBFTChangeView     cv );
    public void handle(PBFTChangeViewACK  cva);
    public void handle(PBFTNewView        nwv);
    public void handle(PBFTBag            bg );
    public void handle(PBFTProcessingToken tk);

  /*##############################################################
   * 4. Utility Methods.
   ###############################################################*/

    public MessageQueue getQueue(String name);

    public long getCurrentPrePrepareSEQ();
    public long getCurrentExecuteSEQ();
    public long getCurrentPrepareSEQ();
    public long getCurrentCommitSEQ();
    public long getSlidingWindowSize();

    public Object getLocalServerID();

    public void emitChangeView();
    public void installNewView(PBFTNewView nv);

    public int getServiceBFTResilience();
    
    public void setCheckpointPeriod(long period);
    public void setCheckpointFactor(Long factor);
    public void setBatchSize(int bsize);
    public void setRejuvenationWindow(long rwindow);
    public void setBatchTimeout(Long btimeout);
    public void setChangeViewRetransmissionTimeout(long cvtimeout);
    //public void setCurrentPrimaryID(Object pid);
    public void setPrimaryFaultTimeout(Long pftimeout);
    public void setCurrentViewNumber(Integer viewn);
    public void setSendStatusPeriod(long ssperiod);
    public void setSlidingWindowSize(Long swsize);

    public Integer getCurrentViewNumber();

    public void loadState();
    public void schedulePeriodicStatusSend();
    public void emitFetch();

    public void setServer(IServer server);
    public IServer getServer();
    public void addListener(IEventListener listener, Method m);

    public boolean changing();
    public boolean running();
    public boolean overloaded();
    public boolean starting();

}
