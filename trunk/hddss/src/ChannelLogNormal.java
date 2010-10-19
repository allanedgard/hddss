public class ChannelLogNormal extends Channel {

    double media;
    double std;
    double min;
    Randomize x;
    ChannelLogNormal (double t, double s, double m) {
        media = t;
        std = s;
        min = m;
        x = new Randomize();
    }

    ChannelLogNormal () {
        x = new Randomize();
    }

    public void setMedia(String dt) {
            media = Float.parseFloat(dt);
    }

    public void setMinDelay(String dt) {
            min = Float.parseFloat(dt);
    }

    public void setStd(String dt) {
            std = Float.parseFloat(dt);
    }


    int atraso() {
        
        return (int) ( min + x.normal(media,std) );
    }

    boolean status() {
        return true;
    }

}