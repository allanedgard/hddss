/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.trash.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author aliriosa
 */
public class BaseStateManager implements IStateManager{
    
    public static final StateChunkOffsetBasedComparator byOffsetComparator =  new StateChunkOffsetBasedComparator();
    public static final StateChunkSizeBasedComparator   byLengthComparator =  new StateChunkSizeBasedComparator();
    
    private FreeStateChunkTable ftable = new FreeStateChunkTable(byLengthComparator);
    private VariableTable table = new VariableTable();
    protected IState state;

    public StateChunk malloc(long size) throws Exception {
        
        StateChunk chunk = new StateChunk(this.getState().getLength(), size);

        StateChunk free = getFreeSpaceTable().ceiling(chunk);

        if(free != null){

            chunk.setOffset(free.getOffset());

            long remain = free.getLength() - chunk.getLength();

            if(remain <= 0){
                getFreeSpaceTable().remove(free);
            }
        }
        return chunk;
    }

    public void free(StateChunk chunk) throws Exception {
        if(chunk == null){
            return;
        }

        if(!getFreeSpaceTable().contains(chunk)){
            getFreeSpaceTable().add(chunk);
        }

        ArrayList<StateChunk> chunks = new ArrayList<StateChunk>();

        chunks.addAll(getFreeSpaceTable());

        Collections.sort(chunks, byOffsetComparator);
        Iterator<StateChunk> itr = chunks.iterator();

        StateChunk prev = null;
        StateChunk curr = itr.next();

        while(itr.hasNext()){
            if(prev != null){
                long peoffset = prev.getOffset() + prev.getLength();
                long coffset = curr.getOffset();

                while(curr != null && peoffset == coffset){

                    prev.setLength(prev.getLength() + curr.getLength());
                    getFreeSpaceTable().remove(curr);
                    curr = itr.next();

                    if(curr!= null){
                        peoffset = prev.getOffset() + prev.getLength();
                        coffset = curr.getOffset();
                    }
                }
            }

            prev = curr;
            curr = itr.next();

        }
    }

    public StateVariable setVariable(Object variableID, Object variableValue, int size) throws Exception {
        
        StateVariable variable = getVariableTable().get(variableID);
       
        if(variable == null){

            StateChunk chunk = malloc(size);

            variable = new StateVariable(chunk);

            table.put(variableID, variable);
        }

        return getVariableTable().put(variableID, variable);
    }

    public StateVariable getVariable(Object variableID) throws Exception {
        return getVariableTable().get(variableID);
    }

    public void remove(Object variableID) throws Exception {
        StateVariable variable = getVariableTable().remove(variableID);
        free(variable);
    }

    public FreeStateChunkTable getFreeSpaceTable() {
        return ftable;
    }

    public void setFreeSpaceTable(FreeStateChunkTable ftable) {
        this.ftable = ftable;
    }

    public VariableTable getVariableTable() {
        return table;
    }

    public void setVariableTable(VariableTable table) {
        this.table = table;
    }

    public IState getState() throws Exception{
        return this.state;
    }

    public void setState(IState state) throws Exception{
        this.state = state;
    }

}
