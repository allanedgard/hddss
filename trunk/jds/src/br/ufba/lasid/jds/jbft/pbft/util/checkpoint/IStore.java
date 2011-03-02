/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util.checkpoint;

import java.io.IOException;
import jdbm.helper.Tuple;

/**
 *
 * @author aliriosa
 */
public interface IStore<Index, Data> {
    public void write(Index index, Data data, boolean replace) throws IOException;
    public Data read(Index index) throws IOException;
    public void commit() throws IOException;
    public Tuple findGreaterOrEqual(Index index) throws IOException;
    public Tuple getLast() throws IOException;
}
