/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state;

import br.ufba.lasid.jds.util.XObject;
import java.math.BigInteger;

/**
 *
 * @author aliriosa
 */
public class StateVariable extends StateChunk{
    public StateVariable(long offset, long length) {
        super(offset, length);
    }

    public StateVariable(StateChunk chunk){
        super(chunk.getOffset(), chunk.getLength());
    }

}
