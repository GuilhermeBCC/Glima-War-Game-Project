package game.campanha;

import javax.swing.ImageIcon;
/**
 * Classe filha da classe Item, representação do item Completar Vida no jogo
 * @author Guilherme
 */
public class ItemCompletarVida extends Item{
    
    public ItemCompletarVida(){
        this.setImag(new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/itemCura.png")));
    }    
    
    /**
     * Método abstrato de item, para item Completar Vida, ativa o metodo completarVida da Classe personagem
     * @param person Recebe um personagem para ativa o método restaurarDefesa
     */
    @Override
    public void ativar(Personagem person){
        person.completarVida();
    }  
    
}
