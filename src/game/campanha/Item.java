package game.campanha;

import javax.swing.ImageIcon;

/**
 * Classe pai de todos os itens do jogo
 * @author Guilherme
 */
public abstract class Item {

    private ImageIcon imag = new ImageIcon();
    
    /**
     * Retorna a imagem do item
     * @return Retorna a variável imag do objeto
     */
    public ImageIcon getImag() {
        return imag;
    }

    /**
     * Atualiza a imagem do item
     * @param imag Atualiza a variável imag do objeto
     */
    public void setImag(ImageIcon imag) {
        this.imag = imag;
    }
    
    /**
     * Metodo abstrato. Utilizado para que o Personagem possa utilizar a função do Item no jogo.
     * @param person
     */
    public void ativar(Personagem person){};
    
}
