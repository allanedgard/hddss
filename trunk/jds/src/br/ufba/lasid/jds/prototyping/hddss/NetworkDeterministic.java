package br.ufba.lasid.jds.prototyping.hddss;

public class NetworkDeterministic extends Network{
    double processingTime;
    public void setProcessingTime(String delay){
       processingTime = Double.parseDouble(delay);
    }
    @Override
    double delay(Message m) {
        return processingTime;
    }

}
