
import java.util.Hashtable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aliriosa
 */
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
