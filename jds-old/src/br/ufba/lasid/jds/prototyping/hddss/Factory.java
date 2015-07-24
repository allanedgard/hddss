package br.ufba.lasid.jds.prototyping.hddss;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Factory {

    public static Configurations config = null;
    
    public static Object create(String _class, String _defaultClass) throws Exception{
            return Class.forName(config.getString(_class, _defaultClass)).newInstance();
    }
    
    public static void setup(Object obj, String TAG){
        java.util.Iterator properties = config.getKeys(TAG + ".");

        while (properties.hasNext()) {
           
            String value = (String) properties.next();
            
                /*
                 *  DEFINE AS PROPRIEDADES DO MODELO DE FALHAS
                 */
                if(isInstanceof(obj, Agent.class) && value.contains("FaultModelProperties")){
                  setProperty(((Agent)obj).getInfra().faultModel, value.substring(TAG.length() + 22), config.getString(value));
                }
                else
            
                if(isInstanceof(obj, Agent.class) && value.contains("FaultModel")){
                  ((Agent)obj).getInfra().setFaultModel(config.getString(value));
                }else{
                    try{
                        setProperty(obj, value.substring(TAG.length() + 1), config.getString(value));
                    }catch(Exception e){
                        setProperty(obj, value.substring(TAG.length() + 1), config.getVector(value, new ArrayList()).toString());
                    }
                }

        }        
    }
    
    public static void setup(Object obj, String TAG, int index){
        setup(obj, TAG +"[" + index +"]");
    }

    public static void setProperty(Object obj, String property, String value) {
        try {            
            String _name = "set"+property;
            if(hasMethod(obj, _name)){
                Class _class = obj.getClass();
                Class args[] = new Class[1];
                args[0] = String.class;
                Method method = _class.getMethod(_name, args);
                method.invoke(obj, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean hasMethod(Object obj, String mthname){

        try{
            Method[] methods = obj.getClass().getMethods();

            for(int m = 0; m < methods.length; m++){
                if(methods[m].getName().equals(mthname)){
                    return true;
                }
            }
        }catch(Exception e){}
        return false;
    }

    public static boolean isInstanceof(Object obj, Class _class){
        try {
            _class.cast(obj);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

}
