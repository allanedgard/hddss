/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.jbft.pbft.executors;

import br.ufba.lasid.jds.IProcess;
import br.ufba.lasid.jds.comm.PDU;
import br.ufba.lasid.jds.comm.Quorum;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.cs.IServer;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTCommit;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTPrePrepare;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTReply;
import br.ufba.lasid.jds.jbft.pbft.comm.PBFTRequest;
import br.ufba.lasid.jds.util.IPayload;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.ufba.lasid.jds.jbft.pbft.PBFT;
import br.ufba.lasid.jds.jbft.pbft.PBFTServer;
import br.ufba.lasid.jds.jbft.pbft.util.PBFTLogEntry;
import br.ufba.lasid.jds.util.Debugger;
import br.ufba.lasid.jds.util.StatedPBFTRequestMessage;

/**
 *
 * @author aliriosa
 */
public class PBFTDoerExecutor extends PBFTCollectorServant<PBFTCommit>{


    public PBFTDoerExecutor(){

    }

    public PBFTDoerExecutor(PBFT p){
        setProtocol(p);
    }


    /**
     * Collect the request sent by the client.
     * @param commit -- the client request.
     */
    protected synchronized boolean accept(PBFTCommit commit){

        PBFTServer pbft = (PBFTServer)getProtocol();

        try{
            long startSEQ = pbft.getNextExecuteSEQ();
            long finalSEQ = commit.getSequenceNumber();

            for(long currSEQ = startSEQ; currSEQ <= finalSEQ; currSEQ++ ){

                PBFTLogEntry entry = pbft.getStateLog().get(currSEQ);

                if(entry == null){

                    return false;

                }

                PBFTPrePrepare preprepare = entry.getPrePrepare();

                if(preprepare == null){

                    return false;

                }

                Quorum pq = entry.getPrepareQuorum();

                if(pq == null) return false;
                if(!pq.complete()) return false;

                Quorum cq = entry.getCommitQuorum();
                if(cq == null) return false;
                if(!cq.complete()) return false;

                for(String digest : preprepare.getDigests()){

                    StatedPBFTRequestMessage statedReq = pbft.getStateLog().getStatedRequest(digest);

                    PBFTRequest request = statedReq.getRequest();

                    if(!statedReq.getState().equals(StatedPBFTRequestMessage.RequestState.COMMITTED)){
                        Debugger.debug(
                          "[PBFTDoerExecutor] s"  + pbft.getLocalServerID()   +
                          ", at time " + pbft.getClock().value() + ", couldn't " +
                          "execute " + request +
                          " because it hasn't been committed yet in current view " +
                          "(currView = " + pbft.getCurrentViewNumber() + ")"
                        );

                        return false;
                    }

                }//end for each digest (check committed)

                for(String digest : preprepare.getDigests()){

                    StatedPBFTRequestMessage statedReq = pbft.getStateLog().getStatedRequest(digest);

                    PBFTRequest request = statedReq.getRequest();

                    IServer server = pbft.getServer();

                    IPayload result = server.doService(request.getPayload());


                    PBFTReply reply = createReplyMessage(request, result);

                    statedReq.setState(StatedPBFTRequestMessage.RequestState.SERVED);
                    statedReq.setReply(reply);

                    Debugger.debug(
                      "[PBFTDoerExecutor] s"  + pbft.getLocalServerID()       +
                      ", at time " + pbft.getClock().value() + ", executed " + request +
                      " (currView = " + pbft.getCurrentViewNumber() + ")"
                    );

                    emit(reply);

                }//end for each digest (execute and reply)

                pbft.updateNextExecuteSEQ(currSEQ);
                
            }//end for each seqn

            return true;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;

    }

    protected PBFTReply createReplyMessage(PBFTRequest r, IPayload result){
        
        PBFTServer pbft = (PBFTServer)getProtocol();

        return createReplyMessage(r, result, pbft.getCurrentViewNumber());

    }

    protected PBFTReply createReplyMessage(PBFTRequest r, IPayload result, Integer viewNumber){

        PBFTServer pbft = (PBFTServer)getProtocol();

        PBFTReply reply = new PBFTReply(
            r, result, pbft.getLocalServerID(), viewNumber
        );

        return reply;

    }

    public synchronized void emit(PBFTReply reply){
        PBFTServer pbft = (PBFTServer)getProtocol();

        SignedMessage m;

        try {

            m = pbft.getAuthenticator().encrypt(reply);

            IProcess theClient  = new br.ufba.lasid.jds.Process(reply.getClientID());
            IProcess thisServer  = pbft.getLocalProcess();

            PDU pdu = new PDU();
            pdu.setSource(thisServer);
            pdu.setDestination(theClient);
            pdu.setPayload(m);

            pbft.getCommunicator().unicast(pdu, theClient);

            Debugger.debug(
              "[PBFTDoerExecutor]s" +  thisServer.getID() +
              " sent  " + reply + " at timestamp " + pbft.getClock().value() +
              " to c" + theClient.getID() + "."
            );


        } catch (Exception ex) {
            Logger.getLogger(PBFTDoerExecutor.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }


    public boolean canConsume(Object object) {
        return (object instanceof PBFTCommit);
    }

    @Override
    public void execute(){

        while(true){
            try{

                PBFTCommit m =
                        (PBFTCommit)getInbox().remove();

                accept(m);
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
    }


}

