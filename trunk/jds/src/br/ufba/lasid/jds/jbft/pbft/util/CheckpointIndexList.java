/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.util;

import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class CheckpointIndexList extends ArrayList<CheckpointIndex>{

    @Override
    public boolean equals(Object o) {
        CheckpointIndexList other = (CheckpointIndexList) o;

        if(other == null){
            return false;
        }

        if(other.size() != this.size()){
            return false;
        }

        for(int i = 0; i < other.size(); i++){
            String otherDigest = other.get(i).getDigest();
            String thisDigest = this.get(i).getDigest();


            if(otherDigest != null && thisDigest != null){
                if(!otherDigest.equals(thisDigest)){
                    return false;
                }                
            }

            if((otherDigest != null && thisDigest == null) || (otherDigest == null && thisDigest != null)){
                return false;
            }
            
            Long otherSEQ = other.get(i).getSequenceNumber();
            Long thisSEQ = this.get(i).getSequenceNumber();

            if(otherSEQ != null && thisSEQ != null){
                if(!otherSEQ.equals(thisSEQ)){
                    return false;
                }
            }

            if((otherSEQ != null && thisSEQ == null) || (otherSEQ == null && thisSEQ != null)){
                return false;
            }
        }

        return true;
    }

    public boolean hasNext(){
        return curr+1 < size();
    }

    public boolean hasPrevious(){
        return curr-1 >= 0;
    }

    private int curr = -1;
    
    public CheckpointIndex next(){
        curr++;
        try{
            return get(curr);
        }catch(Exception except){
            
        }

        return null;
    }

    public CheckpointIndex previous(){
        curr --;
        try{
            return get(curr);
        }catch(Exception except){
            
        }

        return null;
    }
    public CheckpointIndex getCurrent(){
        try{
            return get(curr);
        }catch(Exception except){
            
        }

        return null;
    }
    
    public void restart(){
        curr = -1;
    }

}
