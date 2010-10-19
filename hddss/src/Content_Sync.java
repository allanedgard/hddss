/*
 * Sinc_Conteudo.java
 *
 * Created on 8 de Dezembro de 2008, 13:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author root
 */
public class Content_Sync {
    double inicio;
    double atual;
    /** Creates a new instance of Sinc_Conteudo */
    public Content_Sync(double i) {
        inicio = i;
    }
    
    public String toString() {
        return "i = " + Double.toString(inicio) + " a = " + Double.toString(atual);
    }
    
}
