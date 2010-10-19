/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */

public class CanalExponencial extends Channel {
    
    double media;
    
    CanalExponencial (double t) {
        media = t;
    }

    CanalExponencial () {
    }    
    
    public void setMedia(String dt) {
            media = Float.parseFloat(dt);
    }
    
    int atraso() {
        Randomico x = new Randomico();
        return (int) (x.expntl(media)+media);
    }
    
    boolean status() {
        return true;
    }
    
}