package br.ufba.lasid.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aliriosa
 */
public interface Controller {
    public double sense();
    public void control();
    public void actuate(double value);
}
