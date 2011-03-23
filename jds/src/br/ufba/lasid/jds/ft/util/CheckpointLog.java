/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.ft.util;

import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class CheckpointLog extends Hashtable<Long, CheckpointLogEntry>{

    public CheckpointLogEntry getBiggest(){
        long biggest = -1;
        for(Long key : keySet()){
            if(key.longValue() > biggest){
                biggest = key;
            }
        }

        if(biggest >= 0){
            return get(biggest);
        }

        return null;

    }

}
