/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrador
 */
public class T1 {

   public static void main(String[] args) {
        java.util.ArrayList x = new java.util.ArrayList();
        Mensagem m1 = new Mensagem(1, 0,0, 20, 0, 0);
        Mensagem m2 = new Mensagem(1, 0,0, 10, 0, 0);
        Mensagem m3 = new Mensagem(0, 0,0, 20, 0, 0);
        x.add(m1);
        x.add(m3);
        x.add(m2);
        if ( m1.compareTo(m3)==1 )
            System.out.println("maior");
        java.util.Collections.sort(x, new java.util.Comparator() {
             public int compare(Object o1, Object o2) {
                 Mensagem msg1 = (Mensagem) o1;
                 Mensagem msg2 = (Mensagem) o2;
                 return     (msg1.relogioLogico - msg2.relogioLogico) != 0 ?
                            (msg1.relogioLogico - msg2.relogioLogico) :
                            (msg1.remetente - msg2.remetente);

             }
        });

        for (int i = 0; i<x.size();i++)
            System.out.println(x.get(i));
    }

}
