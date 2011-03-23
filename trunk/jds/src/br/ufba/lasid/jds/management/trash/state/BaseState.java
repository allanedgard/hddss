/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.trash.state;

import br.ufba.lasid.jds.management.memory.IMemory;
import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.pages.PageIndexList;
import br.ufba.lasid.jds.util.XObject;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class BaseState implements IState{

    protected IMemory memory;
    
//    public static final StateChunkOffsetBasedComparator byOffsetComparator =  new StateChunkOffsetBasedComparator();
//    public static final StateChunkSizeBasedComparator   byLengthComparator =  new StateChunkSizeBasedComparator();

//    private FreeStateChunkTable ftable = new FreeStateChunkTable(byLengthComparator);
//    private VariableTable table = new VariableTable();
    private IStateManager manager;
    private Properties options = new Properties();

    public BaseState(IStateManager manager, IMemory memory) throws Exception{
        this.memory = memory;
        this.manager = manager;
        this.manager.setState(this);
        
    }
    
    public Object get(Object variableID) throws Exception {



        Object value = null;
        if(variableID != null){
            StateVariable variable = manager.getVariable(variableID);

            if(variable != null){
                byte[] buffer = new byte[(int)variable.getLength()];
                seek(variable.getOffset());
                read(buffer, 0, buffer.length);
                value = XObject.byteArrayToObject(buffer);
            }
        }
        return value;
    }

    public void put(Object variableID, Object variableValue) throws Exception {
        if(variableID!= null && variableValue != null){

            byte[] bytes = XObject.objectToByteArray(variableValue);
            
            StateVariable variable =  manager.setVariable(variableID, variableValue, bytes.length);

            seek(variable.getOffset());

            write(bytes, 0, (int)variable.getLength());
        }
        
    }
    
    public void remove(Object variableID) throws Exception {
        manager.remove(variableID);
    }

    public void write(byte[] buffer, int offset, int length) throws Exception {
        this.memory.write(buffer, offset, length);
    }

    public int read(byte[] buffer, int offset, int length) throws Exception {
        return this.memory.read(buffer, offset, length);
    }

    public IPage readPage(long index) throws Exception {
        return this.memory.readPage(index);
    }

    public void writePage(IPage page) throws Exception {
        this.memory.writePage(page);
    }

    public void seek(long position) throws Exception {
        this.memory.seek(position);
    }

    public long getPosition() throws Exception {
        return this.memory.getCurrentOffset();
    }

    public long getLength() throws Exception {
        return this.memory.getCurrentAllocatedSize();
    }

    public void setLength(long newLength) throws Exception {
        this.memory.setLength(newLength);
    }

    public long getCurrentNumberOfPages() throws Exception {
        return this.memory.getCurrentNumberOfPages();
    }

    public PageIndexList getRecentlyModifiedPageIndexes() throws Exception{
        return this.memory.getRecentlyModifiedPageIndexes();
    }

    public Properties getOptions() throws Exception {
        return this.options;
    }

    public void setOptions(Properties options) throws Exception {
        this.options.putAll(options);
    }

    public IMemory getMemory() throws Exception{
        return this.memory;
    }


}
