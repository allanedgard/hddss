/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class CanalDeterministico extends Channel {
    
    int delay;
    
    CanalDeterministico (int t) {
        delay = t;
    }
    
    int atraso() {
        return delay;
    }
    
    boolean status() {
        return true;
    }
}
