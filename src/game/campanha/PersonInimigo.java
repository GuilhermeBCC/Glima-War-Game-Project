package game.campanha;


/**
 * Classe filha da classe Personagem, representação do personagem Inimigos no jogo
 * @author Guilherme
 */
public class PersonInimigo extends Personagem {
    
    int posX=0,posY=0;
    
    PersonInimigo(){
        this.setTempDeAtq(2000);
        this.setAlcance(60);
        this.setAtaque(40);
        this.setDefesa(20);
        this.setVelocidade(2);
    }    
    /**
     * Retorna a posição X do objeto personagem
     * @return Retorna a variável posX do objeto personinimigo
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Atualiza a posição X do objeto personagem
     * @param posX Variável para atualizar a variável posX do objeto personInimigo
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * Retorna a posição Y do objeto personagem
     * @return Retorna a variável posY do objeto personinimigo
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Retorna a posição Y do objeto personagem
     * @param posY Variável para atualizar a variável posY do objeto personInimigo
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }        
    
}
