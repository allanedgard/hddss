/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.trash.state;

import br.ufba.lasid.jds.management.memory.IVolatileMemory;
import br.ufba.lasid.jds.util.JDSUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

/**
 *
 * @author aliriosa
 */
public class TestStateManager{
    /*size of chunk counter*/
    final byte  SZCOUNTER =  8;
    final byte OFFCOUNTER =  0;

    /*size of chunk peaces */
    final byte     SZCHUNKTYPE =  1; //chunk type
    final byte SZCHUNKHASHCODE =  4; //chunk hash code
    final byte   SZCHUNKHDSIZE =  8; //chunk data size in main memory
    final byte SZCHUNKHDOFFSET =  8; //chunk data offset in main memory

    /* size of a chunk */
    final byte         SZCHUNK = SZCHUNKTYPE + SZCHUNKHASHCODE + SZCHUNKHDSIZE + SZCHUNKHDOFFSET;

    /*size of chunk peaces */
    //offset of chunk type (byte0)
    final byte     OFFCHUNKTYPE =  0;
    //offset of chunk hash code = OFFCHUNKTYPE + SZCHUNKTYPE = 0 + 1 = 1
    final byte OFFCHUNKHASHCODE =  OFFCHUNKTYPE + SZCHUNKTYPE;
    //offset of chunk data size = OFFCHUNKHASHCODE + SZCHUNKHASHCODE = 1 + 4 = 5
    final byte   OFFCHUNKHDSIZE =  OFFCHUNKHASHCODE + SZCHUNKHASHCODE; //chunk data size in main memory
    //offset of chunk data offset = OFFCHUNKHDSIZE + SZCHUNKHDSIZE = 5 + 8 = 13
    final byte OFFCHUNKHDOFFSET =  OFFCHUNKHDSIZE + SZCHUNKHDSIZE; //chunk data offset in main memory

    final byte OFFFIRSTCHUNK = SZCOUNTER;

    final byte  FREE = 0;
    final byte INUSE = 1;


    public static final StateChunkOffsetBasedComparator byOffsetComparator =  new StateChunkOffsetBasedComparator();
    public static final StateChunkSizeBasedComparator   byLengthComparator =  new StateChunkSizeBasedComparator();

    private FreeStateChunkTable ftable = new FreeStateChunkTable(byLengthComparator);
    private VariableTable table = new VariableTable();
    protected IState state;

    protected IVolatileMemory internal;

    public TestStateManager() throws Exception{

        Properties p = new Properties();

        p.setProperty(JDSUtility.MaximumPageSize, "4096");

        this.internal = JDSUtility.create(JDSUtility.VolatileMemoryProvider, p);
        this.internal.seek(0);
        byte[] buf = new byte[4096 * SZCHUNK];
        
        this.internal.graft(buf);

    }

    private byte[] HANDLE(int hashcode, int size) throws Exception{

        int HASHCODE = hashcode;

        long NCHUNKS = GETNCHUNKS();

        int ICHUNK = (HASHCODE & 0x7FFFFFFF) % (int)NCHUNKS;
        
        byte[] CURRCHUNK = NEWCHUNK();        

        if(NCHUNKS > 0){

            //int ICHUNK = 0;
            
            final long ALLOCATEDSPACE = CALCALLOCATEDSPACE(NCHUNKS);

            int        IFREECHUNK = -1;
            long SZFREECHUNKSPACE = -1L;

            boolean FOUND = false;
            
            for(ICHUNK = OFFFIRSTCHUNK; ICHUNK < ALLOCATEDSPACE; ICHUNK += SZCHUNK){
                
                internal.seek(ICHUNK);
                internal.read(CURRCHUNK, 0, SZCHUNK);

                byte CHUNKTYPE = GETCHUNKTYPE(CURRCHUNK);

                if(CHUNKTYPE == FREE){
                    long CHUNKSPACE = GETCHUNKSPACE(CURRCHUNK);
                    if((SZFREECHUNKSPACE < 0 || CHUNKSPACE < SZFREECHUNKSPACE) && CHUNKSPACE >=size ){
                              IFREECHUNK  = ICHUNK;
                        SZFREECHUNKSPACE = CHUNKSPACE;
                    }//end if has a free chunk that is good
                }//end if chunk is free

                if(CHUNKTYPE == INUSE){
                    int HASHCODECHUNK = GETCHUNKHASHCODE(CURRCHUNK);
                    if(HASHCODECHUNK == HASHCODE){
                        FOUND = true;
                        break;
                    }//end if found the hash code
                }//end if chunk in use
                
            }//end for each chunk

            if(!FOUND){                
                if(IFREECHUNK >= OFFFIRSTCHUNK){
                    internal.seek(IFREECHUNK);
                    internal.read(CURRCHUNK);

                    SETCHUNKTYPE    (CURRCHUNK, INUSE   );
                    SETCHUNKHASHCODE(CURRCHUNK, HASHCODE);
                }
            }                        
        }

        return CURRCHUNK;
    }
    
