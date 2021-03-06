package br.ufba.lasid.jds.prototyping.hddss;

import br.ufba.lasid.jds.prototyping.hddss.TextConsole;
import java.io.*;
import java.awt.Frame;
import java.awt.FileDialog;

import java.util.Enumeration;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.RMainLoopCallbacks;

/*
 *    install.packages("rJava")
 *    include at PATH the R bin directory and i386 or x64 subdiretory
 * 
 */

public class IntegrationR {

    private Rengine re;
    static IntegrationR IntR = null;

    private IntegrationR() {

        if (!Rengine.versionCheck()) {
	    System.err.println("** Version mismatch - Java files don't match library version for R.");
	    System.exit(1);
	}
	String[] args = new String[0];
            re = new Rengine(args, false, new TextConsole());
        
        if (!re.waitForR()) {
            System.out.println("Cannot load R");
            return;
        }

    }
    
    public synchronized static IntegrationR getInstance() {  
      if (IntR == null) {  
         IntR = new IntegrationR();  
      }  
      return IntR;  
   }  

       
    void end() {
        try {
            System.out.println("finishing...");
            re.eval("q(save=\"no\")");
            re.end();
        } catch (Exception e) {
			System.out.println("EX:"+e);
			e.printStackTrace();
	}


    }


double evaluateDouble(String parse) {
            REXP a;
            double r;
            r = 0;
            try {
                 a = re.eval(parse);
                 r = a.asDouble();
            } catch (Exception e) {
			System.out.println("EX:"+e);
			e.printStackTrace();
            }
    return r;
    }   

double[] evaluateDoubleArray(String parse) {
            REXP a;
            double[] r;
            r = new double[0];
            try {
                 a = re.eval(parse);
                 r = a.asDoubleArray();
            } catch (Exception e) {
			System.out.println("EX:"+e);
			e.printStackTrace();
            }
    return r;
    }

private void assignDoubleArray(String val, double[] num) {
            try {
                 re.assign(val, num);
            } catch (Exception e) {
			System.out.println("EX:"+e);
			e.printStackTrace();
            }
    }


public static void main(String[] args) {
	IntegrationR x = new IntegrationR();
        // System.out.println(x.re.eval("citation()"));
        double r1[] = { 1.2, 32.4, 2.3, 5.4};
        x.assignDoubleArray("r1", r1);
        System.out.print(x.evaluateDouble("mean(r1)"));
        /* double a[] = x.evaluateDoubleArray("rnorm(20, mean = 40,  sd = 9 )");
        
        for (int i=0;i<a.length;i++) {
            System.out.println(a[i]);
            }
         * 
         */
        x.end();
    }

}
