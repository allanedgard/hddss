/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss;

import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class RuntimeVariables implements RuntimeSupport{

    Hashtable<String, Value> properties = new Hashtable<String, Value>();

    public Value get(Variable variable) {
        return get(variable.toString());
    }

    public <U> void set(Variable variable, U value){
        set(variable.toString(), value);
    }
    public Value get(String name) {
        return properties.get(name);
    }

    public <U> void set(String name, U value) {
        properties.put(name, new Value(value));
    }

    public void perform(RuntimeContainer rs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void ok() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
