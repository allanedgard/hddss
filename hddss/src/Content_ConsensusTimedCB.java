/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class Content_ConsensusTimedCB implements Comparable<Content_ConsensusTimedCB> {
    java.util.ArrayList UnstableMensagensEnviadas;
    IntegerSet down;
    IntegerSet live;
    IntegerSet uncertain;
    
    Content_ConsensusTimedCB() {
        
    }
    
     @Override public boolean equals( Object aThat ) {
     if ( this == aThat ) return true;
     if ( !(aThat instanceof Content_ConsensusTimedCB) ) return false;

     Content_ConsensusTimedCB that = (Content_ConsensusTimedCB)aThat;
     return
       ( 
            this.down.equals(that.down) && this.live.equals(that.live) &&
            this.uncertain.equals(that.uncertain) &&
            this.UnstableMensagensEnviadas.equals(that.UnstableMensagensEnviadas)
       );
   }

    
   @Override public int hashCode() {
     int result = HashCodeUtil.SEED;
     result = HashCodeUtil.hash( result, this.down.hashCode() );
     result = HashCodeUtil.hash( result, this.live.hashCode() );
     result = HashCodeUtil.hash( result, this.uncertain.hashCode() );
     result = HashCodeUtil.hash( result, this.UnstableMensagensEnviadas.hashCode() );
     return result;
   }
     
    public int compareTo( Content_ConsensusTimedCB aThat ) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

    //this optimization is usually worthwhile, and can
    //always be added
    if ( this == aThat ) return EQUAL;

    //primitive numbers follow this form
    if (this.hashCode() < aThat.hashCode()) return BEFORE;
    if (this.hashCode() > aThat.hashCode()) return AFTER;

    //all comparisons have yielded equality
    //verify that compareTo is consistent with equals (optional)
    assert this.equals(aThat) : "compareTo inconsistent with equals.";

    return EQUAL;
  }
    
            
}
