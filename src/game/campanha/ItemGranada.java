package game.campanha;

import javax.swing.ImageIcon;

/**
 * Classe filha da classe Item, representação do item Granada no jogo
 * @author Guilherme
 */
public class ItemGranada extends Item {
        
    public ItemGranada(){
        this.setImag(new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/itemGranada.png")));
    }
    
    /**
     * Método abstrato de item, para item Granada, ativa o metodo setMatar da Classe personagem
     * @param person Recebe um personagem para ativa o método setMatar
     */
    @Override
    public void ativar(Personagem person){
        person.setMatar();
    }  
    
}
