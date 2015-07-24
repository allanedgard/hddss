/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.hdf.util;

import br.ufba.lasid.hdf.connectors.IConnector;
import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class ConnectorList extends ArrayList<IConnector>{

    public void start(){
        for(IConnector connector : this){
            connector.start();
        }
    }
    
    public void disconnect(){
        for(IConnector connector : this){
            connector.disconnect();
            
        }
    }

    public void stop(){
        for(IConnector connector : this){
            connector.stop();

        }
    }

}
