package br.ufba.lasid.jds.prototyping.hddss;


import java.util.Hashtable;

@SuppressWarnings("UseOfObsoleteCollectionType")
public class TSCollection extends Hashtable<Integer, Integer>{
    public int getMinimum(){
        Integer value = null;
        int min = Integer.MAX_VALUE;

        while((value = this.values().iterator().next()) != null){
            if(min > value.intValue())
                min = value.intValue();
        }

        return min;
    }
}
