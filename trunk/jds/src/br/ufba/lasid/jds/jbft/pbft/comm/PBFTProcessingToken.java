/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

/**
 *
 * @author aliriosa
 */
public class PBFTProcessingToken extends PBFTServerMessage{

    public PBFTProcessingToken(Integer viewNumber, Long sequenceNumber){
        setViewNumber(viewNumber);
        setSequenceNumber(sequenceNumber);
    }

    @Override
    public final String toString() {

        return (
                "<PROC-TOKEN" + ", " +
                 "VIEW = " + getViewNumber().toString() + ", " +
                 "SEQUENCE = " + getSequenceNumber().toString() +
                 ">"
        );
    }
}
