/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */

public class ChannelExponential extends Channel {
    
    double media;
    
    ChannelExponential (double t) {
        media = t;
    }

    ChannelExponential () {
    }    
    
    public void setMedia(String dt) {
            media = Float.parseFloat(dt);
    }
    
    int atraso() {
        Randomize x = new Randomize();
        return (int) (x.expntl(media)+media);
    }
    
    boolean status() {
        return true;
    }
    
}