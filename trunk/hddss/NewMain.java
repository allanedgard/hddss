/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ConjuntoInteiro x = new ConjuntoInteiro();
        x.adiciona(1);
        x.adiciona(2);
        ConjuntoInteiro y = new ConjuntoInteiro();
        y.adiciona(1);
        y.adiciona(2);
        System.out.println(x.equals(y));
        
    }

}