    private long CALCALLOCATEDSPACE(long nchunks){
        return OFFFIRSTCHUNK + nchunks * SZCHUNK;
    }

    private long GETNCHUNKS() throws Exception{

        byte[] BUFFER = new byte[SZCOUNTER];

        internal.seek(OFFCOUNTER);

        int read = internal.read(BUFFER, 0, SZCOUNTER);
        
        if(read < SZCOUNTER)
            return 0;

        return  longValue(BUFFER, 0);
    }

    private byte[] NEWCHUNK(){//byte[] chunk = new byte[SZCHUNK]; bzeros(chunk, SZCHUNK); return chunk;
        return new byte[SZCHUNK]; 
    }

    private void SETCHUNKTYPE(byte[] chunk, byte type){
        byteCopy(type, chunk, (int)OFFCHUNKTYPE);
    }
   
    private byte GETCHUNKTYPE(byte[] chunk){
        return byteValue(chunk, (int)OFFCHUNKTYPE);
    }

    private int GETCHUNKHASHCODE(byte[] chunk){
        return intValue (chunk, (int)OFFCHUNKHASHCODE);
    }

    private int SETCHUNKHASHCODE(byte[] chunk, int hascode){
        return intValue (chunk, (int)OFFCHUNKHASHCODE);
    }


    private long GETCHUNKSPACE(byte[] chunk){
        return longValue(chunk, (int)OFFCHUNKHDOFFSET);
    }

    
    void bzeros(byte[] b, int len){
        for(int i=0; i < len; i++) b[i] = 0;
    }
    public void byteCopy(byte val, byte[] b, int off){
        b[off + 0] = val;
    }
    
    byte byteValue(byte[] b, int off){
        return b[off+0];
    }
    public void intCopy(int val, byte[] b, int off){
        b[off+3] = (byte)((val       ) & 0xFF);
        b[off+2] = (byte)((val >>>  8) & 0xFF);
        b[off+1] = (byte)((val >>> 16) & 0xFF);
        b[off+0] = (byte)((val >>> 24) & 0xFF);
    }

    int intValue(byte[] b, int off){
        int rint = ((int)b[off+0] << 24) +
                   ((int)b[off+1] << 16) +
                   ((int)b[off+2] <<  8) + b[off+3];

        return rint;        
    }
    
    long longValue(byte[] b){
        return longValue(b, 0);
    }
    public void longCopy(long val, byte[] b, int off){
        b[off+7] = (byte)((val       ) & 0xFF);
        b[off+6] = (byte)((val >>>  8) & 0xFF);
        b[off+5] = (byte)((val >>> 16) & 0xFF);
        b[off+4] = (byte)((val >>> 24) & 0xFF);
        b[off+3] = (byte)((val >>> 32) & 0xFF);
        b[off+2] = (byte)((val >>> 40) & 0xFF);
        b[off+1] = (byte)((val >>> 48) & 0xFF);
        b[off+0] = (byte)((val >>> 56) & 0xFF);
    }

    long longValue(byte[] b, int off){
        long rlong = ((long)b[off+0] << 56) +
                     ((long)b[off+1] << 48) +
                     ((long)b[off+2] << 40) +
                     ((long)b[off+3] << 32) +
                     ((long)b[off+4] << 24) +
                     ((long)b[off+5] << 16) +
                     ((long)b[off+6] <<  8) + b[off+7];

        return rlong;
    }

    public StateChunk malloc(long size) throws Exception {

        StateChunk chunk = new StateChunk(this.getState().getLength(), size);

        StateChunk free = ceiling(chunk);

        if(free != null){

            chunk.setOffset(free.getOffset());

            long remain = free.getLength() - chunk.getLength();

            if(remain <= 0){
                getFreeSpaceTable().remove(free);
            }else{
                free.setLength(remain);
                free.setOffset(free.getOffset() + chunk.getLength());
            }
        }
        return chunk;
    }

    protected StateChunk ceiling(StateChunk chunk){

        StateChunk free = getFreeSpaceTable().ceiling(chunk);

        return free;
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

    public void test(StateChunk chunk){
        chunk.setOffset(10);
    }
    public static void main(String[] args) throws Exception{
//        StateChunk chunk = new StateChunk(0, 10);
//        TestStateManager t = new TestStateManager();
//        System.out.println(chunk.getOffset());
//        t.test(chunk);
//        System.out.println(chunk.getOffset());

        int val = 0;
        byte b3 = (byte)((val       ) & 0xFF);
        byte b2 = (byte)((val >>>  8) & 0xFF);
        byte b1 = (byte)((val >>> 16) & 0xFF);
        byte b0 = (byte)((val >>> 24) & 0xFF);

        System.out.println("b3=" +b3);
        System.out.println("b2=" +b2);
        System.out.println("b1=" +b1);
        System.out.println("b0=" +b0);
    }


    
}
