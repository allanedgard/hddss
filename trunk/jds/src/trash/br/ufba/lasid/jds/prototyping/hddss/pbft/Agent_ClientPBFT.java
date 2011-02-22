/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trash.br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.jbft.pbft.PBFTClient;

/**
 *
 * @author aliriosa
 */
public class Agent_ClientPBFT extends Agent_PBFT {
    protected PBFTClient pbft ;
    protected String numberGeneratorAlgorithm = "";
    protected String keyGeneratorAlgorithm = "";
    protected String encryptationAlgorithm = "";
    protected String provider = "";
    protected String timeout = "0";
    /*

    public String getKeyGeneratorAlgorithm() { return keyGeneratorAlgorithm;  }

    public void setKeyGeneratorAlgorithm(String keyGeneratorAlgorithm) {
        this.keyGeneratorAlgorithm = keyGeneratorAlgorithm;
    }

    public String getNumberGeneratorAlgorithm() { return numberGeneratorAlgorithm; }

    public void setNumberGeneratorAlgorithm(String numberGeneratorAlgorithm) {
        this.numberGeneratorAlgorithm = numberGeneratorAlgorithm;
    }

    public String getSecurityProvider() { return provider;    }

    public void setSecurityProvider(String provider) {
        this.provider = provider;
    }

    public String getEncryptationAlgorithm() {
        return encryptationAlgorithm;
    }

    public void setEncryptationAlgorithm(String encryptationAlgorithm) {
        this.encryptationAlgorithm = encryptationAlgorithm;
    }



    
    @Override
    public void setup() {

        
        pbft = new PBFTClient();
        try {
            
            
            pbft.setAuthenticator(
                    new MessageAuthenticator(
                            getNumberGeneratorAlgorithm(),
                            getKeyGeneratorAlgorithm(),
                            getEncryptationAlgorithm(),
                            getSecurityProvider()
                    )
            );

            pbft.setCommunicator(this);
            pbft.setRetransmissionTimeout(getTimeout());
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Agent_ClientPBFT.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(Agent_ClientPBFT.class.getName()).log(Level.SEVERE, null, ex);
        }

        super.setup();
        getProtocol().addExecutor(CreateRequestAction.class, newCreateRequestExecutor());
        getProtocol().addExecutor(SendRequestAction.class, newSendRequestExecutor());
        getProtocol().addExecutor(SendRequestAction.class, newScheculeRequestRetransmissionExecutor());
        getProtocol().addExecutor(ReceiveReplyAction.class, newReceiveReplyExecutor());
        getProtocol().addExecutor(RetransmiteRequestAction.class, newRetransmiteRequestExecutor());
    }


    public void receiveResult(Payload content) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Executor newScheculeRequestRetransmissionExecutor(){
        return new PBFTScheculeRequestRetransmissionExecutor(getProtocol());
    }
    public Executor newCreateRequestExecutor() {
        return new PBFTCreateRequestExecutor(getProtocol());
    }

    public Executor newSendRequestExecutor() {
        return new PBFTSendRequestExecutor(getProtocol());
    }

    public Executor newReceiveReplyExecutor() {
        return new PBFTReceiveReplyExecutor(getProtocol());
    }

    public Executor newRetransmiteRequestExecutor(){
        return new PBFTRetransmiteRequestExecutor(getProtocol());
    }
    public void setClientRetransmissionTimeout(String timeout){
        this.timeout = timeout;
    }

    public Long getTimeout(){
        return new Long(timeout);
    }

    public void asyncCall(Payload payload) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Payload synchCall(Payload payload) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    */
}
