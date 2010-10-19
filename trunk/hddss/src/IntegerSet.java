/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class IntegerSet  implements Comparable<IntegerSet> {
    
    private java.util.ArrayList c;
    
    
    IntegerSet() {
        c = new java.util.ArrayList();
    }
    
    @Override public boolean equals( Object aThat ) {
     if ( this == aThat ) return true;
     if ( !(aThat instanceof IntegerSet) ) return false;

     IntegerSet that = (IntegerSet)aThat;
     return
       ( this.interseccao(that).tamanho()==this.tamanho() );
   }
    
   @Override public int hashCode() {
     int result = HashCodeUtil.SEED;
     for (int i =0;i<this.tamanho() ;i++) 
        result = HashCodeUtil.hash( result,  ((Integer) this.c.get(i)).intValue() );
     return result;
   }

   
    

    public int compareTo( IntegerSet aThat ) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

    //this optimization is usually worthwhile, and can
    //always be added
    if ( this == aThat ) return EQUAL;

    //primitive numbers follow this form
    if (this.tamanho() < aThat.tamanho()) return BEFORE;
    if (this.tamanho() > aThat.tamanho()) return AFTER;

    int s1, s2;
    s1 = 0;
    s2 = 0;
    for (int i =0;i<this.tamanho() ;i++) {
        s1 += ((Integer) this.c.get(i)).intValue();
        s2 += ((Integer) aThat.c.get(i)).intValue();
    }
    
    if (s1 > s2) return AFTER;
    
    if (s2 > s1) return BEFORE;
    

    //all comparisons have yielded equality
    //verify that compareTo is consistent with equals (optional)
    assert this.equals(aThat) : "compareTo inconsistent with equals.";

    return EQUAL;
  }

    
    void adiciona(int v) {
        if (!c.contains(v))
            c.add(v);
    }

    void limpa() {
        c.clear();
    }
    
    void remove(int v) {
        for (int i=0;i<c.size();i++)
            if (c.get(i).equals(v)) 
                c.remove(i);        
    }
    
    boolean existe(int v) {
        for (int i=0;i<c.size();i++)
            if (c.get(i).equals(v)) 
                return true;
        return false;
    }

    
    int minimo() {
        int v = Integer.MAX_VALUE;
        for (int i=0;i<c.size();i++)
            if ( ((Integer) c.get(i)).intValue() <v)
                v=((Integer) c.get(i)).intValue();
        return v;
    }
    
    int tamanho () {
        return  c.size();
    }

    @Override  
    public String toString() {
        String ret;
        ret="";
        for (int i =0;i<c.size();i++)
            ret = ret+" "+c.get(i);
        return "{"+ret+"}";
                
    }    
    
    IntegerSet interseccao(IntegerSet x) {
        IntegerSet y = new IntegerSet();
        for (int i=0; i<x.c.size(); i++) {
            int v = ((Integer) x.c.get(i)).intValue();
            if (this.existe(v))
               y.adiciona(v);
        }
        return y;
    }
    
   void adiciona(IntegerSet x) {
        for (int i=0; i<x.c.size(); i++) {
            int v = ((Integer) x.c.get(i)).intValue();
            if (!this.existe(v))
               this.adiciona(v);
        }
    }
    
   void remove(IntegerSet x) {
        for (int i=0; i<x.c.size(); i++) {
            int v = ((Integer) x.c.get(i)).intValue();
            this.remove(v);
        }
    }
    
}

