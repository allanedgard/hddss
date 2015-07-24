package br.ufba.lasid.jds.prototyping.hddss.instances;

public interface Controller {
    public double sense();
    public void control();
    public void actuate(double value);
}
