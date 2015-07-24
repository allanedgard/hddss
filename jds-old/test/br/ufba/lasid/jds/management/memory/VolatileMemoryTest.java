/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory;

import br.ufba.lasid.jds.management.memory.pages.IPage;
import br.ufba.lasid.jds.management.memory.pages.PageIndexList;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliriosa
 */
public class VolatileMemoryTest {

    public VolatileMemoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of read method, of class BufferMemory.
     */
    @Test
    public void testRead_3args() throws Exception {
        System.out.println("VolatileMemory.read(buffer, offset, length)");
        byte[] buffer = new byte[8];
        int offset = 0;
        int length = 8;
        BufferMemory instance = new BufferMemory();

        assertEquals(-1, instance.read(buffer, offset, length));
         String s = "Alo Mundo!!!!";
         
        instance.seek(0);
        instance.write("Alo Mundo!!!!".getBytes());

        assertEquals(-1, instance.read(buffer, offset, length));

        instance.seek(0);

        assertEquals(8, instance.read(buffer, offset, length));

        System.out.println("Read: " + new String(buffer, 0, buffer.length));

    }

    /**
     * Test of read method, of class BufferMemory.
     */
    @Test
    public void testRead_byteArr() throws Exception {
        System.out.println("VolatileMemory.read(buffer)");

        byte[] buffer = new byte[10];
        BufferMemory instance = new BufferMemory();
        int expResult = (int)instance.getCurrentOffset() + 10;

        if(expResult > instance.getCurrentAllocatedSize()){
            expResult = -1;
        }else{
            expResult = 10;
        }
        int result = instance.read(buffer);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of readPage method, of class BufferMemory.
     */
    @Test
    public void testReadPage() throws Exception {
        System.out.println("VolatileMemory.readPage(index)");
        long pageindex = 0L;
        BufferMemory instance = new BufferMemory();
        IPage expResult = null;       
        IPage result = instance.readPage(pageindex);

        if(instance.getCurrentNumberOfPages() > 0){
            assertNotNull(expResult);
            System.out.println(new String(result.getBytes(), 0,  result.getBytes().length));
        }else{
            assertNull(result);
        }
        
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of write method, of class BufferMemory.
     */
    @Test
    public void testWrite_3args() throws Exception {
        System.out.println("write");
        byte[] buffer = null;
        int offset = 0;
        int length = 0;
        BufferMemory instance = new BufferMemory();
        instance.write(buffer, offset, length);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of write method, of class BufferMemory.
     */
    @Test
    public void testWrite_byteArr() throws Exception {
        System.out.println("write");
        byte[] buffer = null;
        BufferMemory instance = new BufferMemory();
        instance.write(buffer);
        // TODO review the generated test code and remove the default call to fail.
       // fail("The test case is a prototype.");
    }

    /**
     * Test of writePage method, of class BufferMemory.
     */
    @Test
    public void testWritePage() throws Exception {
        System.out.println("writePage");
        IPage page = null;
        BufferMemory instance = new BufferMemory();
        instance.writePage(page);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of seek method, of class BufferMemory.
     */
    @Test
    public void testSeek() throws Exception {
        System.out.println("seek");
        long offset = 33L;
        BufferMemory instance = new BufferMemory();
        instance.seek(offset);

        //assertEquals(, this);
        
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getCurrentOffset method, of class BufferMemory.
     */
    @Test
    public void testGetCurrentOffset() throws Exception {
        System.out.println("getCurrentOffset");
        BufferMemory instance = new BufferMemory();
        long expResult = 0L;
        long result = instance.getCurrentOffset();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getCurrentAllocatedSize method, of class BufferMemory.
     */
    @Test
    public void testGetCurrentAllocatedSize() throws Exception {
        System.out.println("getCurrentAllocatedSize");
        BufferMemory instance = new BufferMemory();
        instance.setLength(10);
        long expResult = 10L;
        long result = instance.getCurrentAllocatedSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getCurrentNumberOfPages method, of class BufferMemory.
     */
    @Test
    public void testGetCurrentNumberOfPages() throws Exception {
        System.out.println("getCurrentNumberOfPages");
        BufferMemory instance = new BufferMemory();
        long expResult = 10L;
        instance.setPageSize(10);
        instance.setLength(100);
        long result = instance.getCurrentNumberOfPages();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getPageSize method, of class BufferMemory.
     */
    @Test
    public void testGetPageSize() throws Exception {
        System.out.println("getPageSize");
        BufferMemory instance = new BufferMemory();
        long expResult = 8L;
        long result = instance.getPageSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setPageSize method, of class BufferMemory.
     */
    @Test
    public void testSetPageSize() throws Exception {
        System.out.println("setPageSize");
        long newPageSize = 3L;
        BufferMemory instance = new BufferMemory();
        instance.setPageSize(newPageSize);
        assertEquals(instance.getPageSize(), newPageSize);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getRecentlyModifiedPageIndexes method, of class BufferMemory.
     */
    @Test
    public void testGetRecentlyModifiedPageIndexes() throws Exception {
        System.out.println("getRecentlyModifiedPageIndexes");
        BufferMemory instance = new BufferMemory();
        PageIndexList expResult = null;
        PageIndexList result = instance.getRecentlyModifiedPageIndexes();
        assertNotNull(result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of clearListOfModifiedPages method, of class BufferMemory.
     */
    @Test
    public void testClearListOfModifiedPages() throws Exception {
        System.out.println("clearListOfModifiedPages");
        BufferMemory instance = new BufferMemory();
        instance.clearListOfModifiedPages();
        // TODO review the generated test code and remove the default call to fail.
        assertEquals(instance.getRecentlyModifiedPageIndexes().size(), 0);
    }

    /**
     * Test of setLength method, of class BufferMemory.
     */
    @Test
    public void testSetLength() throws Exception {
        System.out.println("setLength");
        long newSize = 10L;
        BufferMemory instance = new BufferMemory();
        instance.setLength(newSize);
        assertEquals(10, instance.getCurrentAllocatedSize());
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setOptions method, of class BufferMemory.
     */
    @Test
    public void testSetOptions() {
        System.out.println("setOptions");
        Properties options = null;
        BufferMemory instance = new BufferMemory();
        instance.setOptions(options);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getOptions method, of class BufferMemory.
     */
    @Test
    public void testGetOptions() {
        System.out.println("getOptions");
        BufferMemory instance = new BufferMemory();
        Properties expResult = new Properties();
        Properties result = instance.getOptions();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getPageIndex method, of class BufferMemory.
     */
    @Test
    public void testGetPageIndex() throws Exception {
        System.out.println("getPageIndex");
        long offset = 0L;
        BufferMemory instance = new BufferMemory();
        long expResult = 0L;
        long result = instance.getPageIndex(offset);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getOffsetInPage method, of class BufferMemory.
     */
    @Test
    public void testGetOffsetInPage() throws Exception {
        System.out.println("getOffsetInPage");
        BufferMemory instance = new BufferMemory();
        long expResult = 0L;
        long result = instance.getOffsetInPage();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
       // fail("The test case is a prototype.");
    }

    /**
     * Test of getPageOffset method, of class BufferMemory.
     */
    @Test
    public void testGetPageOffset() throws Exception {
        System.out.println("getPageOffset");
        long pageindex = 0L;
        BufferMemory instance = new BufferMemory();
        long expResult = 0L;
        long result = instance.getPageOffset(pageindex);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of computModifiedPageIndexes method, of class BufferMemory.
     */
    @Test
    public void testComputModifiedPageIndexes() throws Exception {
        System.out.println("computModifiedPageIndexes");
        long offset = 0L;
        long length = 0L;
        BufferMemory instance = new BufferMemory();
        instance.computModifiedPageIndexes(offset, length);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of release method, of class BufferMemory.
     */
    @Test
    public void testRelease() throws Exception {
        System.out.println("release");
        BufferMemory instance = new BufferMemory();
        instance.release();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    @Test
    public void testReadAnWrite() throws Exception{
        BufferMemory instance = new BufferMemory();
        String s = "Hello World!!!!";
        instance.write(s.getBytes());

        //cache.seek(0);
        IPage p = instance.readPage(0);
        String x = new String(p.getBytes(), 0, (int)p.getSize());
        System.out.println("read data: '" + x + "'");

        assertEquals(s.substring(0, 8), x);


        
    }

}