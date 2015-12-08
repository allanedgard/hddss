/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.instances.pbft.examples.fspbft;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliriosa
 */
public class FileSystem implements IFileSystem{

    Hashtable<String, Object> fps = new Hashtable<String, Object>();

    public synchronized long fopen(String filename, Mode mode) {
        try {

            BufferedReader br = new BufferedReader(new FileReader(filename));
            fps.put(filename, br);
            

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    public long fclose(long fp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long fprint(String text, long fp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String fgets(long fp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long fseek(long fp, long offset, long origin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public enum OPERATION{
        FOPEN, FCLOSE, FPRINT, FGESTS
        
    }

    public static String OP1 = "op1";
    public static String OP2 = "op2";
    public static String OP3 = "op3";
    public static String RESULT = "result";



}
