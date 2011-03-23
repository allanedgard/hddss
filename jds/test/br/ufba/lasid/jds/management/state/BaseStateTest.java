/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.state;

import br.ufba.lasid.jds.management.trash.state.BaseStateManager;
import br.ufba.lasid.jds.management.trash.state.BaseState;
import br.ufba.lasid.jds.management.memory.BufferMemory;
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
public class BaseStateTest {

    BaseState instance = null;
    
    public BaseStateTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        try{
        instance = new BaseState(
                        new BaseStateManager(),
                        new BufferMemory()
                    );
        }catch(Exception except){
            except.printStackTrace();
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of get method, of class BaseState.
     */
    @Test
    public void testPutAndGet() throws Exception {

        System.out.println("PutAndGet");
        
        Object variableID = null;
        Object expResult = null;
        Object result = instance.get(variableID);

        assertEquals(expResult, result);
         
        instance.put("X", "Alirio Sá");

        String X = (String)instance.get("X");

        instance.put("Y", 32);

         X = (String)instance.get("X");
        assertEquals(X, "Alirio Sá");

        int Y = (Integer) instance.get("Y");

        assertEquals(Y, 32);               

        
    }

    /**
     * Test of remove method, of class BaseState.
     */
    @Test
    public void testRemove() throws Exception {
        instance.remove("X");
        Object result = instance.get("X");
        assertNull(result);
    }

    /**
     * Test of write method, of class BaseState.
     */
    @Test
    public void testWriteAndRead() throws Exception {
        System.out.println("write");
        
        byte[] wb = "Test Alo test".getBytes();
        int offset = 0;
        int length = wb.length;
        long pos = instance.getMemory().getCurrentOffset();
        instance.write(wb, offset, length);

        instance.getMemory().seek(pos);
        byte[] rb = new byte[length];
        instance.read(rb, offset, length);

        String s = new String(rb);

        assertEquals("Test Alo test", s);
    }



}