package br.ufba.lasid.jds.prototyping.hddss;

public class NetworkZeroDelay extends Network{

    @Override
    double delay(Message m) {
        return 0.0;
    }

}
