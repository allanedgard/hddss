/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.jbft.pbft.util.checkpoint.IState;

/**
 *
 * @author aliriosa
 */
public class PBFTData extends PBFTServerMessage{

    protected IState data;
    protected String digest;

    public PBFTData(Long sequenceNumber, String digest, IState data) {
        this(sequenceNumber, digest, data, null);
    }

    public PBFTData(Long sequenceNumber, String digest, IState data, Object replicaID) {
        setData(data);
        setDigest(digest);
        setSequenceNumber(sequenceNumber);
        setReplicaID(replicaID);
    }

    public IState getData() {
        return data;
    }

    public void setData(IState data) {
        this.data = data;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    
    @Override
    public String toString() {
        return (
                "<DATA" + ", " +
                 "SEQUENCE = " + getSequenceNumber() + ", " +
                 "DIGEST = " + getDigest() + ", " +
                 "STATE = " + getData() + ", " +
                 "SENDER = " + getReplicaID() + ", " +
                 ">"
        );
    }

}
