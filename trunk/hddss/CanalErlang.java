/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ALLAN
 */
public class CanalErlang {
    double media;
    double desvio;
    
    CanalErlang (double t, double s) {
        media = t;
        desvio = s;
    }
    
    int atraso() {
        Randomico x = new Randomico();
        return (int) (x.erlang(media, desvio));
    }
    
    boolean status() {
        return true;
    }
    
}
