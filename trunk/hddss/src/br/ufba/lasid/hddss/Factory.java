package br.ufba.lasid.hddss;


import java.lang.reflect.Method;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aliriosa
 */
public class Factory {

    public static Configurations config = null;
    
    public static Object create(String _class, String _defaultClass) throws Exception{
            return Class.forName(config.getString(_class, _defaultClass)).newInstance();
    }
    
    public static void setup(Object obj, String TAG){
        java.util.Iterator properties = config.getKeys(TAG + ".");

        while (properties.hasNext()) {
            String value = (String) properties.next();

            /*this keeps the compatibility with previous versions*/
//            System.out.println(obj instanceof  Agent);
//            System.out.println(value);
            if(isInstanceof(obj, Agent.class) && value.contains("FaultModel")){
                  ((Agent)obj).infra.setFaultModel(config.getString(value));
            }else{
                setProperty(obj, value.substring(TAG.length() + 1), config.getString(value));
            }
        }        
    }
    
    public static void setup(Object obj, String TAG, int index){
        setup(obj, TAG +"[" + index +"]");
    }

    public static void setProperty(Object obj, String property, String value) {
        try {
            String _name = "set"+property;
            Class _class = obj.getClass();
            Class args[] = new Class[1];
            args[0] = String.class;
            Method method = _class.getMethod(_name, args);
            method.invoke(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
