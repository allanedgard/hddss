/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//import org.apache.java.util.Configurations;
/**
 *
 * @author Administrador
 */


public class Teste {
Configurations config;

    Teste() {
        String args[] = new String[1];
        args[0] = "C:\\config.txt";
        config = getConfig(args);

    }

    public static void main(String[] args) {
        Teste t = new Teste();
        t.configura();

    }

    public void configura() {
        String[] classNames =
            config.getStringArray("canal");
        if (classNames == null) {
            classNames = new String[0];
        }
        for (int i = 0; i < classNames.length; i++) {
            System.out.println(classNames[i]);
        }

        java.util.Iterator x = config.getKeys("canal.");


        while (x.hasNext()) {
            String a = (String) x.next();
            if (a.equalsIgnoreCase("canal.ModeloFalha"))  {
                System.out.println("Modelo de Falhas = "+config.getString(a));
            };
            if (a.startsWith("canal.ModeloFalha.")) {
                System.out.println(a.substring(18));
            }

            System.out.println(a+" = "+config.getString(a));
        }

        /*
        java.util.Properties po = config.getProperties("canal");
        java.util.Enumeration elementos = po.keys();
        while(elementos.hasMoreElements()){
            System.out.println((String) elementos.nextElement());
            
        }
        */

    }


    public static Configurations getConfig(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: program_name configuration_file");
            System.exit(2);
        }

        ExtendedProperties ep;
        try {
            ep = new ExtendedProperties(args[0]);
        } catch (java.io.IOException ex) {
            throw new RuntimeException("Configuration file " + args[0]
                                       + " unreadable!");
        }
        return new Configurations(ep);
    }



}
