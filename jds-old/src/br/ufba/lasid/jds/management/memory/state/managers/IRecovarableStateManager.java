/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.state.managers;

import br.ufba.lasid.jds.ft.util.CheckpointLogEntry;
import br.ufba.lasid.jds.ft.util.PartList;
import br.ufba.lasid.jds.ft.util.PartTree.PartEntry;
import br.ufba.lasid.jds.management.memory.IMemory;

/**
 *
 * @author aliriosa
 */
public interface IRecovarableStateManager extends IStateManager{

    public void checkpoint(long checkpointID) throws Exception;
    public void rollback  (long checkpointID) throws Exception;

    public void rollback  () throws Exception;
    public void checkpoint() throws Exception;

    public long getCurrentCheckpointID();
    public void setCurrentCheckpointID(long checkpointID);

    public String getCurrentValidationString();
    public void   setCurrentValidationString(String validationString);

    public void setObjectStorageID(String osid);
    public String getObjecStorageID();

    public void snapshot(IMemory input, String storageout, long ipage) throws Exception;

    public boolean existsLogEntry(long checkpointID);
    public void addLogEntry(CheckpointLogEntry clogEntry);
    public CheckpointLogEntry getLogEntry(long checkpointID);
    public void removeLogEntry(long checkpointID);
    public CheckpointLogEntry getBiggestLogEntry();
    
//    public PartList getPartsGreaterOrEqualThen(long checkpointID) throws Exception;
//    public PartList getPartsGreaterOrEqualThen(long checkpointID, long level, long ipart) throws Exception;
    public PartEntry getPart(long lpart, long ipart) throws Exception;
    public PartList getSubparts(long lpart, long ipart) throws Exception;
    public PartList getFamily(long lpart, long ipart, long minage) throws Exception;

    public PartEntry put(Long recid, PartEntry value) throws Exception;


    public int getParttreeLevels();
    public int getParttreeOrder();

    public IMemory getDataStorage(String dstorageID) throws Exception;
    public IMemory getObjecStorage() throws Exception;




}
