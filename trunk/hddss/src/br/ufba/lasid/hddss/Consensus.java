package br.ufba.lasid.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class Consensus {
    int number;
    private int round;
    Object estimated;
    Object rec;
    boolean active;
    boolean noneREC;
    boolean gotQuorum;
    IntegerSet quorum;
    
    Consensus (int n, int r, Object e) {
        number = n;
        round = r;
        estimated = e;
        active = true;
        noneREC = false;
        gotQuorum = false;
        quorum = new IntegerSet();
    }
    
    void alteraRound(int r) {
        round = r;
        rec = null;
        noneREC = false;
        gotQuorum = false;
        quorum.clean();
    }
    
    int getRound() {
        return round;
    }
    
    @Override  
    public String toString() {
        return "Consensus number: "+number+", round: "+round+", est: " + estimated
                + ", rec: "+rec;
    }
    
}
