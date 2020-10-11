package game.campanha;

import javax.swing.ImageIcon;


/**
 * Classe filha da classe Item, representação do item Restaurar Defesa no jogo
 * @author Guilherme
 */
public class ItemRestaurarDefesa extends Item{
    
    public ItemRestaurarDefesa(){
        this.setImag(new ImageIcon(getClass().getClassLoader().getResource("imagCAMP/itemColete.png")));
    }        
    
    /**
     * Método abstrato de item, para item Restaura Defesa, ativa o metodo restaurarDefesa da Classe personagem
     * @param person Recebe um personagem para ativa o método restaurarDefesa
     */
    @Override
    public void ativar(Personagem person){
        person.restaurarDefesa();
    }
    
}
