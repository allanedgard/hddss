package br.ufba.lasid.jds.prototyping.hddss;

public interface IClock {
    public long value();
    public long tickValue();
    public void adjustValue(long v);
    public void adjustTickValue(long v);
    public void adjustCorrection(long c);

}
