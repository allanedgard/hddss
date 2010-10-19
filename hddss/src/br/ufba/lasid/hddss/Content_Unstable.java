package br.ufba.lasid.hddss;

public final class Content_Unstable {
    
    int bloco;
    java.util.ArrayList conteudo;
    int id; 
    IntegerSet down;
    IntegerSet visaoProposta;
    public Content_Unstable(int b, java.util.ArrayList c, int p, IntegerSet d) {
        bloco = b;
        conteudo = c;
        id = p;
        down = d;
        visaoProposta = new IntegerSet();
    }
    
    int tamanho() {
        return conteudo.size();
    }
    
    @Override  
    public String toString() {
        return "uC bloco: "+bloco+", msgs: "+conteudo.size()+", view: " + visaoProposta
                +", down: " + down;
                
    }
    
}
