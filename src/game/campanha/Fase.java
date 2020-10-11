package game.campanha;

/**
 * Classe interface para todas a fases do jogo
 * @author Guilherme
 */
public interface Fase {            
  
    /**
    * Método que controla todos os oponentes da fase
    */       
    public void inimigos();
    
    /**
     * Método que executa a animação quando o jogador conclui a fase
     */        
    public void fimFase();    
    
}
