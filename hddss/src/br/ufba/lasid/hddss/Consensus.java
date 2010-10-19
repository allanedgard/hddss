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
    int numero;
    private int round;
    Object estimado;
    Object rec;
    boolean ativo;
    boolean noneREC;
    boolean atingiuQuorum;
    IntegerSet quorum;
    
    Consensus (int n, int r, Object e) {
        numero = n;
        round = r;
        estimado = e;
        ativo = true;
        noneREC = false;
        atingiuQuorum = false;
        quorum = new IntegerSet();
    }
    
    void alteraRound(int r) {
        round = r;
        rec = null;
        noneREC = false;
        atingiuQuorum = false;
        quorum.limpa();
    }
    
    int getRound() {
        return round;
    }
    
    @Override  
    public String toString() {
        return "Consenso numero: "+numero+", round: "+round+", est: " + estimado
                + ", rec: "+rec;
    }
    
}
