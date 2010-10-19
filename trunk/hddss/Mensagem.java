/*
 * Mensagem.java
 *
 * Created on 22 de Julho de 2008, 05:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author root
 */

public final class Mensagem  implements Comparable<Mensagem> {
    
    int remetente;
    int relayFrom;
    int relayTo;
    int destinatario;
    int tipo;
    int relogioLogico;
    int relogioFisico;
    int tempoRecepcao;
    int hops;
    Object conteudo;
    boolean payload;
     
    
    /** Creates a new instance of Mensagem */
    public Mensagem(int r, int d, int t, int rL, int rF, Object c) {
        remetente = r;
        destinatario = d;
        tipo = t;
        hops =0;
        relogioLogico = rL;
        relogioFisico = rF;
        conteudo = c;
        relayFrom = -1;
        relayTo = -1;
        payload = false;
    }
    
    public String getId() {
        return "p"+remetente+" para p"+destinatario+" em "+relogioFisico;
    }

    @Override public boolean equals( Object aThat ) {
     if ( this == aThat ) return true;
     if ( !(aThat instanceof Mensagem) ) return false;

     return (
                (this.destinatario== ((Mensagem)aThat).destinatario) &&
                (this.remetente==((Mensagem)aThat).remetente) &&
                (this.relogioLogico==((Mensagem)aThat).relogioLogico) &&
                (this.relogioFisico==((Mensagem)aThat).relogioFisico) &&
                (this.tipo==((Mensagem)aThat).tipo) &&
                (this.conteudo==((Mensagem)aThat).conteudo) );
   }

   @Override public int hashCode() {
     int result = HashCodeUtil.SEED;
     result = HashCodeUtil.hash( result,  this.relogioLogico);
     result = HashCodeUtil.hash( result,  this.remetente);
     return result;
   }



    public int compareTo( Mensagem aThat ) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

    if ( this.relogioLogico == this.relogioLogico ) return EQUAL;

    if (this.relogioLogico < aThat.relogioLogico)
        return BEFORE;
    else
    if (this.relogioLogico > aThat.relogioLogico)
        return AFTER;
    else
    if (this.remetente < aThat.remetente)
        return BEFORE;
    else
    if (this.remetente > aThat.remetente)
        return AFTER;
    else
        return EQUAL;
  }

  @Override
    public String toString() {
                return ""+
                this.remetente+"; "+
                this.destinatario+"; "+
                this.relogioFisico+"; "+
                this.relogioLogico+"; "+
                this.tipo+"; "+
                this.conteudo;
  }

}
