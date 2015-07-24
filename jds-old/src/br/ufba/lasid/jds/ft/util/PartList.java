/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.ft.util;

import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class PartList extends ArrayList<PartTree.PartEntry>{

    @Override
    public boolean equals(Object o) {
        PartList other = (PartList) o;
        PartList me    = this;

        if(!(other != null && me.size() == other.size())){
            return false;
        }

        for(int s = 0; s < me.size() ; s++){
            PartTree.PartEntry mentry = me.get(s);
            PartTree.PartEntry oentry = other.get(s);

            if((mentry != null && oentry == null) || (mentry == null && oentry != null)){
                return false;
            }
            
            boolean ok = true;

            if(mentry != null && oentry != null){
                ok = ok && mentry.getPartLevel() == oentry.getPartLevel();
                ok = ok && mentry.getPartIndex() == oentry.getPartIndex();
                ok = ok && mentry.getPartCheckpoint() == oentry.getPartCheckpoint();
                ok = ok && mentry.getDigest().equals(oentry.getDigest());

                if(!ok){
                    return false;
                }
            }
        }
        
        return true;
    }

}
