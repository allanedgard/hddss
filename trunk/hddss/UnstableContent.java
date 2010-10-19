public final class UnstableContent {
    
    int bloco;
    java.util.ArrayList conteudo;
    int id; 
    ConjuntoInteiro down;
    ConjuntoInteiro visaoProposta;
    public UnstableContent(int b, java.util.ArrayList c, int p, ConjuntoInteiro d) {
        bloco = b;
        conteudo = c;
        id = p;
        down = d;
        visaoProposta = new ConjuntoInteiro();
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
