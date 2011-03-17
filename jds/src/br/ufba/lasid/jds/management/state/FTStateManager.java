/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state;

import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class FTStateManager extends PersistentStateManager{

    public FTStateManager(Properties options) {
        super(options);
    }

    @Override
    public void download(boolean hasVolatileStateData) throws Exception {
        super.download(true);
    }

    @Override
    public void fillup(boolean hasVolatileStateData) throws Exception {
        super.fillup(true);
    }

    @Override
    public void checkpoint(long checkpointID, boolean execDownloadData) throws Exception {
        super.checkpoint(checkpointID, true);
    }

    @Override
    public void checkpointData(long checkpointID, boolean execDownloadData) throws Exception {
        super.checkpointData(checkpointID, true);
    }

    @Override
    public void rollback(long checkpointID, boolean execRollbackData) throws Exception {
        super.rollback(checkpointID, true);
    }

    @Override
    public void rollbackData(long checkpointID, boolean execRollbackData) throws Exception {
        super.rollbackData(checkpointID, true);
    }

}
