/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aliriosa
 */
public class RedeDeterministica extends Network{

    @Override
    double delay() {
        return processingTime;
    }

}
