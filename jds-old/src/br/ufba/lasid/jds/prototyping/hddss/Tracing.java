/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.lasid.jds.prototyping.hddss;

public class Tracing {

    double [] X;
    String filename;
    char sep;
    Randomize R1;
    int col;
    int next;
    boolean change;
    
    public Tracing() {
        R1 = new Randomize();
        sep = ';';
        col = 1;
        next = 0;
        change = false;
        filename = "";
    }
    
    public void setFilename(String fn) {
        if (!filename.equals(fn)) {
            filename = fn;
            change = true;
        }
    }
    
    public void setSep(char sp) {
        if (sep!=sp) {
            sep = sp;
            change = true;
        }
    }
    
    public void setColumn(int c) {
        if (col != c) {
                col = c;
                change = true;
        }
    }    
    
    void initialize() {
        String command;
        command = "read.csv(\""+filename+"\", header=FALSE, sep=\""+sep+"\")$V"+col+"";
        //System.out.println(command);
        X = R1.R1(command);
        next = 0;
        change = false;
        //X= new double[1];
        //X = R1.R1("rnorm(3, mean=10, sd=5)");
        //System.out.println(X==null);
    }
    
    double getValue() {
        double n;
        if ((X == null) || change)
            initialize();
        n = X[next];
        next = (next + 1) % X.length;
        return n;
    }
    
}
