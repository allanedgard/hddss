package br.ufba.lasid.hddss;

public class ChannelLogNormal extends Channel {

    double mean;
    double std;
    double min;
    Randomize x;
    ChannelLogNormal (double t, double s, double m) {
        mean = t;
        std = s;
        min = m;
        x = new Randomize();
    }

    ChannelLogNormal () {
        x = new Randomize();
    }

    public void setMean(String dt) {
            mean = Float.parseFloat(dt);
    }

    public void setMinDelay(String dt) {
            min = Float.parseFloat(dt);
    }

    public void setStd(String dt) {
            std = Float.parseFloat(dt);
    }


    int delay() {
        
        return (int) ( min + x.normal(mean,std) );
    }

    boolean status() {
        return true;
    }

}