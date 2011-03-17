/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

/**
 *
 * @author aliriosa
 */
public final class XObject {

    /**
     * Convert a object in a array of bytes.
     * @param obj -- the object.
     * @return a array of bytes.
     * @throws IOException
     */

    public static byte[] objectToByteArray(Object obj) throws IOException {

        if(obj == null) return null;

        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bas);

        oos.writeObject(obj);
        oos.flush();
        oos.close();

        byte[] obyte = bas.toByteArray();
        bas.close();

        return obyte;
    }

    /**
     * Convert a array of bytes in a object.
     * @param ibyte -- the array of bytes
     * @return -- a object.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object byteArrayToObject(byte[] ibyte)
            throws IOException, ClassNotFoundException
    {

        if(!(ibyte != null && ibyte.length != 0)) return null;

        ByteArrayInputStream bas = new ByteArrayInputStream(ibyte);
        ObjectInputStream ois = new ObjectInputStream(bas);
        Object obj = ois.readObject();
        ois.close();
        bas.close();

        return obj;

    }

    private static char[] hex = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * Convert um array of bytes in a string in Hexadecimal format
     * @param bytes -- the array of bytes
     * @return a string in hex format
     */
    public static String byteArrayToHexString(byte[] bytes){

        return new BigInteger(bytes).toString(16);
    }

    public static byte[] hexStringToByteArray(String hexString){

        return (new BigInteger(hexString, 16)).toByteArray();

    }
    

}
